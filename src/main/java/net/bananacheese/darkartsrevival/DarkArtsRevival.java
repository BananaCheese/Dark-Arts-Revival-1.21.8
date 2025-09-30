package net.bananacheese.darkartsrevival;

import net.bananacheese.darkartsrevival.blocks.DABlocks;
import net.bananacheese.darkartsrevival.blocks.entity.DABlockEntities;
import net.bananacheese.darkartsrevival.commands.ReviveCommand;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DarkArtsRevival implements ModInitializer {
	public static final String MOD_ID = "darkartsrevival";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        DABlocks.registerModBlocks();
        DABlockEntities.registerBlockEntities();
        ReviveCommand.register();
    }
}