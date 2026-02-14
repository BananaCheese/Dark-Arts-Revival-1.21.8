package net.bananacheese.darkartsrevival.item.custom;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ArmorFrameItem extends Item {
    private final ArmorFrameType FrameType;
    private static final int BASE_DEFENSE = 2;
    private static final int BASE_DURABILITY = 100;

    public ArmorFrameItem(Item.Settings settings, ArmorFrameType FrameType) {
        super(settings
                // Make it equippable in the proper slot
                .component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(FrameType.getSlot()).build())
                // Set max damage
                .maxDamage(BASE_DURABILITY)
                // Set base armor attributes
                .component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createBaseAttributes(FrameType))
        );
        this.FrameType = FrameType;
    }

    /**
     * Creates the base attribute modifiers for the armor frame
     */
    private static AttributeModifiersComponent createBaseAttributes(ArmorFrameType FrameType) {
        List<AttributeModifiersComponent.Entry> modifiers = new ArrayList<>();
        EquipmentSlot slot = FrameType.getSlot();
        AttributeModifierSlot modifierSlot = AttributeModifierSlot.forEquipmentSlot(slot);

        // Create unique identifiers for each slot
        Identifier armorId = Identifier.of("darkartsrevival", "armor_frame_defense_" + slot.getName());
        Identifier toughnessId = Identifier.of("darkartsrevival", "armor_frame_toughness_" + slot.getName());

        // Add base armor modifier
        modifiers.add(new AttributeModifiersComponent.Entry(
                EntityAttributes.ARMOR,
                new EntityAttributeModifier(
                        armorId,
                        BASE_DEFENSE,
                        EntityAttributeModifier.Operation.ADD_VALUE
                ),
                modifierSlot
        ));

        // Add base toughness modifier
        double toughness = BASE_DEFENSE * 0.2;
        modifiers.add(new AttributeModifiersComponent.Entry(
                EntityAttributes.ARMOR_TOUGHNESS,
                new EntityAttributeModifier(
                        toughnessId,
                        toughness,
                        EntityAttributeModifier.Operation.ADD_VALUE
                ),
                modifierSlot
        ));

        return new AttributeModifiersComponent(modifiers);
    }

    public ArmorFrameType getFrameType() {
        return FrameType;
    }

    // Add component to frame
    public static boolean addComponent(ItemStack frameStack, String componentId, int defenseBonus, int durabilityBonus) {
        NbtCompound nbt = frameStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();

        if (!nbt.contains("Components")) {
            nbt.put("Components", new NbtList());
        }

        NbtList components = nbt.getList("Components").orElse(new NbtList());

        if (components.size() >= 6) {
            return false;
        }

        NbtCompound component = new NbtCompound();
        component.putString("Id", componentId);
        component.putInt("Defense", defenseBonus);
        component.putInt("Durability", durabilityBonus);

        components.add(component);
        nbt.put("Components", components);

        frameStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        return true;
    }

    public static List<ComponentData> getComponents(ItemStack FrameStack) {
        List<ComponentData> result = new ArrayList<>();
        NbtCompound nbt = FrameStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();

        if (nbt.contains("Components")) {
            NbtList components = nbt.getList("Components").orElse(new NbtList());
            for (int i = 0; i < components.size(); i++) {
                NbtCompound component = components.getCompound(i).orElse(new NbtCompound());
                result.add(new ComponentData(
                        component.getString("Id").orElse(""),
                        component.getInt("Defense").orElse(0),
                        component.getInt("Durability").orElse(0)
                ));
            }
        }

        return result;
    }

    public static int getComponentCount(ItemStack frameStack) {
        NbtCompound nbt = frameStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        if (nbt.contains("Components")) {
            return nbt.getList("Components").orElse(new NbtList()).size();
        }
        return 0;
    }

    public static int getTotalDefense(ItemStack frameStack) {
        int total = BASE_DEFENSE;
        for (ComponentData component : getComponents(frameStack)) {
            total += component.defenseBonus;
        }
        return total;
    }

    public static int getTotalDurability(ItemStack frameStack) {
        int total = BASE_DURABILITY;
        for (ComponentData component : getComponents(frameStack)) {
            total += component.durabilityBonus;
        }
        return total;
    }

    public enum ArmorFrameType {
        HELMET("Helmet", 6, EquipmentSlot.HEAD),
        CHESTPLATE("Chestplate", 6, EquipmentSlot.CHEST),
        LEGGINGS("Leggings", 6, EquipmentSlot.LEGS),
        BOOTS("Boots", 6, EquipmentSlot.FEET);

        private final String displayName;
        private final int upgradeSlots;
        private final EquipmentSlot slot;

        ArmorFrameType(String displayName, int upgradeSlots, EquipmentSlot slot) {
            this.displayName = displayName;
            this.upgradeSlots = upgradeSlots;
            this.slot = slot;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getUpgradeSlots() {
            return upgradeSlots;
        }

        public EquipmentSlot getSlot() {
            return slot;
        }
    }

    public record ComponentData(String id, int defenseBonus, int durabilityBonus) {}
}