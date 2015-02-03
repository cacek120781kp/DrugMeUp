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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class PlayerHandler implements Listener {

    private Random gen = new Random();
    private final DrugMeUp plugin;
    private ArrayList<String> onDrugs = new ArrayList<>();
    private ArrayList<String> isJump = new ArrayList<>();
    private ArrayList<String> drunk = new ArrayList<>();
    private ArrayList<String> heartattack = new ArrayList<>();
    private ArrayList<String> noPlace = new ArrayList<>();

    public PlayerHandler(DrugMeUp plugin) {
        this.plugin = plugin;
    }

    public void doDrug(Player p, Drug drug) {
        doRemoveDrug(p, drug);
        doMessage(p, drug);
        doEffects(p, drug);
        doSmoke(p, drug);
        doNegatives(p, drug);
    }

    private void doRemoveDrug(Player p, Drug drug) {
        ItemStack dItem = drug.getItemStack();
        if (dItem.getAmount() > 1) {
            dItem.setAmount(dItem.getAmount() - 1);
        } else {
            p.getInventory().removeItem(dItem);
        }
    }

    private void doMessage(Player p, Drug drug) {
        boolean hasMessage = !drug.getMessage().equalsIgnoreCase("");
        p.sendMessage(plugin.colorize((hasMessage) ? drug.getMessage() : plugin.config.getString("Chat.Self.TakeDrugs")
                .replaceAll("%drugname%", drug.getName())));
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
        if (drug.isNegative()) {
            Random ran = new Random();
            int negChance = drug.getNegativeChance();
            int random = ran.nextInt(100);
            int currentNeg = 0;
            while (!drug.getNegatives().contains(currentNeg)) {
                currentNeg = ran.nextInt(4);
            }
            if (random > 100) {
                random = 100;
            } else if (random < 1) {
                random = 1;
            }
            filterInt(currentNeg, 1);

            if (random <= negChance) {
                switch (currentNeg) {
                    case 1:
                        doPuke(p);
                        break;
                    case 2:
                        doBurning(p);
                        break;
                    case 3:
                        doHeartAttack(p);
                        break;
                    case 4:
                        doOverdose(p);
                        break;
                }
            }
        }

    }

    public void applyEffect(Player p, int i) {
        onDrugs.add(p.getName());

        /* All potion effects here:
         http://www.minecraftwiki.net/wiki/Status_effect */

        switch (i) {
            case 0:
                // Portal Effect
                doNausea(p);
                break;
            case 1:
                // Zoom-In & Walk Slow
                doWalkSlow(p);
                break;
            case 2:
                // Zoom-Out & Walk Fast
                doWalkFast(p);
                break;
            case 3:
                // Blind
                doBlindness(p);
                break;
            case 4:
                // Hunger
                doHunger(p);
                break;
            case 5:
                // High Jump
                doHighJump(p);
                break;
            case 6:
                // Sickness & Slower Hitting
                doFatigue(p);
                break;
            case 7:
                // Drunk
                doDrunk(p);
                break;
            case 8:
                // Healing
                doHealing(p);
                break;
            case 9:
                // Regeneration
                doRegeneration(p);
                break;
            case 10:
                // Resistance
                doResistance(p);
                break;
            case 11:
                // Fire Resistance
                doFireResistance(p);
                break;
            case 12:
                // Water breathing
                doWaterBreathing(p);
                break;
            case 13:
                // Invisibility
                doInvisibility(p);
                break;
            case 14:
                // Night Vision
                doNightVision(p);
                break;
            case 15:
                // Poison
                doPoison(p);
                break;
            case 16:
                // Wither Poison
                doWitherPoison(p);
                break;
            case 17:
                // Absorbtion
                doAbsorption(p);
                break;
            case 18:
                // Saturation
                doSaturation(p);
                break;
        }
    }


    public void doNausea(Player p) {
        int maxPower = plugin.config.getInt("Effects.Nausea.MaxPower");
        int minPower = plugin.config.getInt("Effects.Nausea.MinPower");
        int maxTime = plugin.config.getInt("Effects.Nausea.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.Nausea.MinTime") * 20;

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, time,
                power));
        removeEffects(p, false, time);
    }

    public void doWalkSlow(final Player p) {
        int maxPower = plugin.config.getInt("Effects.SlowWalk.MaxPower");
        int minPower = plugin.config.getInt("Effects.SlowWalk.MinPower");
        int maxTime = plugin.config.getInt("Effects.SlowWalk.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.SlowWalk.MinTime") * 20;

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time, power));
        removeEffects(p, false, time);
    }

    public void doWalkFast(final Player p) {
        int maxPower = plugin.config.getInt("Effects.FastWalk.MaxPower");
        int minPower = plugin.config.getInt("Effects.FastWalk.MinPower");
        int maxTime = plugin.config.getInt("Effects.FastWalk.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.FastWalk.MinTime") * 20;

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, time, power));
        removeEffects(p, false, time);
    }

    public void doBlindness(final Player p) {
        int maxPower = plugin.config.getInt("Effects.Blindness.MaxPower");
        int minPower = plugin.config.getInt("Effects.Blindness.MinPower");
        int maxTime = plugin.config.getInt("Effects.Blindness.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.Blindness.MinTime") * 20;

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, time,
                power));
        p.canSee(p);
        removeEffects(p, false, time);
    }

    public void doHunger(final Player p) {
        int maxPower = plugin.config.getInt("Effects.Hunger.MaxPower");
        int minPower = plugin.config.getInt("Effects.Hunger.MinPower");
        int maxTime = plugin.config.getInt("Effects.Hunger.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.Hunger.MinTime") * 20;

        final int currFood = p.getFoodLevel();

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, time, power));
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                new Runnable() {
                    public void run() {
                        onDrugs.remove(p.getName());
                        p.setFoodLevel(currFood / 2);
                    }
                }, time);
    }

    public void doHighJump(final Player p) {
        int maxPower = plugin.config.getInt("Effects.HighJump.MaxPower");
        int minPower = plugin.config.getInt("Effects.HighJump.MinPower");
        int maxTime = plugin.config.getInt("Effects.HighJump.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.HighJump.MinTime") * 20;

        onDrugs.add(p.getName());
        isJump.add(p.getName());

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, time, power));
        removeEffects(p, true, time);
    }

    public void doFatigue(final Player p) {
        int maxPower = plugin.config.getInt("Effects.SlowHit.MaxPower");
        int minPower = plugin.config.getInt("Effects.SlowHit.MinPower");
        int maxTime = plugin.config.getInt("Effects.SlowHit.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.SlowHit.MinTime") * 20;

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, time,
                power));
        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, time,
                power));
        removeEffects(p, false, time);
    }

    public void doHealing(final Player p) {
        int maxPower = plugin.config.getInt("Effects.Healing.MaxPower");
        int minPower = plugin.config.getInt("Effects.Healing.MinPower");
        int maxTime = plugin.config.getInt("Effects.Healing.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.Healing.MinTime") * 20;

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, time, power));
        removeEffects(p, false, time);
    }

    public void doRegeneration(final Player p) {
        int maxPower = plugin.config.getInt("Effects.Regeneration.MaxPower");
        int minPower = plugin.config.getInt("Effects.Regeneration.MinPower");
        int maxTime = plugin.config.getInt("Effects.Regeneration.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.Regeneration.MinTime") * 20;

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, time,
                power));
        removeEffects(p, false, time);
    }

    public void doResistance(final Player p) {
        int maxPower = plugin.config.getInt("Effects.Resistance.MaxPower");
        int minPower = plugin.config.getInt("Effects.Resistance.MinPower");
        int maxTime = plugin.config.getInt("Effects.Resistance.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.Resistance.MinTime") * 20;

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,
                time, power));
        removeEffects(p, false, time);
    }

    public void doFireResistance(final Player p) {
        int maxPower = plugin.config.getInt("Effects.FireResistance.MaxPower");
        int minPower = plugin.config.getInt("Effects.FireResistance.MinPower");
        int maxTime = plugin.config.getInt("Effects.FireResistance.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.FireResistance.MinTime") * 20;

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,
                time, power));
        removeEffects(p, false, time);
    }

    public void doWaterBreathing(final Player p) {
        int maxPower = plugin.config.getInt("Effects.WaterBreathing.MaxPower");
        int minPower = plugin.config.getInt("Effects.WaterBreathing.MinPower");
        int maxTime = plugin.config.getInt("Effects.WaterBreathing.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.WaterBreathing.MinTime") * 20;

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING,
                time, power));
        removeEffects(p, false, time);
    }

    public void doInvisibility(final Player p) {
        int maxPower = plugin.config.getInt("Effects.Invisibility.MaxPower");
        int minPower = plugin.config.getInt("Effects.Invisibility.MinPower");
        int maxTime = plugin.config.getInt("Effects.Invisibility.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.Invisibility.MinTime") * 20;

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, time,
                power));
        removeEffects(p, false, time);
    }

    public void doNightVision(final Player p) {
        int maxPower = plugin.config.getInt("Effects.NightVision.MaxPower");
        int minPower = plugin.config.getInt("Effects.NightVision.MinPower");
        int maxTime = plugin.config.getInt("Effects.NightVision.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.NightVision.MinTime") * 20;

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, time,
                power));
        removeEffects(p, false, time);
    }

    public void doPoison(final Player p) {
        int maxPower = plugin.config.getInt("Effects.Poison.MaxPower");
        int minPower = plugin.config.getInt("Effects.Poison.MinPower");
        int maxTime = plugin.config.getInt("Effects.Poison.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.Poison.MinTime") * 20;

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, time, power));
        removeEffects(p, false, time);
    }

    public void doWitherPoison(final Player p) {
        int maxPower = plugin.config.getInt("Effects.WitherPoison.MaxPower");
        int minPower = plugin.config.getInt("Effects.WitherPoison.MinPower");
        int maxTime = plugin.config.getInt("Effects.WitherPoison.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.WitherPoison.MinTime") * 20;

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, time, power));
        removeEffects(p, false, time);
    }

    public void doAbsorption(final Player p) {
        int maxPower = plugin.config.getInt("Effects.Absorption.MaxPower");
        int minPower = plugin.config.getInt("Effects.Absorption.MinPower");
        int maxTime = plugin.config.getInt("Effects.Absorption.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.Absorption.MinTime") * 20;

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, time,
                power));
        removeEffects(p, false, time);
    }

    public void doSaturation(final Player p) {
        int maxPower = plugin.config.getInt("Effects.Saturation.MaxPower");
        int minPower = plugin.config.getInt("Effects.Saturation.MinPower");
        int maxTime = plugin.config.getInt("Effects.Saturation.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.Saturation.MinTime") * 20;

        int power = filterInt(gen.nextInt(maxPower), minPower);
        int time = filterInt(gen.nextInt(maxTime), minTime);

        p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, time,
                power));
        removeEffects(p, false, time);
    }

    public void doDrunk(final Player p) {
        int maxTime = plugin.config.getInt("Effects.Drunk.MaxTime") * 20;
        int minTime = plugin.config.getInt("Effects.Drunk.MinTime") * 20;

        drunk.add(p.getName());

        int time = filterInt(gen.nextInt(maxTime), minTime);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                new Runnable() {
                    public void run() {
                        p.sendMessage(plugin.colorize(plugin.config
                                .getString("Chat.Self.Sober")));
                        drunk.remove(p.getName());
                    }
                }, time);
    }

    @SuppressWarnings("deprecation")
    public void doPuke(Player p) {
        int itemi = p.getItemInHand().getTypeId();
        short dura = p.getItemInHand().getDurability();
        String puke;

        if (plugin.config.getBoolean("Options.EnableBroadcastMessages")) {
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

    public void doBurning(Player p) {
        Material itemi = p.getItemInHand().getType();
        short dura = p.getItemInHand().getDurability();
        String hot;

        if (plugin.config.getBoolean("Options.EnableBroadcastMessages")) {
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

    public void doOverdose(Player p) {
        Material itemi = p.getItemInHand().getType();
        short dura = p.getItemInHand().getDurability();
        String death;

        if (plugin.config.getBoolean("Options.EnableBroadcastMessages")) {
            if (dura == 0) {
                death = plugin.config.getString("Chat.Broadcast.Death").replaceAll("%drugname%",
                        plugin.config.getString("DrugIds." + itemi
                                + ".DrugName"));
            } else {
                death = plugin.config.getString("Chat.Broadcast.Death").replaceAll("%drugname%",
                        plugin.config.getString("DrugIds." + itemi
                                + ":" + dura + ".DrugName"));
            }
            death = death.replaceAll("%playername%", p.getName());
            Bukkit.broadcastMessage(plugin.colorize(death));
        }
        p.setHealth(0);
    }

    public void doHeartAttack(final Player p) {
        Material itemi = p.getItemInHand().getType();
        short dura = p.getItemInHand().getDurability();
        String heartAttack;

        if (plugin.config.getBoolean("Options.EnableBroadcastMessages")) {
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
        heartattack.add(p.getName());

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                new Runnable() {
                    @Override
                    public void run() {
                        heartattack.remove(p.getName());
                    }
                }, 100);
        if (p.getHealth() < 2) {
            p.setHealth(2);
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,
                new Runnable() {
                    @Override
                    public void run() {
                        if (heartattack.contains(p.getName())) {
                            if (p.getHealth() < 2) {
                                p.setHealth(2);
                            } else {
                                p.setHealth(1);
                            }
                        }
                    }
                }, 0L, 3L);
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

    private int filterInt(int i, int min) {
        return (i < min) ? min : i;
    }

    private void removeEffects(final Player p, final boolean jump, int time) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                new Runnable() {
                    public void run() {
                        onDrugs.remove(p.getName());
                        if (jump) {
                            isJump.remove(p.getName());
                        }
                    }
                }, time);
    }

    /**
     * @return A collection of all drunk players
     */
    public Collection<String> getDrunk() {
        return this.drunk;
    }

    /**
     * @return A collection of all players on drugs
     */
    public Collection<String> getOnDrugs() {
        return this.onDrugs;
    }

    /**
     * @return A collection of all players having a heart attack
     */
    public Collection<String> getHeartAttack() {
        return this.heartattack;
    }

    /**
     * @return A collection of all players that have high-jump
     */
    public Collection<String> getIsJump() {
        return this.isJump;
    }

    /**
     * @return A collection of all players who can't place blocks
     */
    public Collection<String> getNoPlace() {
        return noPlace;
    }
}
