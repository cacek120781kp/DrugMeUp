package me.giinger.dmu;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;

public class DrugHandler {

    private DrugMeUp plugin;
    private HashMap<ItemStack, Drug> drugs = new HashMap<>();
    private FileConfiguration config;

    DrugHandler(DrugMeUp plugin) {
        this.plugin = plugin;
        this.config = plugin.config;
    }

    /**
     * Get Drug instance from ItemStack
     *
     * @param item The ItemStack
     * @return The Drug instance
     */
    public Drug getDrug(ItemStack item) {
        for (Drug drug : getDrugs()) {
            if (drug.getItemStack().getType().name().equalsIgnoreCase(item.getType().name())) {
                return drug;
            }
        }
        return null;
    }

    /**
     * Get Drug instance from Material
     *
     * @param material The Material
     * @return The Drug instance
     */

    public Drug getDrug(Material material) {
        for (Drug drug : getDrugs()) {
            if (drug.getItemStack().getType() == material) {
                return drug;
            }
        }
        return null;
    }

    /**
     * Get Drug instance from name
     *
     * @param name The name
     * @return The drug instance
     */
    public Drug getDrug(String name) {
        for (Drug drug : getDrugs()) {
            if (drug.getName().equalsIgnoreCase(name)) {
                return drug;
            }
        }
        return null;
    }

    /**
     * Get the drug's name
     *
     * @param drug The drug
     * @return The drugs name
     */
    public String getDrugName(Drug drug) {
        return drug.getName();
    }

    /**
     * Get the list of Drugs
     *
     * @return A collection of Drugs
     */
    public Collection<Drug> getDrugs() {
        return drugs.values();
    }

    /**
     * Check if the ItemStack is a Drug
     *
     * @param item The ItemStack
     * @return If the ItemStack is a Drug
     */
    public boolean isDrug(ItemStack item) {
        for (Drug drug : getDrugs()) {
            Material mat = drug.getItemStack().getType();
            short dmg = drug.getItemStack().getDurability();
            if (mat.name().equalsIgnoreCase(item.getType().name()) && dmg == item.getDurability()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the Material is a Drug
     *
     * @param mat The Material
     * @return If the Material is a Drug
     */
    public boolean isDrug(Material mat, short dmg) {
        for (Drug drug : getDrugs()) {
            Material drugMat = drug.getItemStack().getType();
            short drugDmg = drug.getItemStack().getDurability();
            if (drugMat == mat && drugDmg == dmg) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gather a collection full of Drugs
     */
    void gatherDrugs() {
        for (String key : config.getConfigurationSection("DrugIds").getKeys(false)) {
            String path = "DrugIds." + key + ".";
            if (!drugProblem(key)) {
                Material mat = Material.getMaterial(key.split(":")[0]);
                short dmg = (key.split(":").length == 1) ? 0 : Short.parseShort(key.split(":")[1]);
                ItemStack item = new ItemStack(mat, 1, dmg);
                String name = config.getString(path + "DrugName");
                String message = (config.getString(path + "Message") != null) ? config.getString(path + "Message") : "";
                String[] effects = config.getString(path + "Effect").replaceAll(" ", "").split(",");
                String[] negatives = config.getString(path + "Negatives").replaceAll(" ", "").split(",");
                int negChance = (config.getInt(path + "NegChance") != 0) ? config.getInt(path + "NegChance") : 0;
                boolean type = (config.getString(path + "Type") == null) || config.getString(path + "Type")
                        .equalsIgnoreCase("All");
                boolean smoke = config.getBoolean(path + "Smoke");
                boolean negative = (negChance != 0);
                boolean sneak = config.getBoolean(path + "MustSneak");
                boolean edible = item.getType().isEdible() || item.getType().name().equalsIgnoreCase("POTION");
                drugs.put(item, new Drug(item, name, message, effects, negatives, negChance, type, smoke, negative,
                        sneak, edible));
            } else {
                plugin.log.info("Problem loading drug '" + key + "'!");
            }
        }
    }

    /**
     * Check for problems with the current drug in the config
     *
     * @param key The path to the current drug
     * @return If there's a problem with any of the drug's vars
     */
    private boolean drugProblem(String key) {
        String path = "DrugIds." + key + ".";
        return config.getString(path + "DrugName") == null || config.getString(path + "Effect") == null ||
                config.getString(path + "Negatives") == null || config.getString(path + "Type") == null;
    }

    /**
     * Clear the collection of drugs
     */
    void clearDrugs() {
        drugs.clear();
    }
}
