package net.bananacheese.darkartsrevival.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

import java.util.List;

/**
 * Input for ritual recipes containing:
 * - Current altar tier
 * - Item in the altar
 * - Fluid amount in altar
 * - Items in surrounding pedestals
 */
public record RitualRecipeInput(
        int tier,
        ItemStack altarItem,
        int fluidAmount,
        List<ItemStack> pedestalItems
) implements RecipeInput {

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot == 0) {
            return altarItem;
        }
        int pedestalIndex = slot - 1;
        if (pedestalIndex >= 0 && pedestalIndex < pedestalItems.size()) {
            return pedestalItems.get(pedestalIndex);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1 + pedestalItems.size();
    }

    public int getTier() {
        return tier;
    }

    public ItemStack getAltarItem() {
        return altarItem;
    }

    public int getFluidAmount() {
        return fluidAmount;
    }

    public List<ItemStack> getPedestalItems() {
        return pedestalItems;
    }
}