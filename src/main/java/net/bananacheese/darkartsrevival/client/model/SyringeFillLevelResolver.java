package net.bananacheese.darkartsrevival.client.model;

import com.mojang.serialization.MapCodec;
import net.bananacheese.darkartsrevival.component.DAComponents;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SyringeFillLevelResolver implements NumericProperty {
    public static final MapCodec<SyringeFillLevelResolver> CODEC = MapCodec.unit(new SyringeFillLevelResolver());

    @Override
    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
        return stack.getOrDefault(DAComponents.SYRINGE_FILL_LEVEL, 0);
    }

    @Override
    public MapCodec<? extends NumericProperty> getCodec() {
        return CODEC;
    }
}