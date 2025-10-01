package net.bananacheese.darkartsrevival.datagen;

import net.bananacheese.darkartsrevival.blocks.DABlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class DARecipeProvider extends FabricRecipeProvider {
    public DARecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {
                createShaped(RecipeCategory.MISC, DABlocks.ALTER)
                        .pattern("COC")
                        .pattern("COC")
                        .pattern("CCC")
                        .input('C', Items.COBBLESTONE).input('O', Items.CRYING_OBSIDIAN)
                        .criterion(hasItem(Items.CRYING_OBSIDIAN), conditionsFromItem(Items.CRYING_OBSIDIAN))
                        .offerTo(exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "Dark Arts Revival Recipes";
    }
}
