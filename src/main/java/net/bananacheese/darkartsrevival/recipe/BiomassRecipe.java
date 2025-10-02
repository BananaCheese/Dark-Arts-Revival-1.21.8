package net.bananacheese.darkartsrevival.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.bananacheese.darkartsrevival.item.custom.SoulSyringe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class BiomassRecipe extends SpecialCraftingRecipe {

    public BiomassRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        ItemStack syringeStack = ItemStack.EMPTY;
        int biomassCount = 0;

        // Check if we have exactly 1 syringe at level 3 and at least 1 biomass item
        for (ItemStack stack : input.getStacks()) {
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof SoulSyringe) {
                    if (!syringeStack.isEmpty()) {
                        return false; // More than one syringe
                    }
                    syringeStack = stack;
                } else if (isBiomassItem(stack)) {
                    biomassCount++;
                }
            }
        }

        // Must have exactly 1 syringe at level 3 and at least 1 biomass
        boolean matches = !syringeStack.isEmpty()
                && SoulSyringe.getFillLevel(syringeStack) == 3
                && biomassCount > 0;

        // Debug logging
        if (!syringeStack.isEmpty()) {
            System.out.println("BiomassRecipe - Syringe level: " + SoulSyringe.getFillLevel(syringeStack) + ", Biomass count: " + biomassCount + ", Matches: " + matches);
        }

        return matches;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        ItemStack syringeStack = ItemStack.EMPTY;

        // Find the syringe
        for (ItemStack stack : input.getStacks()) {
            if (stack.getItem() instanceof SoulSyringe) {
                syringeStack = stack.copy();
                break;
            }
        }

        if (!syringeStack.isEmpty()) {
            // Set to level 4 (fully filled)
            SoulSyringe.setFillLevel(syringeStack, 4);
            System.out.println("BiomassRecipe - Crafted! New level: " + SoulSyringe.getFillLevel(syringeStack));
        }

        return syringeStack;
    }

    public boolean fits(int width, int height) {
        return width * height >= 2; // At least 2 slots (syringe + biomass)
    }

    @Override
    public RecipeSerializer<BiomassRecipe> getSerializer() {
        return DARecipeSerializers.BIOMASS_RECIPE;
    }

    // Helper method to check if an item is biomass
    private boolean isBiomassItem(ItemStack stack) {
        // Check meat items (more effective)
        if (stack.isOf(Items.BEEF) || stack.isOf(Items.COOKED_BEEF) ||
                stack.isOf(Items.PORKCHOP) || stack.isOf(Items.COOKED_PORKCHOP) ||
                stack.isOf(Items.MUTTON) || stack.isOf(Items.COOKED_MUTTON) ||
                stack.isOf(Items.CHICKEN) || stack.isOf(Items.COOKED_CHICKEN) ||
                stack.isOf(Items.RABBIT) || stack.isOf(Items.COOKED_RABBIT) ||
                stack.isOf(Items.COD) || stack.isOf(Items.COOKED_COD) ||
                stack.isOf(Items.SALMON) || stack.isOf(Items.COOKED_SALMON) ||
                stack.isOf(Items.ROTTEN_FLESH)) {
            return true;
        }

        // Check plant matter (less effective)
        if (stack.isOf(Items.WHEAT) || stack.isOf(Items.WHEAT_SEEDS) ||
                stack.isOf(Items.CARROT) || stack.isOf(Items.POTATO) ||
                stack.isOf(Items.BEETROOT) || stack.isOf(Items.BEETROOT_SEEDS) ||
                stack.isOf(Items.APPLE) || stack.isOf(Items.MELON_SLICE) ||
                stack.isOf(Items.PUMPKIN) || stack.isOf(Items.KELP) ||
                stack.isOf(Items.SWEET_BERRIES)) {
            return true;
        }

        return false;
    }

    public static class Serializer implements RecipeSerializer<BiomassRecipe> {
        public static final MapCodec<BiomassRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        CraftingRecipeCategory.CODEC.fieldOf("category").forGetter(SpecialCraftingRecipe::getCategory)
                ).apply(instance, BiomassRecipe::new)
        );

        public static final PacketCodec<RegistryByteBuf, BiomassRecipe> PACKET_CODEC =
                PacketCodec.tuple(
                        CraftingRecipeCategory.PACKET_CODEC, SpecialCraftingRecipe::getCategory,
                        BiomassRecipe::new
                );

        @Override
        public MapCodec<BiomassRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, BiomassRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}