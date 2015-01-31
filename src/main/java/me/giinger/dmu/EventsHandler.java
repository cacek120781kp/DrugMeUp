package me.giinger.dmu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

@SuppressWarnings("UnusedDeclaration")
public class EventsHandler implements Listener {

    final DrugMeUp plugin;
    final PlayerHandler pHandler;

    public EventsHandler(DrugMeUp plugin, PlayerHandler pHandler) {
        this.plugin = plugin;
        this.pHandler = pHandler;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (plugin.getNoPlace().contains(p.getName())) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void playerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (plugin.getDrunk().contains(p.getName())) {
            plugin.getDrunk().remove(p.getName());
        }
        if (plugin.getOnDrugs().contains(p.getName())) {
            plugin.getOnDrugs().remove(p.getName());
        }
        if (plugin.getHeartAttack().contains(p.getName())) {
            plugin.getHeartAttack().remove(p.getName());
        }
        if (plugin.getIsJump().contains(p.getName())) {
            plugin.getIsJump().remove(p.getName());
        }
        if (plugin.getNoPlace().contains(p.getName())) {
            plugin.getNoPlace().remove(p.getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getItemInHand();
        if (item != null) {
            if (plugin.isDrug(player.getItemInHand())) {
                if (player.hasPermission("drugs.use")) {
                    if (!plugin.isMultiworld() || plugin.getWorlds().contains(player.getWorld())) {
                        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action
                                .RIGHT_CLICK_BLOCK)) {
                            Drug drug = plugin.getDrug(item);
                            if (drug.isSneak()) {
                                if (!player.isSneaking()) {
                                    return;
                                }
                            }
                            if (drug.isEdible()) {
                                // Return because edibles are handled by PlayerItemConsumeEvent
                                return;
                            }
                            pHandler.doDrug(player, item);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (plugin.getIsJump().contains((e.getEntity()).getName())) {
                if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                    if (plugin.config.getBoolean("Options.EnableJumpProtection")) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (plugin.getDrunk().contains(e.getPlayer().getName())) {
            String initial = e.getMessage();
            String end = pHandler.scramble(initial);
            e.setMessage(end);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) throws IOException {
        if (plugin.isUpdateCheck() && !plugin.isUpdateDownload()) {
            if (e.getPlayer().hasPermission("drugs.updates")
                    || e.getPlayer().isOp()) {
                String[] updateNotif = new String[4];
                updateNotif[0] = " *";
                updateNotif[1] = " * [DrugMeUp] Update Available! ";
                updateNotif[2] = " * Download it at: dev.bukkit.org/server-mods/drugmeup";
                updateNotif[3] = " *";
                for (String s : updateNotif) {
                    e.getPlayer().sendMessage(ChatColor.RED + s);
                }
            }
        } else if (plugin.isUpdateDownload()) {
            if (e.getPlayer().hasPermission("drugs.updates") || e.getPlayer().isOp()) {
                String[] updateNotif = new String[5];
                updateNotif[0] = " *";
                updateNotif[1] = " * [DrugMeUp] Update Downloaded! ";
                updateNotif[2] = " * Restart for changes to take effect!";
                updateNotif[3] = " * Check it at: dev.bukkit.org/server-mods/drugmeup";
                updateNotif[4] = " *";
                for (String s : updateNotif) {
                    e.getPlayer().sendMessage(ChatColor.RED + s);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemConsume(final PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        ItemStack i = e.getItem();
        if (plugin.isDrug(i) && i.getType() == Material.MILK_BUCKET) {
            ItemStack milk = new ItemStack(Material.MILK_BUCKET, 1);
            p.getInventory().removeItem(milk);
            e.setCancelled(true);
        }
        if (plugin.isDrug(i)) {
            Drug drug = plugin.getDrug(i);
            if (drug.isSneak() && !p.isSneaking()) {
                return;
            }
            pHandler.doDrug(p, i);
        }
    }

}
