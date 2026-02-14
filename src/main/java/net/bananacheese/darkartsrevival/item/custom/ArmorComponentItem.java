package net.bananacheese.darkartsrevival.item.custom;

import net.minecraft.item.Item;


public class ArmorComponentItem extends Item {
    private final ComponentType componentType;
    private final String componentGroup; // For preventing duplicates (e.g., "pauldrons", "base_plate")
    private final int defenseBonus;
    private final int durabilityBonus;
    private final double toughnessBonus;

    public ArmorComponentItem(Settings settings, ComponentType componentType, String componentGroup,
                              int defenseBonus, int durabilityBonus, double toughnessBonus) {
        super(settings);
        this.componentType = componentType;
        this.componentGroup = componentGroup;
        this.defenseBonus = defenseBonus;
        this.durabilityBonus = durabilityBonus;
        this.toughnessBonus = toughnessBonus;
    }

    // Convenience constructor without toughness (defaults to 0)
    public ArmorComponentItem(Settings settings, ComponentType componentType, String componentGroup,
                              int defenseBonus, int durabilityBonus) {
        this(settings, componentType, componentGroup, defenseBonus, durabilityBonus, 0.0);
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public String getComponentGroup() {
        return componentGroup;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public int getDurabilityBonus() {
        return durabilityBonus;
    }

    public double getToughnessBonus() {
        return toughnessBonus;
    }

    public enum ComponentType {
        // Helmet components
        VISOR("Visor", "Helmet"),
        PLUME("Plume", "Helmet"),

        // Chestplate components
        PAULDRONS("Pauldrons", "Chestplate"),
        GORGET("Gorget", "Chestplate"),
        FAULD("Fauld", "Chestplate"),

        // Leggings components
        TASSET("Tasset", "Leggings"),
        GREAVES("Greaves", "Leggings"),

        // Boots components
        SPURS("Spurs", "Boots"),
        SABATONS("Sabatons", "Boots"),

        // Universal components
        REINFORCEMENT("Reinforcement", "All");

        private final String displayName;
        private final String compatibility;

        ComponentType(String displayName, String compatibility) {
            this.displayName = displayName;
            this.compatibility = compatibility;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getCompatibility() {
            return compatibility;
        }

        public boolean isCompatibleWith(ArmorFrameItem.ArmorFrameType frameType) {
            if (compatibility.equals("All")) return true;
            return compatibility.equalsIgnoreCase(frameType.getDisplayName());
        }
    }
}