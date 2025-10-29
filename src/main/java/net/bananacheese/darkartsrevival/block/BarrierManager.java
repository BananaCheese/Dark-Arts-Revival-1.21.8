package net.bananacheese.darkartsrevival.block;

import net.bananacheese.darkartsrevival.block.custom.BarrierFieldBlock;
import net.bananacheese.darkartsrevival.block.custom.DarkBarrierBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BarrierManager {
    private static final int BARRIER_RANGE = 2; // 2 blocks out from the source

    // Track which barrier blocks are controlling which field positions
    private static final Map<BlockPos, Set<BlockPos>> BARRIER_SOURCES = new HashMap<>();

    /**
     * Updates all barrier fields for a Dark Barrier block
     */
    public static void updateBarriers(World world, BlockPos sourcePos, BlockState sourceState) {
        if (world.isClient) return;

        // Clear old barriers for this source
        removeBarriersForSource(world, sourcePos);

        // Create new barriers
        Set<BlockPos> controlledFields = new HashSet<>();

        // Check all 6 directions
        for (Direction direction : Direction.values()) {
            boolean isActive = DarkBarrierBlock.isBarrierActive(sourceState, direction);
            if (isActive) {
                controlledFields.addAll(createBarrierInDirection(world, sourcePos, direction));
            }
        }

        // Store the controlled fields
        if (!controlledFields.isEmpty()) {
            BARRIER_SOURCES.put(sourcePos, controlledFields);
        }
    }

    /**
     * Creates barriers in a specific direction and returns the positions
     */
    private static Set<BlockPos> createBarrierInDirection(World world, BlockPos sourcePos, Direction direction) {
        Set<BlockPos> createdFields = new HashSet<>();

        for (int distance = 1; distance <= BARRIER_RANGE; distance++) {
            BlockPos fieldPos = sourcePos.offset(direction, distance);

            // Only create barrier if it's air or already a barrier field
            if (world.getBlockState(fieldPos).isAir() ||
                    world.getBlockState(fieldPos).getBlock() instanceof BarrierFieldBlock) {
                world.setBlockState(fieldPos, DABlocks.BARRIER_FIELD.getDefaultState());
                createdFields.add(fieldPos);
            }
        }

        return createdFields;
    }

    /**
     * Removes only the barriers controlled by this specific source
     */
    private static void removeBarriersForSource(World world, BlockPos sourcePos) {
        Set<BlockPos> controlledFields = BARRIER_SOURCES.remove(sourcePos);
        if (controlledFields != null) {
            for (BlockPos fieldPos : controlledFields) {
                // Only remove if it's still a barrier field (and not controlled by another source)
                if (world.getBlockState(fieldPos).getBlock() instanceof BarrierFieldBlock) {
                    // Check if any other source is still controlling this position
                    boolean stillControlled = false;
                    for (Set<BlockPos> otherFields : BARRIER_SOURCES.values()) {
                        if (otherFields.contains(fieldPos)) {
                            stillControlled = true;
                            break;
                        }
                    }

                    if (!stillControlled) {
                        world.setBlockState(fieldPos, net.minecraft.block.Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }

    /**
     * Removes all barriers associated with a Dark Barrier block
     */
    public static void removeAllBarriers(World world, BlockPos sourcePos) {
        removeBarriersForSource(world, sourcePos);
    }
}