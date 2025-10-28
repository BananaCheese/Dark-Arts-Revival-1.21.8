package net.bananacheese.darkartsrevival;

import net.bananacheese.darkartsrevival.datagen.DABlockTagProvider;
import net.bananacheese.darkartsrevival.datagen.DALootTableProvider;
import net.bananacheese.darkartsrevival.datagen.DAModelProvider;
import net.bananacheese.darkartsrevival.datagen.DARecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DarkArtsRevivalDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(DABlockTagProvider::new);
        pack.addProvider(DALootTableProvider::new);
        pack.addProvider(DAModelProvider::new);
        pack.addProvider(DARecipeProvider::new);
	}
}
