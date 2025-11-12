package net.bananacheese.darkartsrevival.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

/**
 * Represents a ritual recipe that requires:
 * - A minimum altar tier
 * - An item in the altar
 * - Optional fluid requirement
 * - Optional items in surrounding pedestals (shapeless)
 * - Produces a result item in the altar
 */
public class RitualRecipe implements Recipe<RitualRecipeInput> {
    private final int minTier;
    private final Ingredient altarItem;
    private final int fluidAmount;
    private final List<Ingredient> pedestalItems;
    private final ItemStack result;
    private final String effectName;

    public RitualRecipe(int minTier, Ingredient altarItem, int fluidAmount,
                        List<Ingredient> pedestalItems, ItemStack result) {
        this(minTier, altarItem, fluidAmount, pedestalItems, result, "");
    }

    public RitualRecipe(int minTier, Ingredient altarItem, int fluidAmount,
                        List<Ingredient> pedestalItems, ItemStack result, String effectName) {
        this.minTier = minTier;
        this.altarItem = altarItem;
        this.fluidAmount = fluidAmount;
        this.pedestalItems = pedestalItems;
        this.result = result;
        this.effectName = effectName;
    }

    @Override
    public boolean matches(RitualRecipeInput input, World world) {
        // Check tier requirement
        if (input.getTier() < minTier) {
            return false;
        }

        // Check altar item
        if (!altarItem.test(input.getAltarItem())) {
            return false;
        }

        // Check fluid amount
        if (input.getFluidAmount() < fluidAmount) {
            return false;
        }

        // Check pedestal items (shapeless matching)
        List<ItemStack> pedestalStacks = input.getPedestalItems();

        // Must have at least the required number of pedestals
        if (pedestalStacks.size() < pedestalItems.size()) {
            return false;
        }

        // Create a mutable copy for matching
        List<ItemStack> remainingStacks = new java.util.ArrayList<>(pedestalStacks);

        // Try to match each required ingredient
        for (Ingredient ingredient : pedestalItems) {
            boolean found = false;
            for (int i = 0; i < remainingStacks.size(); i++) {
                if (ingredient.test(remainingStacks.get(i))) {
                    remainingStacks.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack craft(RitualRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return result.copy();
    }

    public boolean fits(int width, int height) {
        return true;
    }

    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return result;
    }

    @Override
    public RecipeSerializer<RitualRecipe> getSerializer() {
        return RitualRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<RitualRecipe> getType() {
        return RitualRecipeType.INSTANCE;
    }

    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        ingredients.add(altarItem);
        ingredients.addAll(pedestalItems);
        return ingredients;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.NONE;
    }

    @Override
    public List<RecipeDisplay> getDisplays() {
        return List.of();
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return null;
    }

    // Getters
    public int getMinTier() {
        return minTier;
    }

    public Ingredient getAltarItem() {
        return altarItem;
    }

    public int getFluidAmount() {
        return fluidAmount;
    }

    public List<Ingredient> getPedestalItems() {
        return pedestalItems;
    }

    public ItemStack getResultItem() {
        return result;
    }

    public String getEffectName() {
        return effectName;
    }
}