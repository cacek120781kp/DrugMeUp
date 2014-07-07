package me.giinger.dmu;

import me.giinger.dmu.Updater.UpdateType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DrugMeUp extends JavaPlugin {

    public HashMap<String, UUID> players = new HashMap<>();

    private PluginDescriptionFile pdfFile;
    private ArrayList<String> onDrugs = new ArrayList<>();
    private ArrayList<String> noplace = new ArrayList<>();
    private ArrayList<String> isJump = new ArrayList<>();
    private ArrayList<String> drunk = new ArrayList<>();
    private ArrayList<String> heartattack = new ArrayList<>();
    private ArrayList<String> materials = new ArrayList<>();
    private ArrayList<World> worlds = new ArrayList<>();
    private HashMap<ItemStack, Drug> drugs = new HashMap<>();
    private boolean isUpdate;
    private boolean isDownloaded;
    File configFile = new File("plugins/DrugMeUp/config.yml");
    File matList = new File("plugins/DrugMeUp/materialList.txt");
    File oldDir = new File("plugins/DrugMeUp/Old_Configs/");
    FileConfiguration config;
    private PlayerHandler playerHandler = new PlayerHandler(this);

    public void onDisable() {
        pdfFile = getDescription();
        Bukkit.getConsoleSender().sendMessage("v" + pdfFile.getVersion() + " Disabled!");
    }

    public void onEnable() {
        pdfFile = getDescription();
        Bukkit.getConsoleSender().sendMessage("v" + pdfFile.getVersion() + " Enabled!");

        getServer().getPluginManager().registerEvents(new EventsHandler(this), this);

        saveDefaultConfig();
        createMaterialList();
        try {
            configUpdate();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        initialConfigGen();

        gatherMaterials();
        gatherDrugs();

        config = getConfig();
        if (config.getBoolean("Options.AutoUpdateChecker")
                && config.getBoolean("Options.AutoUpdateDownload")) {
            isUpdate(UpdateType.NO_DOWNLOAD);
        } else if (config.getBoolean("Options.AutoUpdateDownload")) {
            isUpdate(UpdateType.DEFAULT);
        }

        if (isMultiworld()) {
            gatherWorlds();
        }
    }

    @Override
    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
    }

    public void saveDefaultConfig2() {
        saveResource("config.yml", true);
    }

    public void initialConfigGen() {
        config = getConfig();
        if (config.getInt("DO_NOT_TOUCH_2") == 0) {
            config.options()
                    .header("######### Effects #########"
                            + System.lineSeparator()
                            + "0 - Nausea (Screen Spin)"
                            + System.lineSeparator()
                            + "1 - Zoom-In & Walk Slow"
                            + System.lineSeparator()
                            + "2 - Zoom-Out & Walk Fast"
                            + System.lineSeparator()
                            + "3 - Blindness"
                            + System.lineSeparator()
                            + "4 - Hunger"
                            + System.lineSeparator()
                            + "5 - High Jump"
                            + System.lineSeparator()
                            + "6 - Sickness & Slower Hitting"
                            + System.lineSeparator()
                            + "7 - Drunk (Word Scramble)"
                            + System.lineSeparator()
                            + "8 - Instant Healing"
                            + System.lineSeparator()
                            + "9 - Regeneration"
                            + System.lineSeparator()
                            + "10 - Resistance"
                            + System.lineSeparator()
                            + "11 - Fire Resistance"
                            + System.lineSeparator()
                            + "12 - Water Breathing"
                            + System.lineSeparator()
                            + "13 - Invisibility"
                            + System.lineSeparator()
                            + "14 - Night Vision"
                            + System.lineSeparator()
                            + "15 - Poison"
                            + System.lineSeparator()
                            + "16 - Wither Poison"
                            + System.lineSeparator()
                            + "17 - Absorption (NOTE: If you use higher numbers on Absorption MaxPower, " +
                            "your screen will fill with golden hearts! :) )"
                            + System.lineSeparator()
                            + "18 - Saturation"
                            + System.lineSeparator()
                            + " ######## Negatives ########"
                            + System.lineSeparator()
                            + "0 - None"
                            + System.lineSeparator()
                            + "1 - Puke"
                            + System.lineSeparator()
                            + "2 - Catch on fire" +
                            System.lineSeparator()
                            + "3 - Heart Attack" + System.lineSeparator() + "4 - Overdose" + System.lineSeparator()
                            + "###########################");
            config.addDefault("Effects.Nausea.MaxPower", 100);
            config.addDefault("Effects.Nausea.MinPower", 10);
            config.addDefault("Effects.Nausea.MaxTime", 50);
            config.addDefault("Effects.Nausea.MinTime", 15);
            config.addDefault("Effects.SlowWalk.MaxPower", 100);
            config.addDefault("Effects.SlowWalk.MinPower", 10);
            config.addDefault("Effects.SlowWalk.MaxTime", 50);
            config.addDefault("Effects.SlowWalk.MinTime", 15);
            config.addDefault("Effects.FastWalk.MaxPower", 100);
            config.addDefault("Effects.FastWalk.MinPower", 10);
            config.addDefault("Effects.FastWalk.MaxTime", 50);
            config.addDefault("Effects.FastWalk.MinTime", 15);
            config.addDefault("Effects.Blindness.MaxPower", 1000);
            config.addDefault("Effects.Blindness.MinPower", 100);
            config.addDefault("Effects.Blindness.MaxTime", 50);
            config.addDefault("Effects.Blindness.MinTime", 15);
            config.addDefault("Effects.Hunger.MaxPower", 1000);
            config.addDefault("Effects.Hunger.MinPower", 100);
            config.addDefault("Effects.Hunger.MaxTime", 50);
            config.addDefault("Effects.Hunger.MinTime", 15);
            config.addDefault("Effects.HighJump.MaxPower", 15);
            config.addDefault("Effects.HighJump.MinPower", 1);
            config.addDefault("Effects.HighJump.MaxTime", 50);
            config.addDefault("Effects.HighJump.MinTime", 15);
            config.addDefault("Effects.SlowHit.MaxPower", 1000);
            config.addDefault("Effects.SlowHit.MinPower", 100);
            config.addDefault("Effects.SlowHit.MaxTime", 50);
            config.addDefault("Effects.SlowHit.MinTime", 15);
            config.addDefault("Effects.Healing.MaxPower", 1000);
            config.addDefault("Effects.Healing.MinPower", 100);
            config.addDefault("Effects.Healing.MaxTime", 50);
            config.addDefault("Effects.Healing.MinTime", 15);
            config.addDefault("Effects.Regeneration.MaxPower", 1000);
            config.addDefault("Effects.Regeneration.MinPower", 100);
            config.addDefault("Effects.Regeneration.MaxTime", 50);
            config.addDefault("Effects.Regeneration.MinTime", 15);
            config.addDefault("Effects.Resistance.MaxPower", 1000);
            config.addDefault("Effects.Resistance.MinPower", 100);
            config.addDefault("Effects.Resistance.MaxTime", 50);
            config.addDefault("Effects.Resistance.MinTime", 15);
            config.addDefault("Effects.FireResistance.MaxPower", 1000);
            config.addDefault("Effects.FireResistance.MinPower", 100);
            config.addDefault("Effects.FireResistance.MaxTime", 50);
            config.addDefault("Effects.FireResistance.MinTime", 15);
            config.addDefault("Effects.WaterBreathing.MaxPower", 1000);
            config.addDefault("Effects.WaterBreathing.MinPower", 100);
            config.addDefault("Effects.WaterBreathing.MaxTime", 50);
            config.addDefault("Effects.WaterBreathing.MinTime", 15);
            config.addDefault("Effects.Invisibility.MaxPower", 1000);
            config.addDefault("Effects.Invisibility.MinPower", 100);
            config.addDefault("Effects.Invisibility.MaxTime", 50);
            config.addDefault("Effects.Invisibility.MinTime", 15);
            config.addDefault("Effects.NightVision.MaxPower", 1000);
            config.addDefault("Effects.NightVision.MinPower", 100);
            config.addDefault("Effects.NightVision.MaxTime", 50);
            config.addDefault("Effects.NightVision.MinTime", 15);
            config.addDefault("Effects.Poison.MaxPower", 1000);
            config.addDefault("Effects.Poison.MinPower", 100);
            config.addDefault("Effects.Poison.MaxTime", 50);
            config.addDefault("Effects.Poison.MinTime", 15);
            config.addDefault("Effects.WitherPoison.MaxPower", 1000);
            config.addDefault("Effects.WitherPoison.MinPower", 100);
            config.addDefault("Effects.WitherPoison.MaxTime", 50);
            config.addDefault("Effects.WitherPoison.MinTime", 15);
            config.addDefault("Effects.Absorption.MaxPower", 4);
            config.addDefault("Effects.Absorption.MinPower", 1);
            config.addDefault("Effects.Absorption.MaxTime", 50);
            config.addDefault("Effects.Absorption.MinTime", 15);
            config.addDefault("Effects.Saturation.MaxPower", 1000);
            config.addDefault("Effects.Saturation.MinPower", 100);
            config.addDefault("Effects.Saturation.MaxTime", 50);
            config.addDefault("Effects.Saturation.MinTime", 15);
            config.addDefault("Effects.Drunk.MaxTime", 50);
            config.addDefault("Effects.Drunk.MinTime", 15);
            config.addDefault("DrugIds.SUGAR.Effect", "2,5");
            config.addDefault("DrugIds.SUGAR.Negatives", "1,3");
            config.addDefault("DrugIds.SUGAR.NegChance", 30);
            config.addDefault("DrugIds.SUGAR.Type", "Random");
            config.addDefault("DrugIds.SUGAR.Smoke", false);
            config.addDefault("DrugIds.SUGAR.DrugName", "Cocaine");
            config.addDefault("DrugIds.INK_SACK:2.Effect", "0,4");
            config.addDefault("DrugIds.INK_SACK:2.Negatives", "0");
            config.addDefault("DrugIds.INK_SACK:2.Type", "All");
            config.addDefault("DrugIds.INK_SACK:2.Smoke", true);
            config.addDefault("DrugIds.INK_SACK:2.DrugName", "Marijuana");
            config.addDefault("DrugIds.RED_MUSHROOM.Effect", "0,1,3,6");
            config.addDefault("DrugIds.RED_MUSHROOM.Negatives", "1,2,3,4");
            config.addDefault("DrugIds.RED_MUSHROOM.NegChance", 20);
            config.addDefault("DrugIds.RED_MUSHROOM.Type", "Random");
            config.addDefault("DrugIds.RED_MUSHROOM.Smoke", false);
            config.addDefault("DrugIds.RED_MUSHROOM.DrugName", "Shrooms");
            config.addDefault("DrugIds.RED_MUSHROOM.Message",
                    "You're about to get hella trippy man.");
            config.addDefault("DrugIds.POTION.Effect", "2,5");
            config.addDefault("DrugIds.POTION.Negatives", "1");
            config.addDefault("DrugIds.POTION.NegChance", 10);
            config.addDefault("DrugIds.POTION.Type", "Random");
            config.addDefault("DrugIds.POTION.Smoke", true);
            config.addDefault("DrugIds.POTION.DrugName", "Vodka");
            config.addDefault("DrugIds.COOKIE.Effect", "0,2,4,5");
            config.addDefault("DrugIds.COOKIE.Negatives", "0");
            config.addDefault("DrugIds.COOKIE.Type", "All");
            config.addDefault("DrugIds.COOKIE.Smoke", true);
            config.addDefault("DrugIds.COOKIE.DrugName", "Hash Cookies");
            config.addDefault("DrugIds.SPIDER_EYE.Effect", "0,1,3,6");
            config.addDefault("DrugIds.SPIDER_EYE.Negatives", "1,2,3,4");
            config.addDefault("DrugIds.SPIDER_EYE.NegChance", 25);
            config.addDefault("DrugIds.SPIDER_EYE.Type", "All");
            config.addDefault("DrugIds.SPIDER_EYE.Smoke", true);
            config.addDefault("DrugIds.SPIDER_EYE.DrugName", "Wild Shrooms");
            config.addDefault("DrugIds.SPIDER_EYE.MustSneak", false);
            config.addDefault("Chat.Broadcast.Burning",
                    "%c* %playername% bursts into flames");
            config.addDefault("Chat.Broadcast.Death",
                    "%c* %playername% OD'd - Don't do drugs kids!");
            config.addDefault("Chat.Broadcast.Puke",
                    "%2* %playername% violently pukes his guts out");
            config.addDefault("Chat.Broadcast.HeartAttack",
                    "%c* %playername% had a heart attack!");
            config.addDefault("Chat.Self.TakeDrugs",
                    "You have taken %drugname%!");
            config.addDefault("Chat.Self.Sober", "%aYou begin to feel sober!");
            config.addDefault("Options.Worlds", "*");
            config.addDefault("Options.AutoUpdateChecker", true);
            config.addDefault("Options.AutoUpdateDownload", true);
            config.addDefault("Options.SaveOldConfigs", true);
            config.addDefault("Options.EnableNegativeEffects", true);
            config.addDefault("Options.EnableEffectMessages", true);
            config.addDefault("Options.EnableJumpProtection", true);
            config.addDefault("DO_NOT_TOUCH", "0.9.1");
            config.addDefault("DO_NOT_TOUCH_2", 1);
            config.set("DO_NOT_TOUCH_2", 1);
            config.options().copyDefaults(true);
            saveConfig();
            reloadConfig();
        }
    }

    /* Gather the drugs into their own, separate objects */
    private void gatherDrugs() {
        for (String s : materials) {
            if (config.getString("DrugIds." + s).replaceAll(":.+", "") != null) {
                Material mat = Material.valueOf(s);
                short dmg = Short.parseShort(config.getString("DrugIds." + s).replaceAll(".+:", ""));
                ItemStack item = new ItemStack(mat, 1, dmg);
                String name = config.getString("DrugIds." + s + ".DrugName");
                String message = (config.getString("DrugIds." + s + ".Message") != null) ? config.getString("DrugIds" +
                        "." + s + ".Message") : "";
                String[] effects = config.getString("DrugIds." + s + ".Effect")
                        .replaceAll(" ", "").split(",");
                String[] negatives = config.getString("DrugIds." + s + ".Negatives")
                        .replaceAll(" ", "").split(",");
                int negChance = (config.getInt("DrugIds." + s + ".NegChance") != 0) ? config.getInt("DrugIds." + s
                        + ".NegChance") : 0;
                boolean type = config.getString("DrugIds." + s + ".Type").equalsIgnoreCase("All");
                boolean smoke = config.getBoolean("DrugIds." + s + ".Smoke");
                boolean negative = (negChance != 0);
                boolean sneak = config.getBoolean("DrugIds." + s + ".MustSneak");
                boolean edible = item.getType().isEdible();
                drugs.put(item, new Drug(item, name, message, effects, negatives, negChance, type, smoke, negative,
                        sneak, edible));
            }
        }
    }

    /* Gather the matierals list into one ArrayList<String> */
    private void gatherMaterials() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(matList));
            String s;
            while ((s = br.readLine()) != null) {
                materials.add(s);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }

    /* Create the list of materials */
    private void createMaterialList() {
        try {
            if (!matList.exists()) {
                matList.createNewFile();
                List<String> list = new ArrayList<>();
                for (Material m : Material.values()) {
                    list.add(m.name());
                }
                FileWriter fw = new FileWriter(matList);

                fw.write("---- All Materials ----" + System.lineSeparator());
                for (String s : list) {
                    fw.write(s + System.lineSeparator());
                }
                fw.close();

                Bukkit.getConsoleSender().sendMessage(
                        ChatColor.RED + "" + ChatColor.BOLD + System.lineSeparator() + System.lineSeparator()
                                + "[DrugMeUp] Material File Generated " + System.lineSeparator()
                                + ChatColor.RESET);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
                             String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("drugmeup")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("drugs.reload")) {
                        reloadConfig();
                        if (sender instanceof Player) {
                            sender.sendMessage(ChatColor.GREEN
                                    + "DrugMeUp Reloaded!");
                            return true;
                        }
                        sender.sendMessage("[DrugMeUp] Reloaded!");
                        return true;
                    }
                }
            } else if (args.length == 2) {
                if (players.containsKey(args[1])) {
                    if (args[0].equalsIgnoreCase("cleardrugs")) {
                        if (sender.hasPermission("drugs.cleardrugs")) {
                            Player p = Bukkit.getPlayer(players.get(args[1]));
                            if (p == null) {
                                if (sender instanceof Player)
                                    sender.sendMessage(ChatColor.RED
                                            + "[DrugMeUp] '" + args[1]
                                            + "' is not online.");
                                else
                                    sender.sendMessage("[DrugMeUp] '" + args[1]
                                            + "' is not online.");
                                return true;
                            } else {
                                for (PotionEffect pe : p.getActivePotionEffects())
                                    p.removePotionEffect(pe.getType());
                                if (sender instanceof Player)
                                    sender.sendMessage(ChatColor.GREEN
                                            + "[DrugMeUp] Cleared drug effects from '"
                                            + p.getName() + "'.");
                                else
                                    sender.sendMessage("[DrugMeUp] Cleared drug effects from '"
                                            + p.getName() + "'.");
                                p.sendMessage(ChatColor.GREEN
                                        + "[DrugMeUp] All of your drug effects have been cleared!");
                                return true;
                            }
                        }
                    }
                }
            } else {
                return false;
            }
        } else {
            sender.sendMessage(ChatColor.DARK_RED
                    + "You don't have permission!");
        }
        return true;
    }

    /* Colorize a String */
    public String colorize(String s) {
        if (s == null) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('%', s);
    }

    /* Update config */
    private void configUpdate() throws IOException {
        File file = new File(getDataFolder(), "config.yml");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        // Only change this if you need to regenerate the config.
        String check = "DO_NOT_TOUCH: '0.9'";
        boolean needUpdate = false;
        boolean saveOld = true;

        while ((line = br.readLine()) != null) {
            if (line.equalsIgnoreCase(check)) {
                needUpdate = true;
            }
            if (line.replaceAll(" ", "").equalsIgnoreCase(
                    "SaveOldConfigs:false")) {
                saveOld = false;
            }
        }
        br.close();

        if (needUpdate) {
            String string;
            if (saveOld) {
                string = ChatColor.RED
                        + ""
                        + ChatColor.BOLD
                        + "[DrugMeUp] Config Saved & Regenerated! Update new one to your liking."
                        + ChatColor.RESET;
                oldDir.mkdir();
                DateFormat dateFormat = new SimpleDateFormat(
                        "MM-dd-yyyy_HH-mm-ss");
                Date date = new Date();
                file.renameTo(new File(oldDir + "\\" + dateFormat.format(date)
                        + ".yml"));
            } else {
                string = ChatColor.RED
                        + ""
                        + ChatColor.BOLD
                        + "[DrugMeUp] Config Regenerated! Update new one to your liking."
                        + ChatColor.RESET;
                saveDefaultConfig2();
                config = getConfig();
                config.set("Options.SaveOldConfigs", false);
                saveConfig();
                reloadConfig();
                Bukkit.getConsoleSender().sendMessage(string);
                return;
            }
            saveDefaultConfig2();
            Bukkit.getConsoleSender().sendMessage(string);
        }
    }

    /* Check for config update */
    public boolean isUpdate(UpdateType type) {
        Updater updater;
        updater = new Updater(this, 35506, this.getFile(),
                Updater.UpdateType.NO_DOWNLOAD, true);
        pdfFile = getDescription();
        if (type == UpdateType.DEFAULT) {
            if (!updater.getLatestName().equalsIgnoreCase(
                    "drugmeup v" + pdfFile.getVersion())) {
                updater = new Updater(this, 35506, this.getFile(), type, true);
                isDownloaded = true;
            }
        } else if (type == UpdateType.NO_DOWNLOAD) {
            updater = new Updater(this, 35506, this.getFile(), type, false);
            Bukkit.getConsoleSender()
                    .sendMessage(
                            ChatColor.RED
                                    + ""
                                    + ChatColor.BOLD
                                    + System.lineSeparator()
                                    + System.lineSeparator()
                                    + "[DrugMeUp] Update Available! "
                                    + System.lineSeparator()
                                    + "Download it at: dev.bukkit.org/server-mods/drugmeup"
                                    + System.lineSeparator() + ChatColor.RESET);
            isUpdate = true;
        }

        return false;
    }

    /* Check if the plugin should be multi-world */
    public boolean isMultiworld() {
        return !getConfig().getString("Options.Worlds").equalsIgnoreCase("*");
    }

    /* Return the ArrayList<World> of worlds that the plugin is active in */
    public ArrayList<World> getWorlds() {
        return worlds;
    }

    /* Gather the worlds that support this plugin */
    public void gatherWorlds() {
        String[] inConfig = config.getString("Options.Worlds")
                .replaceAll(" ", "").split(",");
        ArrayList<World> worlds = new ArrayList<>();
        for (String s : inConfig) {
            worlds.add(Bukkit.getWorld(s));
        }
        this.worlds = worlds;
    }

    /* Get everyone who can't place blocks */
    public List<String> getNoPlace() {
        return noplace;
    }

    /* Get everyone who has scrambled text */
    public List<String> getDrunk() {
        return this.drunk;
    }

    /* Get everyone who is on drugs */
    public List<String> getOnDrugs() {
        return this.onDrugs;
    }

    /* Get everyone who is having a heart attack */
    public List<String> getHeartAttack() {
        return this.heartattack;
    }

    /* Get everyone who has drug-induced jump boost */
    public List<String> getIsJump() {
        return this.isJump;
    }

    /* Check if there's an update */
    public boolean getIsUpdate() {
        return this.isUpdate;
    }

    /* Check if the update should be automatically downloaded */
    public boolean getIsDownloaded() {
        return this.isDownloaded;
    }

    /* Check if the itemstack is a type of drug */
    public boolean isDrug(ItemStack item) {
        return drugs.containsKey(item);
    }

    public Drug getDrug(ItemStack item) {
        return drugs.get(item);
    }
}
