package me.giinger.dmu;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class PlayerHandler implements Listener {

    Random gen = new Random();
    public final DrugMeUp plugin;

    public PlayerHandler(DrugMeUp plugin) {
        this.plugin = plugin;
    }

    public void doDrug(Player p, Drug drug) {
        p.getInventory().remove(drug.getItemStack());
        doEffects(p, drug);
        doSmoke(p, drug);
        if (plugin.config.getBoolean("Options.EnableNegativeEffects"))
            doNegatives(p, drug);
    }

    private void doEffects(Player p, Drug drug) {
        if (drug.isType()) {
            // All
            for (int effect : drug.getEffects()) {
                applyEffect(p, effect);
            }
        } else {
            // Random
            int totalEffects = drug.getEffects().size();
            int random = gen.nextInt(totalEffects);

            if (random < 0) {
                random = 0;
            }
            if (random > totalEffects) {
                random = totalEffects;
            }
            int x = drug.getEffects().get(random);
            applyEffect(p, x);
        }
    }

    public void doSmoke(Player p, Drug drug) {
        if (drug.isSmoke()) {
            for (int iSmoke = 0; iSmoke <= 8; iSmoke++) {
                p.getWorld().playEffect(p.getLocation().add(0, 1, 0),
                        Effect.SMOKE, iSmoke);
            }
        }
    }

    public void doNegatives(Player p, Drug drug) {
        Random ran = new Random();
        int negChance = drug.getNegativeChance();
        int random = ran.nextInt(100);
        int currentNeg = ran.nextInt(4);
        if (random < 100) {
            random = 100;
        } else if (random < 1) {
            random = 1;
        }
        if (currentNeg < 1) {
            currentNeg = 1;
        }

        if (random <= negChance) {
            switch (currentNeg) {
                case 1:
                    pukeInv(p);
                    break;
                case 2:
                    torchYa(p);
                    break;
                case 3:
                    heartAttack(p);
                    break;
                case 4:
                    youOd(p);
                    break;
            }
        }
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
            } while (used[rndIndex]);
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
        String puke;

        if (plugin.config.getBoolean("Options.EnableEffectMessages")) {
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
        Material itemi = p.getItemInHand().getType();
        short dura = p.getItemInHand().getDurability();
        String hot;

        if (plugin.config.getBoolean("Options.EnableEffectMessages")) {
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
        Material itemi = p.getItemInHand().getType();
        short dura = p.getItemInHand().getDurability();
        String death;

        if (plugin.config.getBoolean("Options.EnableEffectMessages")) {
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
        Material itemi = p.getItemInHand().getType();
        short dura = p.getItemInHand().getDurability();
        String heartAttack;

        if (plugin.config.getBoolean("Options.EnableEffectMessages")) {
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
