package net.bananacheese.darkartsrevival.client.model;

import com.mojang.serialization.MapCodec;
import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.bananacheese.darkartsrevival.item.custom.ArmorFrameItem;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Custom numeric property that calculates a unique value based on attached components
 * This value is used by model overrides to select the correct texture variant
 *
 * Uses hash-based calculation that matches ArmorModelJsonGenerator
 */
public class ArmorComponentsProperty implements NumericProperty {

    public static final MapCodec<ArmorComponentsProperty> CODEC =
            MapCodec.unit(new ArmorComponentsProperty());

    @Override
    public float getValue(ItemStack stack, @Nullable ClientWorld world,
                          @Nullable LivingEntity entity, int seed) {
        if (!(stack.getItem() instanceof ArmorFrameItem)) {
            return 0.0f;
        }

        // Get the components attached to this frame
        List<ArmorFrameItem.ComponentData> components = ArmorFrameItem.getComponents(stack);

        if (components.isEmpty()) {
            return 0.0f; // No components = use base texture
        }

        // Calculate a unique value based on the component combination
        float value = calculatePredicateValue(components);

        // DEBUG: Log the calculated value
        DarkArtsRevival.LOGGER.info("ArmorComponentsProperty: Item=" + stack.getItem().getName().getString() +
                ", Components=" + components.size() +
                ", Value=" + value);

        return value;
    }

    /**
     * Calculate a unique predicate value for a component combination
     * Must match the calculation in ArmorModelJsonGenerator
     */
    private float calculatePredicateValue(List<ArmorFrameItem.ComponentData> components) {
        // Extract component IDs and sort them
        List<String> componentNames = new ArrayList<>();
        for (ArmorFrameItem.ComponentData comp : components) {
            // Extract just the item name from the full ID
            String id = comp.id();
            String itemName = id.substring(id.lastIndexOf(':') + 1);
            componentNames.add(itemName);
        }

        // Sort to ensure consistent ordering
        Collections.sort(componentNames);

        // Create a string representation
        String combined = String.join("|", componentNames);

        // Hash it to a float between 0.0 and 1.0
        int hash = combined.hashCode();
        float value = (Math.abs(hash) % 10000) / 10000f;

        // Ensure it's not 0.0 (which is the default/base model)
        return Math.max(value, 0.0001f);
    }

    @Override
    public MapCodec<? extends NumericProperty> getCodec() {
        return CODEC;
    }
}