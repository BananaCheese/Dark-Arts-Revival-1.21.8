package net.bananacheese.darkartsrevival.recipe;

import net.minecraft.recipe.RecipeType;

public class RitualRecipeType implements RecipeType<RitualRecipe> {
    public static final RitualRecipeType INSTANCE = new RitualRecipeType();
    public static final String ID = "ritual";

    private RitualRecipeType() {}
}