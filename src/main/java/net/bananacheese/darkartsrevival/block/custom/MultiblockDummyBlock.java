package net.bananacheese.darkartsrevival.block.custom;

import com.mojang.serialization.MapCodec;
import net.bananacheese.darkartsrevival.block.entity.custom.GearForgeBlockEntity;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MultiblockDummyBlock extends Block {

    public MultiblockDummyBlock(Settings settings) {
        // Back to unbreakable - hammer handles forming/unforming
        super(settings);
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return null;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos,
                                 PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            // Redirect to master block
            BlockPos masterPos = findMasterBlock(world, pos);
            if (masterPos != null) {
                BlockState masterState = world.getBlockState(masterPos);
                // Create a new hit result at the master block position
                BlockHitResult newHit = new BlockHitResult(
                        hit.getPos(),
                        hit.getSide(),
                        masterPos,
                        hit.isInsideBlock()
                );
                return masterState.onUse(world, player, newHit);
            }
        }
        return ActionResult.SUCCESS;
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos,
                                   BlockState newState, boolean moved) {
        // Dummy blocks are now unbreakable, so this only triggers if replaced another way
        if (!state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, moved);
        }
    }

    private BlockPos findMasterBlock(World world, BlockPos dummyPos) {
        // Search in a 5x5x5 area for the master block
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos checkPos = dummyPos.add(x, y, z);
                    if (world.getBlockEntity(checkPos) instanceof GearForgeBlockEntity forge) {
                        if (forge.isFormed() && forge.isDummyPartOfMultiblock(dummyPos)) {
                            return checkPos;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        // Prevent particles
    }
}