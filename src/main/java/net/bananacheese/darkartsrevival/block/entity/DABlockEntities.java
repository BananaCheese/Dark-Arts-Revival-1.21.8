package net.bananacheese.darkartsrevival.block.entity;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.bananacheese.darkartsrevival.block.DABlocks;
import net.bananacheese.darkartsrevival.block.entity.custom.AlterBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class DABlockEntities {
    public static final BlockEntityType<AlterBlockEntity> ALTER_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(DarkArtsRevival.MOD_ID, "alter_be"),
                    FabricBlockEntityTypeBuilder.create(AlterBlockEntity::new, DABlocks.ALTER).build());

    public static void registerBlockEntities() {
        DarkArtsRevival.LOGGER.info("Registering Block Entities for " + DarkArtsRevival.MOD_ID);
    }
}
