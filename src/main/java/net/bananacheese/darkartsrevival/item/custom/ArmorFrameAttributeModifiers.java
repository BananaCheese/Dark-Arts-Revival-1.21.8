package net.bananacheese.darkartsrevival.item.custom;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to dynamically update armor attribute modifiers based on attached components
 */
public class ArmorFrameAttributeModifiers {

    /**
     * Updates the armor's attribute modifiers based on current component configuration
     */
    public static void updateAttributes(ItemStack stack) {
        if (!(stack.getItem() instanceof ArmorFrameItem frameItem)) {
            return;
        }

        int totalDefense = ArmorFrameItem.getTotalDefense(stack);
        int totalDurability = ArmorFrameItem.getTotalDurability(stack);
        double totalToughness = ArmorFrameItem.getTotalToughness(stack);

        // Update max damage based on total durability
        stack.set(DataComponentTypes.MAX_DAMAGE, totalDurability);

        // Create attribute modifiers list
        List<AttributeModifiersComponent.Entry> modifiers = new ArrayList<>();

        // Get slot info
        EquipmentSlot slot = frameItem.getFrameType().getSlot();
        AttributeModifierSlot modifierSlot = AttributeModifierSlot.forEquipmentSlot(slot);

        // Create unique identifiers for this slot
        Identifier armorId = Identifier.of("darkartsrevival", "armor_frame_defense_" + slot.getName());
        Identifier toughnessId = Identifier.of("darkartsrevival", "armor_frame_toughness_" + slot.getName());

        // Add armor defense modifier with unique identifier
        modifiers.add(new AttributeModifiersComponent.Entry(
                EntityAttributes.ARMOR,
                new EntityAttributeModifier(
                        armorId,
                        totalDefense,
                        EntityAttributeModifier.Operation.ADD_VALUE
                ),
                modifierSlot
        ));

        // Add armor toughness with unique identifier
        modifiers.add(new AttributeModifiersComponent.Entry(
                EntityAttributes.ARMOR_TOUGHNESS,
                new EntityAttributeModifier(
                        toughnessId,
                        totalToughness,
                        EntityAttributeModifier.Operation.ADD_VALUE
                ),
                modifierSlot
        ));

        // Set the attribute modifiers component
        stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS,
                new AttributeModifiersComponent(modifiers));
    }
}