package net.bananacheese.darkartsrevival.block.custom;

import com.mojang.serialization.MapCodec;
import net.bananacheese.darkartsrevival.block.entity.DABlockEntities;
import net.bananacheese.darkartsrevival.block.entity.custom.GearForgeBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GearForgeBlock extends BlockWithEntity {
    public static final BooleanProperty FORMED = BooleanProperty.of("formed");
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    public GearForgeBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(FORMED, false)
                .with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FORMED, FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
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
                    player.sendMessage(net.minecraft.text.Text.literal("Opening Gear Forge GUI..."), false);
                    // TODO: Open GUI screen here
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
                (world1, pos, state1, blockEntity) -> {
                    blockEntity.tick(world1, pos, state1);

                    // Update block state to match formed status
                    boolean currentFormed = state1.get(FORMED);
                    boolean shouldBeFormed = blockEntity.isFormed();
                    if (currentFormed != shouldBeFormed) {
                        world1.setBlockState(pos, state1.with(FORMED, shouldBeFormed), 3);
                    }
                });
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(FORMED)) {
            // When formed, create a large hitbox covering the entire multiblock
            return VoxelShapes.cuboid(-1.0, 0.0, -1.0, 2.0, 2.0, 2.0);
        }
        // Normal block shape when not formed
        return VoxelShapes.fullCube();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(FORMED)) {
            // Full collision box when formed
            return VoxelShapes.cuboid(-1.0, 0.0, -1.0, 2.0, 2.0, 2.0);
        }
        return VoxelShapes.fullCube();
    }
}