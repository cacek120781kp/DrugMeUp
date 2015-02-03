package me.giinger.dmu;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@SuppressWarnings("UnusedDeclaration")
public class Drug {
    private ItemStack itemStack;
    private String name;
    private String message;
    private String[] effects;
    private String[] negatives;
    private int negativeChance;
    private boolean type;
    private boolean smoke;
    private boolean negative;
    private boolean sneak;
    private boolean edible;

    public Drug(ItemStack itemStack, String name, String message, String[] effects,
                String[] negatives, int negativeChance, boolean type, boolean smoke, boolean negative,
                boolean sneak, boolean edible) {
        this.itemStack = itemStack;
        this.name = name;
        this.message = message;
        this.effects = effects;
        this.negatives = negatives;
        this.negativeChance = negativeChance;
        this.type = type;
        this.smoke = smoke;
        this.negative = negative;
        this.sneak = sneak;
        this.edible = edible;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Integer> getEffects() {
        ArrayList<Integer> effects = new ArrayList<>();
        for (String s : this.effects) {
            effects.add(Integer.parseInt(s));
        }
        return effects;
    }

    public ArrayList<Integer> getNegatives() {
        ArrayList<Integer> negatives = new ArrayList<>();
        for (String s : this.negatives) {
            negatives.add(Integer.parseInt(s));
        }
        return negatives;
    }

    public int getNegativeChance() {
        return negativeChance;
    }

    public void setNegativeChance(int negativeChance) {
        this.negativeChance = negativeChance;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public boolean isSmoke() {
        return smoke;
    }

    public void setSmoke(boolean smoke) {
        this.smoke = smoke;
    }

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    public boolean isSneak() {
        return sneak;
    }

    public void setSneak(boolean sneak) {
        this.sneak = sneak;
    }

    public boolean isEdible() {
        return edible;
    }

    public void setEdible(boolean edible) {
        this.edible = edible;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}