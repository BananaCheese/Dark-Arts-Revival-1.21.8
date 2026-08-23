package net.bananacheese.darkartsrevival.datagen;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.bananacheese.darkartsrevival.item.DAItems;
import net.bananacheese.darkartsrevival.recipe.DARecipes;
import net.bananacheese.darkartsrevival.recipe.RevivalRitualRecipe;
import net.bananacheese.darkartsrevival.recipe.RitualRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DARitualRecipeProvider extends FabricRecipeProvider {

    public DARitualRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {
                // Example: Altar Analyzer Creation (Tier 1)
                createRitualRecipe(recipeExporter, "altar_analyzer_creation",
                        1, // Min tier
                        Items.GOLD_INGOT, // Altar item
                        1000, // Fluid amount (1 bucket)
                        List.of(), // No pedestal items
                        new ItemStack(DAItems.ALTER_ANALYZER),
                        "portal");

                // Example: Dark Ritual (Tier 4)
                // Complex ritual requiring multiple items
                createRitualRecipe(recipeExporter, "dark_crystal_creation",
                        4, // Min tier
                        Items.CRYING_OBSIDIAN, // Altar item
                        0, // Full water
                        List.of(
                                Ingredient.ofItems(Items.REDSTONE_BLOCK),
                                Ingredient.ofItems(Items.REDSTONE_BLOCK),
                                Ingredient.ofItems(Items.AMETHYST_BLOCK),
                                Ingredient.ofItems(Items.AMETHYST_BLOCK),
                                Ingredient.ofItems(Items.SCULK),
                                Ingredient.ofItems(Items.SCULK),
                                Ingredient.ofItems(Items.SCULK),
                                Ingredient.ofItems(Items.SCULK)
                        ),
                        new ItemStack(DAItems.DARK_CRYSTAL),
                        "dark");

                createRevivalRecipe(recipeExporter, "player_revival",
                        4, // Min tier
                        DAItems.SOUL_SYRINGE, // Altar item
                        1000, // Full water
                        List.of(
                                Ingredient.ofItems(Items.TOTEM_OF_UNDYING),
                                Ingredient.ofItems(Items.NETHER_STAR),
                                Ingredient.ofItems(Items.ENCHANTED_GOLDEN_APPLE),
                                Ingredient.ofItems(Items.ENCHANTED_GOLDEN_APPLE),
                                Ingredient.ofItems(Items.DIAMOND_BLOCK),
                                Ingredient.ofItems(Items.DIAMOND_BLOCK),
                                Ingredient.ofItems(Items.DIAMOND_BLOCK),
                                Ingredient.ofItems(Items.DIAMOND_BLOCK)
                        ));
            }
        };
    }

    /**
     * Helper method to create ritual recipes
     */
    private void createRitualRecipe(RecipeExporter exporter, String name,
                                    int minTier, ItemConvertible altarItem, int fluidAmount,
                                    List<Ingredient> pedestalItems, ItemStack result) {
        createRitualRecipe(exporter, name, minTier, altarItem, fluidAmount, pedestalItems, result, "");
    }

    /**
     * Helper method to create ritual recipes with custom effects
     */
    private void createRitualRecipe(RecipeExporter exporter, String name,
                                    int minTier, ItemConvertible altarItem, int fluidAmount,
                                    List<Ingredient> pedestalItems, ItemStack result, String effect) {
        RitualRecipe recipe = new RitualRecipe(
                minTier,
                Ingredient.ofItems(altarItem),
                fluidAmount,
                pedestalItems,
                result,
                effect
        );

        exporter.accept(
                net.minecraft.registry.RegistryKey.of(
                        net.minecraft.registry.RegistryKeys.RECIPE,
                        Identifier.of(DarkArtsRevival.MOD_ID, name)
                ),
                recipe,
                null
        );
    }

    /**
     * Helper method to create revival ritual recipes
     */
    private void createRevivalRecipe(RecipeExporter exporter, String name,
                                     int minTier, ItemConvertible altarItem, int fluidAmount,
                                     List<Ingredient> pedestalItems) {
        RevivalRitualRecipe recipe = new RevivalRitualRecipe(
                minTier,
                Ingredient.ofItems(altarItem),
                fluidAmount,
                pedestalItems,
                ItemStack.EMPTY // Revival doesn't produce an item
        );

        exporter.accept(
                net.minecraft.registry.RegistryKey.of(
                        net.minecraft.registry.RegistryKeys.RECIPE,
                        Identifier.of(DarkArtsRevival.MOD_ID, name)
                ),
                recipe,
                null
        );
    }

    @Override
    public String getName() {
        return "Dark Arts Revival Ritual Recipes";
    }
}