package net.bananacheese.darkartsrevival.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public class ArmorCoreItem extends Item {
    private final ArmorCoreType coreType;

    public ArmorCoreItem(Settings settings, ArmorCoreType coreType) {
        super(settings);
        this.coreType = coreType;
    }

    public ArmorCoreType getCoreType() {
        return coreType;
    }

    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("§7" + coreType.getDisplayName() + " Core"));
        tooltip.add(Text.literal("§8Place in Gear Forge to customize"));
    }

    public enum ArmorCoreType {
        HELMET("Helmet", 6),
        CHESTPLATE("Chestplate", 6),
        LEGGINGS("Leggings", 6),
        BOOTS("Boots", 6);

        private final String displayName;
        private final int upgradeSlots;

        ArmorCoreType(String displayName, int upgradeSlots) {
            this.displayName = displayName;
            this.upgradeSlots = upgradeSlots;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getUpgradeSlots() {
            return upgradeSlots;
        }
    }
}