package net.bananacheese.darkartsrevival;

import net.bananacheese.darkartsrevival.blocks.entity.DABlockEntities;
import net.bananacheese.darkartsrevival.blocks.entity.renderer.AlterBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class DarkArtsRevivalClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
        BlockEntityRendererFactories.register(DABlockEntities.ALTER_BE, AlterBlockEntityRenderer::new);
	}
}