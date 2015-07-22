package me.giinger.dmu.handlers;

import me.giinger.dmu.Drug;
import me.giinger.dmu.DrugMeUp;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
import java.util.Random;

@SuppressWarnings("UnusedDeclaration")
public class EventsHandler implements Listener {

    private final DrugMeUp plugin;
    private final ConfigHandler cHandler;
    private final PlayerHandler pHandler;
    private final DrugHandler dHandler;

    public EventsHandler(DrugMeUp plugin) {
        this.plugin = plugin;
        this.cHandler = DrugMeUp.getConfigHandler();
        this.pHandler = DrugMeUp.getPlayerHandler();
        this.dHandler = DrugMeUp.getDrugHandler();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (pHandler.getNoPlace().contains(p.getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (pHandler.getDrunk().contains(p.getName())) {
            pHandler.getDrunk().remove(p.getName());
        }
        if (pHandler.getOnDrugs().containsKey(p.getName())) {
            pHandler.getOnDrugs().remove(p.getName());
        }
        if (pHandler.getHeartAttack().contains(p.getName())) {
            pHandler.getHeartAttack().remove(p.getName());
        }
        if (pHandler.getIsJump().contains(p.getName())) {
            pHandler.getIsJump().remove(p.getName());
        }
        if (pHandler.getNoPlace().contains(p.getName())) {
            pHandler.getNoPlace().remove(p.getName());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getItemInHand();
        if (item != null) {
            if (dHandler.isDrug(item)) {
                if (player.hasPermission("drugs.use")) {
                    if (!cHandler.isMultiworld() || cHandler.getWorlds().contains(player.getWorld())) {
                        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action
                                .RIGHT_CLICK_BLOCK)) {
                            Drug drug = dHandler.getDrug(item);
                            // Check for sneak-only drug
                            if (drug.isSneak()) {
                                if (!player.isSneaking()) {
                                    return;
                                }
                            }
                            if (drug.isEdible() && player.getFoodLevel() < 20) {
                                // Return because edibles are handled by PlayerItemConsumeEvent
                                return;
                            }
                            pHandler.doDrug(player, dHandler.getDrug(item));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
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

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (pHandler.getDrunk().contains(e.getPlayer().getName())) {
            String initial = e.getMessage();
            String end = scramble(initial);
            e.setMessage(end);
        }
    }

    @EventHandler
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

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
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
            pHandler.doDrug(p, dHandler.getDrug(i));
        }
    }

    private String scramble(String word) {
        StringBuilder builder = new StringBuilder(word.length());
        boolean[] used = new boolean[word.length()];

        for (int iScramble = 0; iScramble < word.length(); iScramble++) {
            int rndIndex;
            do {
                rndIndex = new Random().nextInt(word.length());
            } while (used[rndIndex]);
            used[rndIndex] = true;
            builder.append(word.charAt(rndIndex));
        }
        return builder.toString();
    }
}