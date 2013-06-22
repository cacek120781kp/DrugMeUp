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
														+ "\n\n[DrugMeUp] Update Available! \nDownload it at: dev.bukkit.org/server-mods/drugmeup\n"
														+ ChatColor.RESET);
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
		if (config.getInt("DO_NOT_TOUCH_2") == 0) {
			config.options()
					.header("######### Effects #########\n0 - Nausea (Screen Spin)\n1 - Zoom-In & Walk Slow\n2 - Zoom-Out & Walk Fast\n3 - Blindness\n4 - Hunger\n5 - High Jump\n6 - Sickness & Slower Hitting\n7 - Drunk (Word Scramble)\n######## Negatives ########\n0 - None\n1 - Puke\n2 - Catch on fire\n3 - Heart Attack\n4 - Overdose\n###########################");
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
			config.addDefault("DO_NOT_TOUCH", "0.8.1");
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
			if (sender.hasPermission("drugs.reload")) {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("reload")) {
						reloadConfig();
						if (sender instanceof Player) {
							sender.sendMessage(ChatColor.GREEN
									+ "DrugMeUp Reloaded!");
							return true;
						}
						sender.sendMessage("[DrugMeUp] Reloaded!");
						return true;
					}
				} else {
					sender.sendMessage(ChatColor.DARK_RED
							+ "Invalid Arguments!");
				}
				return false;
			} else {
				sender.sendMessage(ChatColor.DARK_RED
						+ "You don't have permission!");
			}
		}
		return false;
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
		String check = "DO_NOT_TOUCH: '0.8'";
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
