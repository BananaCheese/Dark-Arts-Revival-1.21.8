package net.bananacheese.darkartsrevival.block.custom;

import com.mojang.serialization.MapCodec;
import net.bananacheese.darkartsrevival.block.BarrierManager;
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

public class DarkBarrierBlock extends Block {
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
        setDefaultState(getStateManager().getDefaultState()
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
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState();
    }

    @Override
    protected void onBlockAdded(BlockState state, net.minecraft.world.World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (!world.isClient) {
            // Initialize barriers when block is placed
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

                    player.sendMessage(Text.literal("§6" + side.getAxis() + " barrier: " +
                            (currentState ? "§cOFF" : "§aON")), true);
                }

                if (side.getAxis().isVertical()) {
                    BooleanProperty property = getPropertyForDirection(side);
                    boolean currentState = state.get(property);
                    world.setBlockState(pos, state.with(property, !currentState));

                    player.sendMessage(Text.literal("§6" + side.getAxis() + " barrier: " +
                            (currentState ? "§cOFF" : "§aON")), true);
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private BooleanProperty getPropertyForDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
            default -> NORTH; // Fallback
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
            default -> null;
        };
        return property != null && state.get(property);
    }
}