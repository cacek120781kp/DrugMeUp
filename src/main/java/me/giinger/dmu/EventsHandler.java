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
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class EventsHandler implements Listener {

    private final DrugMeUp plugin;
    private final ConfigHandler cHandler;
    private final PlayerHandler pHandler;
    private final DrugHandler dHandler;
    private ArrayList<String> noplace = new ArrayList<>();

    public EventsHandler(DrugMeUp plugin) {
        this.plugin = plugin;
        this.cHandler = plugin.getConfigHandler();
        this.pHandler = plugin.getPlayerHandler();
        this.dHandler = plugin.getDrugHandler();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (noplace.contains(p.getName())) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void playerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (pHandler.getDrunk().contains(p.getName())) {
            pHandler.getDrunk().remove(p.getName());
        }
        if (pHandler.getOnDrugs().contains(p.getName())) {
            pHandler.getOnDrugs().remove(p.getName());
        }
        if (pHandler.getHeartAttack().contains(p.getName())) {
            pHandler.getHeartAttack().remove(p.getName());
        }
        if (pHandler.getIsJump().contains(p.getName())) {
            pHandler.getIsJump().remove(p.getName());
        }
        if (noplace.contains(p.getName())) {
            noplace.remove(p.getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getItemInHand();
        if (item != null) {
            if (dHandler.isDrug(player.getItemInHand())) {
                if (player.hasPermission("drugs.use")) {
                    if (!cHandler.isMultiworld() || cHandler.getWorlds().contains(player.getWorld())) {
                        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action
                                .RIGHT_CLICK_BLOCK)) {
                            Drug drug = dHandler.getDrug(item);
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
            if (pHandler.getIsJump().contains((e.getEntity()).getName())) {
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
        if (pHandler.getDrunk().contains(e.getPlayer().getName())) {
            String initial = e.getMessage();
            String end = pHandler.scramble(initial);
            e.setMessage(end);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) throws IOException {
        if (cHandler.isUpdateCheck() && !cHandler.isUpdateDownload()) {
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
        } else if (cHandler.isUpdateDownload()) {
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
        if (dHandler.isDrug(i) && i.getType() == Material.MILK_BUCKET) {
            ItemStack milk = new ItemStack(Material.MILK_BUCKET, 1);
            p.getInventory().removeItem(milk);
            e.setCancelled(true);
        }
        if (dHandler.isDrug(i)) {
            Drug drug = dHandler.getDrug(i);
            if (drug.isSneak() && !p.isSneaking()) {
                return;
            }
            pHandler.doDrug(p, i);
        }
    }

    /* Get everyone who can't place blocks */
    public List<String> getNoPlace() {
        return noplace;
    }

}
