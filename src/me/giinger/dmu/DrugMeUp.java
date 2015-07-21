package me.giinger.dmu;

import me.giinger.dmu.events.DMUConfigReloadEvent;
import me.giinger.dmu.handlers.ConfigHandler;
import me.giinger.dmu.handlers.DrugHandler;
import me.giinger.dmu.handlers.EventsHandler;
import me.giinger.dmu.handlers.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.logging.Logger;

public class DrugMeUp extends JavaPlugin {

    public FileConfiguration config = getConfig();
    public Logger log = getLogger();
    public File file = getFile();

    private static ConfigHandler cHandler;
    private static DrugHandler dHandler;
    private static PlayerHandler pHandler;

    public void onDisable() {
        log.info("Disabled!");
    }

    public void onEnable() {
        /* Handler init */
        cHandler = new ConfigHandler(this);
        dHandler = new DrugHandler(this);
        pHandler = new PlayerHandler(this);

        /* Event Handler */
        getServer().getPluginManager().registerEvents(new EventsHandler(this), this);

        /* Configuration Setup */
        saveDefaultConfig();
        cHandler.createMaterialList();
        cHandler.configUpdate();
        config = getConfig();

        /* Gather worlds */
        if (cHandler.isMultiworld()) {
            cHandler.gatherWorlds();
        }

        /* Check for updates */
        cHandler.isUpdateCheck();

        /* Gather Drugs in Config */
        log.info(dHandler.gatherDrugs() + " Drugs Loaded!");
        log.info("Enabled!");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("drugmeup") || cmd.getName().equalsIgnoreCase("dmu")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("drugs.reload")) {
                        reloadConfig();
                        config = getConfig();
                        dHandler.clearDrugs();
                        cHandler.clearWorlds();
                        dHandler = new DrugHandler(this);
                        pHandler = new PlayerHandler(this);
                        cHandler = new ConfigHandler(this);
                        if (cHandler.isMultiworld())
                            cHandler.gatherWorlds();
                        log.info("Reloaded!");
                        sender.sendMessage(ChatColor.GREEN + "[DrugMeUp] Reloaded!");
                        log.info(dHandler.gatherDrugs() + " Drugs Loaded!");
                        Bukkit.getServer().getPluginManager().callEvent(new DMUConfigReloadEvent());
                        return true;
                    }
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("cleardrugs") ||
                        args[0].equalsIgnoreCase("cd") ||
                        args[0].equalsIgnoreCase("clear")) {
                    if (sender.hasPermission("drugs.cleardrugs")) {
                        Player p = getPlayer(args[1]);
                        if (p == null) {
                            sender.sendMessage(ChatColor.RED + "[DrugMeUp] '" + args[1] + "' is not online.");
                            return true;
                        } else {
                            for (PotionEffect potionEffect : p.getActivePotionEffects()) {
                                p.removePotionEffect(potionEffect.getType());
                            }
                            p.sendMessage(ChatColor.GREEN + "[DrugMeUp] All of your drug effects have been cleared!");
                            sender.sendMessage(ChatColor.GREEN + "[DrugMeUp] Cleared drug effects from '" + p.getName
                                    () + "'.");
                            return true;
                        }
                    } else {
                        sender.sendMessage(colorize(config.getString("Chat.Errors.NoPerms")));
                    }
                }
            } else {
                return false;
            }
        } else {
            sender.sendMessage(colorize(config.getString("Chat.Errors.NoPerms")));
        }
        return true;
    }

    /* Colorize a String */
    public String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    /* Return Player from name */
    public Player getPlayer(String name) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    public static ConfigHandler getConfigHandler() {
        return cHandler;
    }

    public static DrugHandler getDrugHandler() {
        return dHandler;
    }

    public static PlayerHandler getPlayerHandler() {
        return pHandler;
    }
}
