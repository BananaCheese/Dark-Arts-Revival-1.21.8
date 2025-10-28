package net.bananacheese.darkartsrevival.block;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.bananacheese.darkartsrevival.block.custom.AlterBlock;
import net.bananacheese.darkartsrevival.block.custom.BarrierFieldBlock;
import net.bananacheese.darkartsrevival.block.custom.DarkBarrierBlock;
import net.bananacheese.darkartsrevival.block.custom.PedestalBlock;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class DABlocks {
    public static final Block ALTER = registerBlock("alter",
            properties -> new AlterBlock(properties.nonOpaque().solid().strength(2, 6)));

    public static final Block PEDESTAL = registerBlock("pedestal",
            properties -> new PedestalBlock(properties.nonOpaque().solid().strength(2, 6)));

    public static final Block DARK_BARRIER = registerBlock("dark_barrier",
            properties -> new DarkBarrierBlock(properties.strength(3, 9)));

    public static final Block BARRIER_FIELD = registerBlockWithoutBlockItem("barrier_field",
            properties -> new BarrierFieldBlock(properties.strength(-1.0F, 3600000.0F).dropsNothing().noCollision().nonOpaque()));

    private static Block registerBlock(String name, Function<AbstractBlock.Settings, Block> function) {
        Block toRegister = function.apply(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DarkArtsRevival.MOD_ID, name))));
        registerBlockItem(name, toRegister);
        return Registry.register(Registries.BLOCK, Identifier.of(DarkArtsRevival.MOD_ID, name), toRegister);
    }

    private static Block registerBlockWithoutBlockItem(String name, Function<AbstractBlock.Settings, Block> function) {
        return Registry.register(Registries.BLOCK, Identifier.of(DarkArtsRevival.MOD_ID, name),
                function.apply(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DarkArtsRevival.MOD_ID, name)))));
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, name),
                new BlockItem(block, new Item.Settings().useBlockPrefixedTranslationKey()
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, name)))));
    }

    public static void registerModBlocks() {
        DarkArtsRevival.LOGGER.info("Registering Mod Blocks for " + DarkArtsRevival.MOD_ID);
    }
}
