package net.bananacheese.darkartsrevival;

import net.bananacheese.darkartsrevival.block.entity.DABlockEntities;
import net.bananacheese.darkartsrevival.block.entity.renderer.AlterBlockEntityRenderer;
import net.bananacheese.darkartsrevival.client.model.SyringeFillLevelResolver;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.item.property.numeric.NumericProperties;
import net.minecraft.util.Identifier;

public class DarkArtsRevivalClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(DABlockEntities.ALTER_BE, AlterBlockEntityRenderer::new);

        // Register the custom syringe fill level property in the ID_MAPPER
        NumericProperties.ID_MAPPER.put(
                Identifier.of(DarkArtsRevival.MOD_ID, "syringe_fill_level"),
                SyringeFillLevelResolver.CODEC
        );
    }
}