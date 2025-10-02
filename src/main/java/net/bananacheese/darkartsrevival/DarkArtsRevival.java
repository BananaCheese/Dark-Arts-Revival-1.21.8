package net.bananacheese.darkartsrevival;

import net.bananacheese.darkartsrevival.block.DABlocks;
import net.bananacheese.darkartsrevival.block.entity.DABlockEntities;
import net.bananacheese.darkartsrevival.commands.ReviveCommand;
import net.bananacheese.darkartsrevival.component.DAComponents;
import net.bananacheese.darkartsrevival.item.DAItemGroup;
import net.bananacheese.darkartsrevival.item.DAItems;
import net.bananacheese.darkartsrevival.recipe.DARecipeSerializers;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DarkArtsRevival implements ModInitializer {
	public static final String MOD_ID = "darkartsrevival";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        DAComponents.registerComponents();
        DAItemGroup.registerItemGroups();
        DABlocks.registerModBlocks();
        DABlockEntities.registerBlockEntities();
        DAItems.registerModItems();
        DARecipeSerializers.registerRecipeSerializers();
        ReviveCommand.register();
    }
}