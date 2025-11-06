package net.bananacheese.darkartsrevival.block.custom;

import com.mojang.serialization.MapCodec;
import net.bananacheese.darkartsrevival.block.BarrierManager;
import net.bananacheese.darkartsrevival.block.base.ConnectedTextureBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Objects;

public class DarkBarrierBlock extends ConnectedTextureBlock {
    public static final MapCodec<DarkBarrierBlock> CODEC = createCodec(DarkBarrierBlock::new);

    // Properties for each side being active
    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty WEST = BooleanProperty.of("west");
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");

    public DarkBarrierBlock(Settings settings) {
        super(settings);
        setDefaultState(getConnectionDefaultState(getStateManager().getDefaultState())
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false));
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return updateConnections(Objects.requireNonNull(super.getPlacementState(ctx)), ctx.getWorld(), ctx.getBlockPos());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return updateConnections(super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos),
                world, pos);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (!world.isClient) {
            // Update neighboring connections
            updateNeighborConnections(world, pos);

            // Initialize barriers
            BarrierManager.updateBarriers(world, pos, state);
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        // For now, use redstone torch to toggle sides (replace with custom item later)
        if (player.getStackInHand(Hand.MAIN_HAND).getItem() == Items.REDSTONE_TORCH) {
            if (!world.isClient) {
                Direction side = hit.getSide();

                // Only toggle horizontal sides
                if (side.getAxis().isHorizontal()) {
                    BooleanProperty property = getPropertyForDirection(side);
                    boolean currentState = state.get(property);
                    world.setBlockState(pos, state.with(property, !currentState));

                    player.sendMessage(Text.literal("§6" + side.name() + " barrier: " +
                            (currentState ? "§cOFF" : "§aON")), true);
                }

                if (side.getAxis().isVertical()) {
                    BooleanProperty property = getPropertyForDirection(side);
                    boolean currentState = state.get(property);
                    world.setBlockState(pos, state.with(property, !currentState));

                    player.sendMessage(Text.literal("§6" + side.name() + " barrier: " +
                            (currentState ? "§cOFF" : "§aON")), true);
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static BooleanProperty getPropertyForDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    public static boolean isBarrierActive(BlockState state, Direction direction) {
        BooleanProperty property = switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
        return property != null && state.get(property);
    }
}