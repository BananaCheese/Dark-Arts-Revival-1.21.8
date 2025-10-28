package net.bananacheese.darkartsrevival.block;

import net.bananacheese.darkartsrevival.block.custom.BarrierFieldBlock;
import net.bananacheese.darkartsrevival.block.custom.DarkBarrierBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BarrierManager {
    private static final int BARRIER_RANGE = 2; // 2 blocks out from the source

    /**
     * Updates all barrier fields for a Dark Barrier block
     */
    public static void updateBarriers(World world, BlockPos sourcePos, BlockState sourceState) {
        if (world.isClient) return;

        // Check all 6 directions
        for (Direction direction : Direction.values()) {
            boolean isActive = DarkBarrierBlock.isBarrierActive(sourceState, direction);
            updateBarrierInDirection(world, sourcePos, direction, isActive);
        }
    }

    /**
     * Updates barriers in a specific direction
     */
    private static void updateBarrierInDirection(World world, BlockPos sourcePos, Direction direction, boolean active) {
        for (int distance = 1; distance <= BARRIER_RANGE; distance++) {
            BlockPos barrierPos = sourcePos.offset(direction, distance);

            // For horizontal directions, create 2-block-tall barriers
            if (direction.getAxis().isHorizontal()) {
                for (int y = 0; y <= 1; y++) {
                    BlockPos fieldPos = barrierPos.up(y);
                    setBarrierField(world, fieldPos, active);
                }
            } else {
                // For vertical directions, just place at that position
                setBarrierField(world, barrierPos, active);
            }
        }
    }

    private static void setBarrierField(World world, BlockPos pos, boolean active) {
        if (active) {
            // Place barrier field if position is air or already a barrier field
            if (world.getBlockState(pos).isAir() ||
                    world.getBlockState(pos).getBlock() instanceof BarrierFieldBlock) {
                world.setBlockState(pos, DABlocks.BARRIER_FIELD.getDefaultState());
            }
        } else {
            // Remove barrier field if it exists
            if (world.getBlockState(pos).getBlock() instanceof BarrierFieldBlock) {
                world.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState());
            }
        }
    }

    /**
     * Removes all barriers associated with a Dark Barrier block
     */
    public static void removeAllBarriers(World world, BlockPos sourcePos) {
        for (Direction direction : Direction.values()) {
            updateBarrierInDirection(world, sourcePos, direction, false);
        }
    }
}