package net.bananacheese.darkartsrevival.datagen;

import net.bananacheese.darkartsrevival.block.DABlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class DALootTableProvider extends FabricBlockLootTableProvider {
    public DALootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(DABlocks.ALTER);
        addDrop(DABlocks.PEDESTAL);
        addDrop(DABlocks.DARK_BARRIER);
        addDrop(DABlocks.CLOUD_BLOCK);
    }
}
