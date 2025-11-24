package net.bananacheese.darkartsrevival.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

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

    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("§9" + componentType.getDisplayName()));
        if (defenseBonus > 0) {
            tooltip.add(Text.literal("§a+%d Defense".formatted(defenseBonus)));
        }
        if (durabilityBonus > 0) {
            tooltip.add(Text.literal("§a+%d Durability".formatted(durabilityBonus)));
        }
        tooltip.add(Text.literal("§8Compatible: " + componentType.getCompatibility()));
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

        public boolean isCompatibleWith(ArmorCoreItem.ArmorCoreType coreType) {
            if (compatibility.equals("All")) return true;
            return compatibility.equalsIgnoreCase(coreType.getDisplayName());
        }
    }
}