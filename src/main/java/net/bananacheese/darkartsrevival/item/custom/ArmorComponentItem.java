package net.bananacheese.darkartsrevival.item.custom;

import net.minecraft.item.Item;


public class ArmorComponentItem extends Item {
    private final ComponentType componentType;
    private final int defenseBonus;
    private final int durabilityBonus;

    public ArmorComponentItem(Settings settings, ComponentType componentType, int defenseBonus, int durabilityBonus) {
        super(settings);
        this.componentType = componentType;
        this.defenseBonus = defenseBonus;
        this.durabilityBonus = durabilityBonus;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public int getDurabilityBonus() {
        return durabilityBonus;
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