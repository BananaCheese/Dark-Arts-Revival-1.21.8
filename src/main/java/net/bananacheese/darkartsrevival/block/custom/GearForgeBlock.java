package net.bananacheese.darkartsrevival.block.custom;

import com.mojang.serialization.MapCodec;
import net.bananacheese.darkartsrevival.block.entity.DABlockEntities;
import net.bananacheese.darkartsrevival.block.entity.custom.GearForgeBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GearForgeBlock extends BlockWithEntity {

    public GearForgeBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GearForgeBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos,
                                 PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GearForgeBlockEntity forgeEntity) {
                if (forgeEntity.isFormed()) {
                    // Open GUI here - we'll implement this next
                    player.sendMessage(net.minecraft.text.Text.literal("Gear Forge is formed! GUI would open here."), false);
                } else {
                    player.sendMessage(net.minecraft.text.Text.literal("Multiblock not formed correctly."), false);
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos,
                                   BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GearForgeBlockEntity forgeEntity) {
                forgeEntity.unformMultiblock();
            }
            super.onStateReplaced(state, world, pos, moved);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
                                                                  BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, DABlockEntities.GEAR_FORGE_BLOCK_ENTITY,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof GearForgeBlockEntity forgeEntity && forgeEntity.isFormed()) {
            // Return full block shape when formed
            return VoxelShapes.fullCube();
        }
        // Return smaller shape when not formed (adjust as needed)
        return Block.createCuboidShape(2, 0, 2, 14, 16, 14);
    }
}