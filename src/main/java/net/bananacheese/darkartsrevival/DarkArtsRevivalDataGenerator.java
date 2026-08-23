package net.bananacheese.darkartsrevival;

import net.bananacheese.darkartsrevival.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

public class DarkArtsRevivalDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(DABlockTagProvider::new);
        pack.addProvider(DALootTableProvider::new);
        pack.addProvider(DAModelProvider::new);
        pack.addProvider(DARecipeProvider::new);
        pack.addProvider(DARitualRecipeProvider::new);

        pack.addProvider((output, registries) ->
                new FabricDynamicRegistryProvider(output, registries) {
                    @Override
                    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
                        entries.addAll(registries.getOrThrow(RegistryKeys.BIOME));
                    }
                    @Override public String getName() { return "LushLands Biomes"; }
                });
    }
}
