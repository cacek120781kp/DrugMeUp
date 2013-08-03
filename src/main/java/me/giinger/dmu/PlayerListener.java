package me.giinger.dmu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {

	Random gen = new Random();
	int i = 0;
	public final Drugs plugin;
	private Integer[] edibles;

	public PlayerListener(Drugs plugin) {
		this.plugin = plugin;
		this.edibles = new Integer[] { 260, 282, 297, 319, 320, 322, 335, 349,
				350, 357, 360, 363, 364, 365, 366, 367, 373, 375, 391, 392,
				393, 394, 400 };
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent e) {
		final Player player = e.getPlayer();
		if (player.hasPermission("drugs.use") || player.isOp()) {
			ItemStack stack = player.getItemInHand();
			if (stack != null) {
				short data = stack.getDurability();
				if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e
						.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
					if (plugin.isDrug(stack.getTypeId(), data)) {
						if (Arrays.asList(edibles).contains(stack.getTypeId())) {
							return;
						}
						if (player.isSneaking()) {
							ItemStack old = new ItemStack(e.getPlayer()
									.getItemInHand().getTypeId(), e.getPlayer()
									.getItemInHand().getAmount() - 1, data);
							e.getPlayer().setItemInHand(old);
							gatherEffects(player, stack.getTypeId(), data);
							plugin.getNoPlace().add(player.getName());
							doSmoke(player, stack.getTypeId(), data);
							if (plugin.config
									.getBoolean("Options.EnableNegativeEffects") == true) {
								doNegatives(player, stack.getTypeId(), data);
							}

							Bukkit.getScheduler().scheduleSyncDelayedTask(
									plugin, new BukkitRunnable() {
										public void run() {
											plugin.getNoPlace().remove(
													player.getName());
										}
									}, 20);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			if (plugin.getIsJump().contains(((Player) e.getEntity()).getName())) {
				if (e.getCause().equals(DamageCause.FALL)) {
					if (plugin.config
							.getBoolean("Options.EnableJumpProtection") == true) {
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
			String end = scramble(initial);
			e.setMessage(end);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) throws IOException {
		if (plugin.getIsUpdate()) {
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
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerItemConsume(final PlayerItemConsumeEvent e) {
		Player p = e.getPlayer();
		ItemStack i = e.getItem();
		if (i.getType() == Material.MILK_BUCKET) {
			if (plugin.isDrug(i.getTypeId(), i.getDurability())) {
				ItemStack milk = new ItemStack(Material.MILK_BUCKET, 1);
				for (ItemStack i2 : p.getInventory().getContents()) {
					if (i2 != null) {
						if (i2 == milk) {
							p.getInventory().remove(i2);
						}
					}
				}
				e.setCancelled(true);
			}
		}
		if (plugin.isDrug(i.getTypeId(), i.getDurability()))
			if (plugin.config.getBoolean("DrugIds." + i.getTypeId()
					+ ".MustSneak") == true) {
				if (p.isSneaking()) {
					doDrug(e);
				}
			} else {
				doDrug(e);
			}
	}

	public void doDrug(PlayerItemConsumeEvent e) {
		Player p = e.getPlayer();
		ItemStack i = e.getItem();
		ItemStack old = new ItemStack(
				e.getPlayer().getItemInHand().getTypeId(), e.getPlayer()
						.getItemInHand().getAmount() - 1, i.getDurability());
		e.getPlayer().setItemInHand(old);
		gatherEffects(p, i.getTypeId(), i.getDurability());
		doSmoke(p, i.getTypeId(), i.getDurability());
		if (plugin.config.getBoolean("Options.EnableNegativeEffects") == true)
			doNegatives(p, i.getTypeId(), i.getDurability());
	}

	public void doSmoke(Player p, int id, short dmg) {
		boolean smoke;
		if (dmg == 0) {
			smoke = plugin.config.getBoolean("DrugIds." + id + ".Smoke");
		} else {
			smoke = plugin.config.getBoolean("DrugIds." + id + ":" + dmg
					+ ".Smoke");
		}
		if (smoke) {
			for (int iSmoke = 0; iSmoke <= 8; iSmoke++) {
				p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, iSmoke);
			}
		}
	}

	public void doNegatives(Player p, int id, short dmg) {
		List<Integer> negatives = new ArrayList<Integer>();
		try {
			if (dmg == 0) {
				String[] negs = plugin.config
						.getString("DrugIds." + id + ".Negatives")
						.replaceAll(" ", "").split(",");
				for (String s : negs) {
					negatives.add(Integer.parseInt(s));
				}
			} else {
				String[] negs = plugin.config
						.getString("DrugIds." + id + ":" + dmg + ".Negatives")
						.replaceAll(" ", "").split(",");
				for (String s : negs) {
					negatives.add(Integer.parseInt(s));
				}
			}
			if (negatives.contains(0)) {
				return;
			} else {
				int iNegative = gen.nextInt(32);
				if (iNegative < 1) {
					iNegative = 5;
				}
				if (iNegative > 32) {
					iNegative = 32;
				}

				if (iNegative == 1) {
					if (negatives.contains(1)) {
						pukeInv(p);
					}
				} else if (iNegative == 2) {
					if (negatives.contains(2)) {
						torchYa(p);
					}
				} else if (iNegative == 3) {
					if (negatives.contains(3)) {
						heartAttack(p);
					}
				} else if (iNegative == 4) {
					if (negatives.contains(4)) {
						youOd(p);
					}
				}
			}
			negatives.clear();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public void gatherEffects(Player p, int i, short dmg) {
		plugin.getEffects(i, dmg);

		if (dmg == 0) {
			if (plugin.config.getString("DrugIds." + i + ".Type")
					.equalsIgnoreCase("All")) {
				for (int ii : plugin.getEffectList()) {
					applyEffect(p, ii);
				}
				plugin.getEffectList().clear();
			} else if (plugin.config.getString("DrugIds." + i + ".Type")
					.equalsIgnoreCase("Random")) {
				doRandomEffects(p);
			}
		} else {
			if (plugin.config.getString("DrugIds." + i + ":" + dmg + ".Type")
					.equalsIgnoreCase("All")) {
				for (int ii : plugin.getEffectList()) {
					applyEffect(p, ii);
				}
				plugin.getEffectList().clear();
			} else if (plugin.config.getString(
					"DrugIds." + i + ":" + dmg + ".Type").equalsIgnoreCase(
					"Random")) {
				doRandomEffects(p);
			}
		}

		int itemi = p.getItemInHand().getTypeId();
		short dura = p.getItemInHand().getDurability();
		String ond = "";

		if (dura <= 0) {
			if (plugin.config.getString("DrugIds." + itemi + ".Message") == null) {
				ond = plugin.config.getString("Chat.Self.TakeDrugs")
						.replaceAll(
								"%drugname%",
								plugin.config.getString("DrugIds." + itemi
										+ ".DrugName"));
			} else {
				ond = plugin.config.getString("DrugIds." + itemi + ".Message")
						.replaceAll(
								"%drugname%",
								plugin.config.getString("DrugIds." + itemi
										+ ".DrugName"));
			}
		} else {
			if (plugin.config.getString("DrugIds." + itemi + ":" + dura
					+ ".Message") == null) {
				ond = plugin.config.getString("Chat.Self.TakeDrugs")
						.replaceAll(
								"%drugname%",
								plugin.config.getString("DrugIds." + itemi
										+ ":" + dura + ".DrugName"));
			} else {
				ond = plugin.config.getString(
						"DrugIds." + itemi + ":" + dura + ".Message")
						.replaceAll(
								"%drugname%",
								plugin.config.getString("DrugIds." + itemi
										+ ":" + dura + ".DrugName"));
			}
		}

		ond = ond.replaceAll("%playername%", p.getName());
		p.sendMessage(plugin.colorize(ond));

		plugin.getEffectList().clear();
	}

	public void doRandomEffects(Player p) {
		int ii = plugin.getEffectList().size();
		int iRandom = gen.nextInt(ii);

		if (iRandom < 0) {
			iRandom = 0;
		}
		if (iRandom > ii) {
			iRandom = ii;
		}

		int x = plugin.getEffectList().get(iRandom);

		applyEffect(p, x);
	}

	public void applyEffect(Player p, int i) {

		plugin.getOnDrugs().add(p.getName());

		// All potion effects here:
		// http://www.minecraftwiki.net/wiki/Status_effect

		if (i == 0) {
			// Portal Effect
			walkWeird(p);
		} else if (i == 1) {
			// Zoom-In & Walk Slow
			walkSlow(p);
		} else if (i == 2) {
			// Zoom-Out & Walk Fast
			walkFast(p);
		} else if (i == 3) {
			// Blind
			blindMe(p);
		} else if (i == 4) {
			// Hunger
			soHungry(p);
		} else if (i == 5) {
			// High Jump
			feelingJumpy(p);
		} else if (i == 6) {
			// Sickness & Slower Hitting
			soSick(p);
		} else if (i == 7) {
			// Drunk
			drunk(p);
		} else if (i == 8) {
			// Healing
			healing(p);
		} else if (i == 9) {
			// Regeneration
			regeneration(p);
		} else if (i == 10) {
			// Resistance
			resistance(p);
		} else if (i == 11) {
			// fire resistance
			fireResistance(p);
		} else if (i == 12) {
			// Water breathing
			waterBreathe(p);
		} else if (i == 13) {
			// Invisibility
			invisibility(p);
		} else if (i == 14) {
			// Night vision
			nightVision(p);
		} else if (i == 15) {
			// Poison
			poison(p);
		} else if (i == 16) {
			// Wither poison
			witherPoison(p);
		} else if (i == 17) {
			// Absorption
			absorption(p);
		} else if (i == 18) {
			// Saturation
			saturation(p);
		}

	}

	public void walkWeird(Player p) {
		int maxpower = plugin.config.getInt("Effects.Nausea.MaxPower");
		int minpower = plugin.config.getInt("Effects.Nausea.MinPower");
		int maxtime = plugin.config.getInt("Effects.Nausea.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.Nausea.MinTime") * 20;

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}
		p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, ran,
				power));
	}

	public void walkSlow(final Player p) {
		int maxpower = plugin.config.getInt("Effects.SlowWalk.MaxPower");
		int minpower = plugin.config.getInt("Effects.SlowWalk.MinPower");
		int maxtime = plugin.config.getInt("Effects.SlowWalk.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.SlowWalk.MinTime") * 20;

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, ran, power));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						plugin.getOnDrugs().remove(p.getName());
					}
				}, ran);
	}

	public void walkFast(final Player p) {
		int maxpower = plugin.config.getInt("Effects.FastWalk.MaxPower");
		int minpower = plugin.config.getInt("Effects.FastWalk.MinPower");
		int maxtime = plugin.config.getInt("Effects.FastWalk.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.FastWalk.MinTime") * 20;

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ran, power));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						plugin.getOnDrugs().remove(p.getName());
					}
				}, ran);
	}

	public void blindMe(final Player p) {
		int maxpower = plugin.config.getInt("Effects.Blindness.MaxPower");
		int minpower = plugin.config.getInt("Effects.Blindness.MinPower");
		int maxtime = plugin.config.getInt("Effects.Blindness.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.Blindness.MinTime") * 20;

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}
		p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, ran,
				power));
		p.canSee(p);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						plugin.getOnDrugs().remove(p.getName());
					}
				}, ran);
	}

	public void soHungry(final Player p) {
		int maxpower = plugin.config.getInt("Effects.Hunger.MaxPower");
		int minpower = plugin.config.getInt("Effects.Hunger.MinPower");
		int maxtime = plugin.config.getInt("Effects.Hunger.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.Hunger.MinTime") * 20;

		final int food = p.getFoodLevel();

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}
		p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, ran, power));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						plugin.getOnDrugs().remove(p.getName());
						p.setFoodLevel(food / 2);
					}
				}, ran);
	}

	public void feelingJumpy(final Player p) {
		int maxpower = plugin.config.getInt("Effects.HighJump.MaxPower");
		int minpower = plugin.config.getInt("Effects.HighJump.MinPower");
		int maxtime = plugin.config.getInt("Effects.HighJump.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.HighJump.MinTime") * 20;

		plugin.getOnDrugs().add(p.getName());
		plugin.getIsJump().add(p.getName());

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, ran, power));

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						plugin.getOnDrugs().remove(p.getName());
						plugin.getIsJump().remove(p.getName());
					}
				}, ran);
	}

	public void soSick(final Player p) {
		int maxpower = plugin.config.getInt("Effects.SlowHit.MaxPower");
		int minpower = plugin.config.getInt("Effects.SlowHit.MinPower");
		int maxtime = plugin.config.getInt("Effects.SlowHit.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.SlowHit.MinTime") * 20;

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}

		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, ran,
				power));
		p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, ran,
				power));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						plugin.getOnDrugs().remove(p.getName());
					}
				}, ran);
	}

	public void healing(final Player p) {
		int maxpower = plugin.config.getInt("Effects.Healing.MaxPower");
		int minpower = plugin.config.getInt("Effects.Healing.MinPower");
		int maxtime = plugin.config.getInt("Effects.Healing.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.Healing.MinTime") * 20;

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}

		p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, ran, power));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						plugin.getOnDrugs().remove(p.getName());
					}
				}, ran);
	}

	public void regeneration(final Player p) {
		int maxpower = plugin.config.getInt("Effects.Regeneration.MaxPower");
		int minpower = plugin.config.getInt("Effects.Regeneration.MinPower");
		int maxtime = plugin.config.getInt("Effects.Regeneration.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.Regeneration.MinTime") * 20;

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}

		p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, ran,
				power));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						plugin.getOnDrugs().remove(p.getName());
					}
				}, ran);
	}

	public void resistance(final Player p) {
		int maxpower = plugin.config.getInt("Effects.Resistance.MaxPower");
		int minpower = plugin.config.getInt("Effects.Resistance.MinPower");
		int maxtime = plugin.config.getInt("Effects.Resistance.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.Resistance.MinTime") * 20;

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}

		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,
				ran, power));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						plugin.getOnDrugs().remove(p.getName());
					}
				}, ran);
	}

	public void fireResistance(final Player p) {
		int maxpower = plugin.config.getInt("Effects.FireResistance.MaxPower");
		int minpower = plugin.config.getInt("Effects.FireResistance.MinPower");
		int maxtime = plugin.config.getInt("Effects.FireResistance.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.FireResistance.MinTime") * 20;

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}

		p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,
				ran, power));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						plugin.getOnDrugs().remove(p.getName());
					}
				}, ran);
	}

	public void waterBreathe(final Player p) {
		int maxpower = plugin.config.getInt("Effects.WaterBreathing.MaxPower");
		int minpower = plugin.config.getInt("Effects.WaterBreathing.MinPower");
		int maxtime = plugin.config.getInt("Effects.WaterBreathing.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.WaterBreathing.MinTime") * 20;

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}

		p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING,
				ran, power));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						plugin.getOnDrugs().remove(p.getName());
					}
				}, ran);
	}

	public void invisibility(final Player p) {
		int maxpower = plugin.config.getInt("Effects.Invisibility.MaxPower");
		int minpower = plugin.config.getInt("Effects.Invisibility.MinPower");
		int maxtime = plugin.config.getInt("Effects.Invisibility.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.Invisibility.MinTime") * 20;

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}

		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, ran,
				power));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						plugin.getOnDrugs().remove(p.getName());
					}
				}, ran);
	}

	public void nightVision(final Player p) {
		int maxpower = plugin.config.getInt("Effects.NightVision.MaxPower");
		int minpower = plugin.config.getInt("Effects.NightVision.MinPower");
		int maxtime = plugin.config.getInt("Effects.NightVision.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.NightVision.MinTime") * 20;

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}

		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, ran,
				power));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						plugin.getOnDrugs().remove(p.getName());
					}
				}, ran);
	}

	public void poison(final Player p) {
		int maxpower = plugin.config.getInt("Effects.Poison.MaxPower");
		int minpower = plugin.config.getInt("Effects.Poison.MinPower");
		int maxtime = plugin.config.getInt("Effects.Poison.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.Poison.MinTime") * 20;

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}

		p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, ran, power));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						plugin.getOnDrugs().remove(p.getName());
					}
				}, ran);
	}

	public void witherPoison(final Player p) {
		int maxpower = plugin.config.getInt("Effects.WitherPoison.MaxPower");
		int minpower = plugin.config.getInt("Effects.WitherPoison.MinPower");
		int maxtime = plugin.config.getInt("Effects.WitherPoison.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.WitherPoison.MinTime") * 20;

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}

		p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, ran, power));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						plugin.getOnDrugs().remove(p.getName());
					}
				}, ran);
	}

	public void absorption(final Player p) {
		int maxpower = plugin.config.getInt("Effects.Absorption.MaxPower");
		int minpower = plugin.config.getInt("Effects.Absorption.MinPower");
		int maxtime = plugin.config.getInt("Effects.Absorption.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.Absorption.MinTime") * 20;

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}

		p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, ran,
				power));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						plugin.getOnDrugs().remove(p.getName());
					}
				}, ran);
	}

	public void saturation(final Player p) {
		int maxpower = plugin.config.getInt("Effects.Saturation.MaxPower");
		int minpower = plugin.config.getInt("Effects.Saturation.MinPower");
		int maxtime = plugin.config.getInt("Effects.Saturation.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.Saturation.MinTime") * 20;

		int power = gen.nextInt(maxpower);
		if (power <= minpower) {
			power = minpower;
		}
		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}

		p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, ran,
				power));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						plugin.getOnDrugs().remove(p.getName());
					}
				}, ran);
	}

	public String scramble(String word) {
		StringBuilder builder = new StringBuilder(word.length());
		boolean[] used = new boolean[word.length()];

		for (int iScramble = 0; iScramble < word.length(); iScramble++) {
			int rndIndex;
			do {
				rndIndex = new Random().nextInt(word.length());
			} while (used[rndIndex] != false);
			used[rndIndex] = true;

			builder.append(word.charAt(rndIndex));
		}
		return builder.toString();
	}

	public void drunk(final Player p) {
		int maxtime = plugin.config.getInt("Effects.Drunk.MaxTime") * 20;
		int mintime = plugin.config.getInt("Effects.Drunk.MinTime") * 20;

		plugin.getDrunk().add(p.getName());

		int ran = gen.nextInt(maxtime);
		if (ran <= mintime) {
			ran = mintime;
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					public void run() {
						p.sendMessage(plugin.colorize(plugin.config
								.getString("Chat.Self.Sober")));
						plugin.getDrunk().remove(p.getName());
					}
				}, ran);
	}

	@SuppressWarnings("deprecation")
	public void pukeInv(Player p) {
		int itemi = p.getItemInHand().getTypeId();
		short dura = p.getItemInHand().getDurability();
		String puke = "";

		if (plugin.config.getBoolean("Options.EnableEffectMessages") == true) {
			if (dura == 0) {
				puke = plugin.config.getString("Chat.Broadcast.Puke")
						.replaceAll(
								"%drugname%",
								plugin.config.getString("DrugIds." + itemi
										+ ".DrugName"));
			} else {
				puke = plugin.config.getString("Chat.Broadcast.Puke")
						.replaceAll(
								"%drugname%",
								plugin.config.getString("DrugIds." + itemi
										+ ":" + dura + ".DrugName"));
			}
			puke = puke.replaceAll("%playername%", p.getName());
			Bukkit.broadcastMessage(plugin.colorize(puke));
		}
		ItemStack[] inventory = p.getInventory().getContents();
		Location l = p.getLocation().getBlock().getRelative(BlockFace.NORTH, 3)
				.getLocation();
		p.getInventory().clear();
		for (ItemStack item : inventory) {
			if (item != null) {
				p.getWorld().dropItemNaturally(l, item);
				p.updateInventory();
			}
		}
		p.updateInventory();
	}

	public void torchYa(Player p) {
		int itemi = p.getItemInHand().getTypeId();
		short dura = p.getItemInHand().getDurability();
		String hot = "";

		if (plugin.config.getBoolean("Options.EnableEffectMessages") == true) {
			if (dura == 0) {
				hot = plugin.config.getString("Chat.Broadcast.Burning")
						.replaceAll(
								"%drugname%",
								plugin.config.getString("DrugIds." + itemi
										+ ".DrugName"));
			} else {
				hot = plugin.config.getString("Chat.Broadcast.Burning")
						.replaceAll(
								"%drugname%",
								plugin.config.getString("DrugIds." + itemi
										+ ":" + dura + ".DrugName"));
			}
			hot = hot.replaceAll("%playername%", p.getName());
			Bukkit.broadcastMessage(plugin.colorize(hot));
		}
		p.setFireTicks(200);
	}

	public void youOd(Player p) {
		int itemi = p.getItemInHand().getTypeId();
		short dura = p.getItemInHand().getDurability();
		String death = "";

		if (plugin.config.getBoolean("Options.EnableEffectMessages") == true) {
			if (dura == 0) {
				death = plugin.config.getString("Chat.Broadcast.Death")
						.replaceAll(
								"%drugname%",
								plugin.config.getString("DrugIds." + itemi
										+ ".DrugName"));
			} else {
				death = plugin.config.getString("Chat.Broadcast.Death")
						.replaceAll(
								"%drugname%",
								plugin.config.getString("DrugIds." + itemi
										+ ":" + dura + ".DrugName"));
			}
			death = death.replaceAll("%playername%", p.getName());
			Bukkit.broadcastMessage(plugin.colorize(death));
		}
		p.setHealth(0);
	}

	public void heartAttack(final Player p) {
		int itemi = p.getItemInHand().getTypeId();
		short dura = p.getItemInHand().getDurability();
		String heartAttack;

		if (plugin.config.getBoolean("Options.EnableEffectMessages") == true) {
			if (dura == 0) {
				heartAttack = plugin.config.getString(
						"Chat.Broadcast.HeartAttack").replaceAll(
						"%drugname%",
						plugin.config.getString("DrugIds." + itemi
								+ ".DrugName"));
			} else {
				heartAttack = plugin.config.getString(
						"Chat.Broadcast.HeartAttack").replaceAll(
						"%drugname%",
						plugin.config.getString("DrugIds." + itemi + ":" + dura
								+ ".DrugName"));
			}
			heartAttack = heartAttack.replaceAll("%playername%", p.getName());
			Bukkit.broadcastMessage(plugin.colorize(heartAttack));
		}
		plugin.getHeartAttack().add(p.getName());

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new BukkitRunnable() {
					@Override
					public void run() {
						plugin.getHeartAttack().remove(p.getName());
					}
				}, 100);
		if (p.getHealth() < 2) {
			p.setHealth(2);
		}
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,
				new BukkitRunnable() {
					@Override
					public void run() {
						if (plugin.getHeartAttack().contains(p.getName())) {
							if (p.getHealth() < 2) {
								p.setHealth(2);
							} else {
								p.setHealth(1);
							}
						}
					}
				}, 0L, 3L);
	}
}
