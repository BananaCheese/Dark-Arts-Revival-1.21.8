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
 * Special revival ritual recipe that also checks for a target player in the Soul Syringe
 * This extends RitualRecipe but adds custom validation for revival-specific requirements
 */
public class RevivalRitualRecipe implements Recipe<RitualRecipeInput> {
    private final int minTier;
    private final Ingredient altarItem;
    private final int fluidAmount;
    private final List<Ingredient> pedestalItems;

    public RevivalRitualRecipe(int minTier, Ingredient altarItem, int fluidAmount,
                               List<Ingredient> pedestalItems, ItemStack result) {
        this.minTier = minTier;
        this.altarItem = altarItem;
        this.fluidAmount = fluidAmount;
        this.pedestalItems = pedestalItems;
        // Result is ignored for revival rituals
    }

    @Override
    public boolean matches(RitualRecipeInput input, World world) {
        // Check tier requirement
        if (input.getTier() < minTier) {
            return false;
        }

        // Check altar item is a Soul Syringe
        if (!altarItem.test(input.getAltarItem())) {
            return false;
        }

        // Check if syringe is fully charged (fill level 4)
        ItemStack altarStack = input.getAltarItem();
        if (altarStack.getItem() instanceof net.bananacheese.darkartsrevival.item.custom.SoulSyringe) {
            int fillLevel = net.bananacheese.darkartsrevival.item.custom.SoulSyringe.getFillLevel(altarStack);
            if (fillLevel < 4) {
                return false;
            }

            // Check if syringe has target player set
            java.util.UUID targetUuid = net.bananacheese.darkartsrevival.item.custom.SoulSyringe.getTargetPlayer(altarStack);
            if (targetUuid == null) {
                return false;
            }
        } else {
            return false;
        }

        // Check fluid amount
        if (input.getFluidAmount() < fluidAmount) {
            return false;
        }

        // Check pedestal items (shapeless matching)
        List<ItemStack> pedestalStacks = input.getPedestalItems();

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
        // Revival doesn't produce an item in the altar - it revives a player
        return ItemStack.EMPTY;
    }

    public boolean fits(int width, int height) {
        return true;
    }

    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<RevivalRitualRecipe> getSerializer() {
        return RevivalRitualRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<RevivalRitualRecipe> getType() {
        return RevivalRitualRecipeType.INSTANCE;
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
        return ItemStack.EMPTY;
    }
}