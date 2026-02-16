package net.bananacheese.darkartsrevival;

import net.bananacheese.darkartsrevival.block.DABlocks;
import net.bananacheese.darkartsrevival.block.entity.DABlockEntities;
import net.bananacheese.darkartsrevival.block.entity.renderer.AlterBlockEntityRenderer;
import net.bananacheese.darkartsrevival.block.entity.renderer.PedestalBlockEntityRenderer;
import net.bananacheese.darkartsrevival.client.BarrierParticleRenderer;
import net.bananacheese.darkartsrevival.client.model.ArmorComponentsProperty;
import net.bananacheese.darkartsrevival.client.model.SyringeFillLevelResolver;
import net.bananacheese.darkartsrevival.screen.DAScreenHandlers;
import net.bananacheese.darkartsrevival.screen.GearForgeScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.item.property.numeric.NumericProperties;
import net.minecraft.util.Identifier;

public class DarkArtsRevivalClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register custom properties FIRST before anything else loads models
        NumericProperties.ID_MAPPER.put(
                Identifier.of(DarkArtsRevival.MOD_ID, "syringe_fill_level"),
                SyringeFillLevelResolver.CODEC
        );

        NumericProperties.ID_MAPPER.put(
                Identifier.of(DarkArtsRevival.MOD_ID, "components"),
                ArmorComponentsProperty.CODEC
        );

        BlockEntityRendererFactories.register(DABlockEntities.ALTER_BE, AlterBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(DABlockEntities.PEDESTAL_BE, PedestalBlockEntityRenderer::new);

        HandledScreens.register(DAScreenHandlers.GEAR_FORGE_SCREEN_HANDLER, GearForgeScreen::new);

        // Register barrier particle renderer
        BarrierParticleRenderer.register();

        BlockRenderLayerMap.putBlock(DABlocks.CLOUD_BLOCK, BlockRenderLayer.TRANSLUCENT);

        // Clear texture cache when resources reload
        registerResourceReloadListener();
    }

    private void registerResourceReloadListener() {
        // Clear cache when resource packs reload
        net.fabricmc.fabric.api.resource.ResourceManagerHelper.get(net.minecraft.resource.ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(new net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener() {
                    @Override
                    public net.minecraft.util.Identifier getFabricId() {
                        return net.minecraft.util.Identifier.of(DarkArtsRevival.MOD_ID, "dynamic_texture_cache");
                    }

                    @Override
                    public void reload(net.minecraft.resource.ResourceManager manager) {
                        net.bananacheese.darkartsrevival.client.renderer.DynamicTextureManager.clearCache();
                    }
                });
    }
}