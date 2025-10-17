package net.bananacheese.darkartsrevival.datagen;

import net.bananacheese.darkartsrevival.block.DABlocks;
import net.bananacheese.darkartsrevival.item.DAItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
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

                createShaped(RecipeCategory.MISC, DABlocks.PEDESTAL)
                        .pattern("ORO")
                        .pattern(" C ")
                        .pattern("CCC")
                        .input('C', Items.COBBLESTONE).input('O', Items.CRYING_OBSIDIAN).input('R', Items.RED_CARPET)
                        .criterion(hasItem(Items.CRYING_OBSIDIAN), conditionsFromItem(Items.CRYING_OBSIDIAN))
                        .offerTo(exporter);

                createShaped(RecipeCategory.MISC, DAItems.SOUL_SYRINGE)
                        .pattern(" GI")
                        .pattern(" XG")
                        .pattern("I  ")
                        .input('G', Items.GOLD_INGOT).input('I', Items.IRON_INGOT).input('X', Items.GLASS_PANE)
                        .criterion(hasItem(Items.GLASS_PANE), conditionsFromItem(Items.GOLD_INGOT))
                        .offerTo(exporter);
            }

        };
    }

    @Override
    public String getName() {
        return "Dark Arts Revival Recipes";
    }
}
