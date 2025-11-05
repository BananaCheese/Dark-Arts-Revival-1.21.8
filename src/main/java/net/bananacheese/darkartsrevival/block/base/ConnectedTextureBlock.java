package net.bananacheese.darkartsrevival.block.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

/**
 * Base class for blocks with connected textures
 * Automatically detects neighbors of the same block type and updates texture connections
 */
public abstract class ConnectedTextureBlock extends Block {

    // Connection properties for each direction
    public static final BooleanProperty CONNECTED_NORTH = BooleanProperty.of("connected_north");
    public static final BooleanProperty CONNECTED_SOUTH = BooleanProperty.of("connected_south");
    public static final BooleanProperty CONNECTED_EAST = BooleanProperty.of("connected_east");
    public static final BooleanProperty CONNECTED_WEST = BooleanProperty.of("connected_west");
    public static final BooleanProperty CONNECTED_UP = BooleanProperty.of("connected_up");
    public static final BooleanProperty CONNECTED_DOWN = BooleanProperty.of("connected_down");

    public ConnectedTextureBlock(Settings settings) {
        super(settings);
        setDefaultState(getConnectionDefaultState(getStateManager().getDefaultState()));
    }

    /**
     * Sets default connection states (all false)
     * Override this to add additional default states
     */
    protected BlockState getConnectionDefaultState(BlockState state) {
        return state
                .with(CONNECTED_NORTH, false)
                .with(CONNECTED_SOUTH, false)
                .with(CONNECTED_EAST, false)
                .with(CONNECTED_WEST, false)
                .with(CONNECTED_UP, false)
                .with(CONNECTED_DOWN, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CONNECTED_NORTH, CONNECTED_SOUTH, CONNECTED_EAST,
                CONNECTED_WEST, CONNECTED_UP, CONNECTED_DOWN);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = getDefaultState();
        return updateConnections(state, ctx.getWorld(), ctx.getBlockPos());
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return updateConnections(state, world, pos);
    }

    /**
     * Updates all connection properties based on neighboring blocks
     */
    protected BlockState updateConnections(BlockState state, WorldAccess world, BlockPos pos) {
        return state
                .with(CONNECTED_NORTH, shouldConnect(world, pos, Direction.NORTH))
                .with(CONNECTED_SOUTH, shouldConnect(world, pos, Direction.SOUTH))
                .with(CONNECTED_EAST, shouldConnect(world, pos, Direction.EAST))
                .with(CONNECTED_WEST, shouldConnect(world, pos, Direction.WEST))
                .with(CONNECTED_UP, shouldConnect(world, pos, Direction.UP))
                .with(CONNECTED_DOWN, shouldConnect(world, pos, Direction.DOWN));
    }

    /**
     * Determines if this block should visually connect to a neighbor in the given direction
     * Override this to customize connection logic
     */
    protected boolean shouldConnect(WorldAccess world, BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.offset(direction);
        BlockState neighborState = world.getBlockState(neighborPos);
        return canConnectTo(neighborState);
    }

    /**
     * Checks if this block can connect to another block state
     * By default, connects to the same block type
     * Override for custom connection logic
     */
    protected boolean canConnectTo(BlockState neighborState) {
        return neighborState.getBlock() == this;
    }

    /**
     * Updates connections for all neighboring connected texture blocks
     * Call this when a block is added or removed
     */
    protected void updateNeighborConnections(WorldAccess world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof ConnectedTextureBlock) {
                world.setBlockState(neighborPos,
                        ((ConnectedTextureBlock) neighborState.getBlock()).updateConnections(neighborState, world, neighborPos),
                        3);
            }
        }
    }

    /**
     * Helper method to get connection property for a direction
     */
    public static BooleanProperty getConnectionProperty(Direction direction) {
        return switch (direction) {
            case NORTH -> CONNECTED_NORTH;
            case SOUTH -> CONNECTED_SOUTH;
            case EAST -> CONNECTED_EAST;
            case WEST -> CONNECTED_WEST;
            case UP -> CONNECTED_UP;
            case DOWN -> CONNECTED_DOWN;
        };
    }
}