package net.bananacheese.darkartsrevival.recipe;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class DARecipes {

    public static void registerRecipes() {
        // Regular ritual recipes
        Registry.register(Registries.RECIPE_SERIALIZER,
                Identifier.of(DarkArtsRevival.MOD_ID, RitualRecipeType.ID),
                RitualRecipeSerializer.INSTANCE);

        Registry.register(Registries.RECIPE_TYPE,
                Identifier.of(DarkArtsRevival.MOD_ID, RitualRecipeType.ID),
                RitualRecipeType.INSTANCE);

        // Revival ritual recipes
        Registry.register(Registries.RECIPE_SERIALIZER,
                Identifier.of(DarkArtsRevival.MOD_ID, RevivalRitualRecipeType.ID),
                RevivalRitualRecipeSerializer.INSTANCE);

        Registry.register(Registries.RECIPE_TYPE,
                Identifier.of(DarkArtsRevival.MOD_ID, RevivalRitualRecipeType.ID),
                RevivalRitualRecipeType.INSTANCE);

        DarkArtsRevival.LOGGER.info("Registering Ritual Recipes for " + DarkArtsRevival.MOD_ID);
    }
}