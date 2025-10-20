package net.bananacheese.darkartsrevival.ritual;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public enum AlterTier {
    TIER_1(1, "Basic Altar") {
        @Override
        public boolean isStructureValid(World world, BlockPos alterPos) {
            // Tier 1 requires nothing, just the altar block itself
            return true;
        }
    },

    TIER_2(2, "Enhanced Altar") {
        @Override
        public boolean isStructureValid(World world, BlockPos alterPos) {
            // Tier 2: Altar surrounded by a ring of specific blocks (e.g., blackstone)
            // Pattern: 1 block out in cardinal directions
            return checkBlocksInPattern(world, alterPos, new BlockPos[]{
                    new BlockPos(0, -1, 0),
                    new BlockPos(1, -1, 0),
                    new BlockPos(-1, -1, 0),
                    new BlockPos(0, -1, 1),
                    new BlockPos(0, -1, -1)
            }, Blocks.BLACKSTONE);
        }
    },

    TIER_3(3, "Ritualic Altar") {
        @Override
        public boolean isStructureValid(World world, BlockPos alterPos) {
            // Tier 3: Larger ring + corner blocks + pedestals at 4 corners
            // Ring pattern (1 block out)
            boolean ringValid = checkBlocksInPattern(world, alterPos, new BlockPos[]{
                    new BlockPos(0, -1, 0),
                    new BlockPos(1, -1, 0),
                    new BlockPos(-1, -1, 0),
                    new BlockPos(0, -1, 1),
                    new BlockPos(0, -1, -1),
                    new BlockPos(1, -1, 1),
                    new BlockPos(1, -1, -1),
                    new BlockPos(-1, -1, 1),
                    new BlockPos(-1, -1, -1),

                    new BlockPos(2, -1, 2),
                    new BlockPos(-2, -1, 2),
                    new BlockPos(2, -1, 2),
                    new BlockPos(-2, -1, -2)
            }, Blocks.BLACKSTONE);

            // 4 pedestals at distance 2
            boolean pedestalsValid = checkBlocksInPattern(world, alterPos, new BlockPos[]{
                    new BlockPos(2, 0, 2),
                    new BlockPos(-2, 0, 2),
                    new BlockPos(2, 0, -2),
                    new BlockPos(-2, 0, -2)
            }, net.bananacheese.darkartsrevival.block.DABlocks.PEDESTAL);

            return ringValid && pedestalsValid;
        }
    },

    TIER_4(4, "Supreme Altar") {
        @Override
        public boolean isStructureValid(World world, BlockPos alterPos) {
            // Tier 4: Extended ring + 8 pedestals at distance 3 in octagon pattern
            // Extended ring (2 blocks out)
            boolean ringValid = checkBlocksInPattern(world, alterPos, new BlockPos[]{
                    // Inner ring (1 block out)
                    new BlockPos(1, -1, 0),
                    new BlockPos(-1, -1, 0),
                    new BlockPos(0, -1, 1),
                    new BlockPos(0, -1, -1),
                    new BlockPos(1, -1, 1),
                    new BlockPos(1, -1, -1),
                    new BlockPos(-1, -1, 1),
                    new BlockPos(-1, -1, -1),
                    // Outer ring (2 blocks out on cardinals)
                    new BlockPos(2, -1, 0),
                    new BlockPos(-2, -1, 0),
                    new BlockPos(0, -1, 2),
                    new BlockPos(0, -1, -2),

                    new BlockPos(3, -1, 0),
                    new BlockPos(-3, -1, 0),
                    new BlockPos(0, -1, 3),
                    new BlockPos(0, -1, -3),
                    new BlockPos(2, -1, 2),
                    new BlockPos(2, -1, -2),
                    new BlockPos(-2, -1, 2),
                    new BlockPos(-2, -1, -2)
            }, Blocks.BLACKSTONE);

            // 8 pedestals in octagon pattern at distance 3
            boolean pedestalsValid = checkBlocksInPattern(world, alterPos, new BlockPos[]{
                    new BlockPos(3, 0, 0),
                    new BlockPos(-3, 0, 0),
                    new BlockPos(0, 0, 3),
                    new BlockPos(0, 0, -3),
                    new BlockPos(2, 0, 2),
                    new BlockPos(2, 0, -2),
                    new BlockPos(-2, 0, 2),
                    new BlockPos(-2, 0, -2)
            }, net.bananacheese.darkartsrevival.block.DABlocks.PEDESTAL);

            return ringValid && pedestalsValid;
        }
    };

    private final int level;
    private final String displayName;

    AlterTier(int level, String displayName) {
        this.level = level;
        this.displayName = displayName;
    }

    public int getLevel() {
        return level;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if the structure is valid for this tier
     */
    public abstract boolean isStructureValid(World world, BlockPos alterPos);

    /**
     * Helper method to check if specific blocks exist at relative positions
     */
    protected static boolean checkBlocksInPattern(World world, BlockPos centerPos, BlockPos[] relativePositions, Block expectedBlock) {
        for (BlockPos relativePos : relativePositions) {
            BlockPos absolutePos = centerPos.add(relativePos);
            if (!world.getBlockState(absolutePos).isOf(expectedBlock)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the tier by level, or TIER_1 if invalid
     */
    public static AlterTier getTierByLevel(int level) {
        for (AlterTier tier : values()) {
            if (tier.level == level) {
                return tier;
            }
        }
        return TIER_1;
    }

    /**
     * Determines the highest valid tier at the given position
     */
    public static AlterTier getHighestValidTier(World world, BlockPos alterPos) {
        for (int i = AlterTier.values().length - 1; i >= 0; i--) {
            AlterTier tier = AlterTier.values()[i];
            if (tier.isStructureValid(world, alterPos)) {
                return tier;
            }
        }
        return TIER_1;
    }
}
