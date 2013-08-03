package me.giinger.dmu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class Drugs extends JavaPlugin {

	private PluginDescriptionFile pdfFile;
	private List<String> onDrugs;
	private List<String> noplace;
	private List<String> isJump;
	private List<String> drunk;
	private List<Integer> effectlist;
	private List<String> heartattack;
	private boolean isUpdate;
	private BlockListener blockListener;
	private EntityListener entityListener;
	private PlayerListener playerListener;
	File configFile = new File("plugins/DrugMeUp/config.yml");
	FileConfiguration config;

	public Drugs() {
		this.onDrugs = new ArrayList<String>();
		this.noplace = new ArrayList<String>();
		this.isJump = new ArrayList<String>();
		this.drunk = new ArrayList<String>();
		this.effectlist = new ArrayList<Integer>();
		this.heartattack = new ArrayList<String>();
		this.blockListener = new BlockListener(this);
		this.entityListener = new EntityListener(this);
		this.playerListener = new PlayerListener(this);
	}

	public void onDisable() {
		pdfFile = getDescription();
		Bukkit.getConsoleSender().sendMessage(
				"[DrugMeUp] v" + pdfFile.getVersion() + " Disabled!");
	}

	public void onEnable() {
		pdfFile = getDescription();
		Bukkit.getConsoleSender().sendMessage(
				"[DrugMeUp] v" + pdfFile.getVersion() + " Enabled!");

		getServer().getPluginManager()
				.registerEvents(this.playerListener, this);
		getServer().getPluginManager().registerEvents(this.blockListener, this);
		getServer().getPluginManager()
				.registerEvents(this.entityListener, this);

		saveDefaultConfig();
		try {
			configUpdate();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		initialConfigGen();

		final String nL = System.lineSeparator();
		config = getConfig();
		if (config.getBoolean("Options.AutoUpdateChecker") == true) {
			getServer().getScheduler().runTaskAsynchronously(this,
					new BukkitRunnable() {
						@Override
						public void run() {
							if (isUpdate()) {
								Bukkit.getConsoleSender()
										.sendMessage(
												ChatColor.RED
														+ ""
														+ ChatColor.BOLD
														+ nL
														+ nL
														+ "[DrugMeUp] Update Available! "
														+ nL
														+ "Download it at: dev.bukkit.org/server-mods/drugmeup"
														+ nL + ChatColor.RESET);
								isUpdate = true;
							}
						}
					});
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
		String nL = System.lineSeparator();
		if (config.getInt("DO_NOT_TOUCH_2") == 0) {
			config.options()
					.header("######### Effects #########"
							+ nL
							+ "0 - Nausea (Screen Spin)"
							+ nL
							+ "1 - Zoom-In & Walk Slow"
							+ nL
							+ "2 - Zoom-Out & Walk Fast"
							+ nL
							+ "3 - Blindness"
							+ nL
							+ "4 - Hunger"
							+ nL
							+ "5 - High Jump"
							+ nL
							+ "6 - Sickness & Slower Hitting"
							+ nL
							+ "7 - Drunk (Word Scramble)"
							+ nL
							+ "8 - Instant Healing"
							+ nL
							+ "9 - Regeneration"
							+ nL
							+ "10 - Resistance"
							+ nL
							+ "11 - Fire Resistance"
							+ nL
							+ "12 - Water Breathing"
							+ nL
							+ "13 - Invisibility"
							+ nL
							+ "14 - Night Vision"
							+ nL
							+ "15 - Poison"
							+ nL
							+ "16 - Wither Poison"
							+ nL
							+ "17 - Absorption (NOTE: If you use higher numbers on Absorption MaxPower, your screen will fill with golden hearts! :) )"
							+ nL + "18 - Saturation" + nL
							+ " ######## Negatives ########" + nL + "0 - None"
							+ nL + "1 - Puke" + nL + "2 - Catch on fire" + nL
							+ "3 - Heart Attack" + nL + "4 - Overdose" + nL
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
			config.addDefault("DrugIds.353.Effect", "2,5");
			config.addDefault("DrugIds.353.Negatives", "1,3");
			config.addDefault("DrugIds.353.Type", "Random");
			config.addDefault("DrugIds.353.Smoke", false);
			config.addDefault("DrugIds.353.DrugName", "Cocaine");
			config.addDefault("DrugIds.351:2.Effect", "0,4");
			config.addDefault("DrugIds.351:2.Negatives", "0");
			config.addDefault("DrugIds.351:2.Type", "All");
			config.addDefault("DrugIds.351:2.Smoke", true);
			config.addDefault("DrugIds.351:2.DrugName", "Marijuana");
			config.addDefault("DrugIds.40.Effect", "0,1,3,6");
			config.addDefault("DrugIds.40.Negatives", "1,2,3,4");
			config.addDefault("DrugIds.40.Type", "Random");
			config.addDefault("DrugIds.40.Smoke", false);
			config.addDefault("DrugIds.40.DrugName", "Shrooms");
			config.addDefault("DrugIds.40.Message",
					"You're about to get hella trippy man.");
			config.addDefault("DrugIds.373.Effect", "2,5");
			config.addDefault("DrugIds.373.Negatives", "1");
			config.addDefault("DrugIds.373.Type", "Random");
			config.addDefault("DrugIds.373.Smoke", true);
			config.addDefault("DrugIds.373.DrugName", "Vodka");
			config.addDefault("DrugIds.357.Effect", "0,2,4,5");
			config.addDefault("DrugIds.357.Negatives", "0");
			config.addDefault("DrugIds.357.Type", "All");
			config.addDefault("DrugIds.357.Smoke", true);
			config.addDefault("DrugIds.357.DrugName", "Hash Cookies");
			config.addDefault("DrugIds.375.Effect", "0,1,3,6");
			config.addDefault("DrugIds.375.Negatives", "1,2,3,4");
			config.addDefault("DrugIds.375.Type", "All");
			config.addDefault("DrugIds.375.Smoke", true);
			config.addDefault("DrugIds.375.DrugName", "Wild Shrooms");
			config.addDefault("DrugIds.375.MustSneak", false);
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
			config.addDefault("Options.AutoUpdateChecker", true);
			config.addDefault("Options.EnableNegativeEffects", true);
			config.addDefault("Options.EnableEffectMessages", true);
			config.addDefault("Options.EnableJumpProtection", true);
			config.addDefault("DO_NOT_TOUCH", "0.9");
			config.addDefault("DO_NOT_TOUCH_2", 1);
			config.set("DO_NOT_TOUCH_2", 1);
			config.options().copyDefaults(true);
			saveConfig();
			reloadConfig();
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
				if (args[0].equalsIgnoreCase("cleardrugs")) {
					if (sender.hasPermission("drugs.cleardrugs")) {
						Player p = Bukkit.getPlayer(args[1]);
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
			} else {
				return false;
			}
		} else {
			sender.sendMessage(ChatColor.DARK_RED
					+ "You don't have permission!");
		}
		return true;
	}

	public String colorize(String s) {
		if (s == null) {
			return null;
		}
		return ChatColor.translateAlternateColorCodes('%', s);
	}

	public void getEffects(int id, short damage) {
		config = getConfig();

		String[] effects;
		String effectslist;

		if (damage == 0) {
			effectslist = config.getString("DrugIds." + id + ".Effect");
		} else {
			effectslist = config.getString("DrugIds." + id + ":" + damage
					+ ".Effect");
		}

		effects = effectslist.replaceAll(" ", "").split(",");

		for (String s : effects) {
			effectlist.add(Integer.parseInt(s));
		}
	}

	private void configUpdate() throws IOException {
		File file = new File(getDataFolder(), "config.yml");
		FileReader fileRead = new FileReader(file);
		BufferedReader br = new BufferedReader(fileRead);
		String line;
		// Only change this if you need to regenerate the config.
		String check = "DO_NOT_TOUCH: 0.8.2";
		boolean needUpdate = false;

		while ((line = br.readLine()) != null) {
			if (line.equalsIgnoreCase(check)) {
				needUpdate = true;
			}
		}

		if (needUpdate) {
			saveDefaultConfig2();
			Bukkit.getConsoleSender()
					.sendMessage(
							ChatColor.RED
									+ ""
									+ ChatColor.BOLD
									+ "[DrugMeUp] Config Regenerated! Update to your liking again."
									+ ChatColor.RESET);
		}
		br.close();
	}

	public boolean isUpdate() {
		try {
			pdfFile = getDescription();
			URL url = new URL(
					"https://dl.dropbox.com/u/46370614/DMU_Update.txt");
			URLConnection urlconnection = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					urlconnection.getInputStream()));

			String u = br.readLine();
			String current = pdfFile.getVersion();

			if (u == null ? current == null : u.equals(current)) {
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			Bukkit.getConsoleSender().sendMessage(
					"There was a problem checking for an update for DrugMeUp!");
		}
		return false;
	}

	public boolean isDrug(int id, short data) {
		config = getConfig();

		if (data <= 0) {
			if (config.getString("DrugIds." + id) != null) {
				return true;
			}
		} else {
			if (config.getString("DrugIds." + id + ":" + data) != null) {
				return true;
			}
		}

		return false;
	}

	public List<String> getNoPlace() {
		return noplace;
	}

	public List<String> getDrunk() {
		return this.drunk;
	}

	public List<String> getOnDrugs() {
		return this.onDrugs;
	}

	public List<String> getHeartAttack() {
		return this.heartattack;
	}

	public List<String> getIsJump() {
		return this.isJump;
	}

	public boolean getIsUpdate() {
		return this.isUpdate;
	}

	public List<Integer> getEffectList() {
		return this.effectlist;
	}
}
