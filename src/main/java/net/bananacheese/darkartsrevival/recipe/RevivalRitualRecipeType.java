package net.bananacheese.darkartsrevival.recipe;

import net.minecraft.recipe.RecipeType;

public class RevivalRitualRecipeType implements RecipeType<RevivalRitualRecipe> {
    public static final RevivalRitualRecipeType INSTANCE = new RevivalRitualRecipeType();
    public static final String ID = "revival_ritual";

    private RevivalRitualRecipeType() {}
}