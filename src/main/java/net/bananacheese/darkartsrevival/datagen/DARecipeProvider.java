package net.bananacheese.darkartsrevival.datagen;

import net.bananacheese.darkartsrevival.block.DABlocks;
import net.bananacheese.darkartsrevival.item.DAItems;
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
                        .criterion("has_crying_obsidian", conditionsFromItem(Items.CRYING_OBSIDIAN))
                        .offerTo(exporter);

                createShaped(RecipeCategory.MISC, DABlocks.PEDESTAL)
                        .pattern("ORO")
                        .pattern(" C ")
                        .pattern("CCC")
                        .input('C', Items.COBBLESTONE).input('O', Items.CRYING_OBSIDIAN).input('R', Items.RED_CARPET)
                        .criterion("has_crying_obsidian", conditionsFromItem(Items.CRYING_OBSIDIAN))
                        .offerTo(exporter);

                createShaped(RecipeCategory.MISC, DAItems.SOUL_SYRINGE)
                        .pattern(" GI")
                        .pattern(" XG")
                        .pattern("I  ")
                        .input('G', Items.GOLD_INGOT).input('I', Items.IRON_INGOT).input('X', Items.GLASS_PANE)
                        .criterion("has_gold_ingot", this.conditionsFromItem(Items.GOLD_INGOT))
                        .offerTo(exporter);

                createShaped(RecipeCategory.MISC, DABlocks.DARK_BARRIER, 8)
                        .pattern("BRB")
                        .pattern("ADA")
                        .pattern("BRB")
                        .input('B', Items.BLACKSTONE).input('R', Items.REDSTONE_BLOCK).input('A', Items.AMETHYST_BLOCK).input('D', DAItems.DARK_CRYSTAL)
                        .criterion("has_dark_crystal", this.conditionsFromItem(DAItems.DARK_CRYSTAL))
                        .offerTo(exporter);
            }

        };
    }

    @Override
    public String getName() {
        return "Dark Arts Revival Recipes";
    }
}
