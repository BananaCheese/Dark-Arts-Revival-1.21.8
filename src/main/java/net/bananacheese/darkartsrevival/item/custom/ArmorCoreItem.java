package net.bananacheese.darkartsrevival.item.custom;

import net.minecraft.item.Item;

public class ArmorCoreItem extends Item {
    private final ArmorCoreType coreType;

    public ArmorCoreItem(Settings settings, ArmorCoreType coreType) {
        super(settings);
        this.coreType = coreType;
    }

    public ArmorCoreType getCoreType() {
        return coreType;
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