package net.bananacheese.darkartsrevival.recipe;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class DARecipeSerializers {

    public static final RecipeSerializer<BiomassRecipe> BIOMASS_RECIPE =
            Registry.register(
                    Registries.RECIPE_SERIALIZER,
                    Identifier.of(DarkArtsRevival.MOD_ID, "biomass"),
                    new BiomassRecipe.Serializer()
            );

    public static void registerRecipeSerializers() {
        DarkArtsRevival.LOGGER.info("Registering Recipe Serializers for " + DarkArtsRevival.MOD_ID);
    }
}
