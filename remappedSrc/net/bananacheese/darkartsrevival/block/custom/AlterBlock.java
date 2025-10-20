package net.bananacheese.darkartsrevival.block.custom;

import com.mojang.serialization.MapCodec;
import net.bananacheese.darkartsrevival.block.entity.custom.AlterBlockEntity;
import net.bananacheese.darkartsrevival.ritual.RitualHandler;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AlterBlock extends BlockWithEntity implements BlockEntityProvider {
    private static final VoxelShape SHAPE =
            Block.createCuboidShape(2, 0, 2, 14, 13, 14);
    public static final MapCodec<AlterBlock> CODEC = AlterBlock.createCodec(AlterBlock::new);

    public AlterBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AlterBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                         PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof AlterBlockEntity alterBlockEntity) {
            // Handle water bucket - adds fluid instead of placing item
            if (stack.getItem() == Items.WATER_BUCKET && !world.isClient) {
                if (alterBlockEntity.addFluid(1000)) { // 1 bucket = 1000 mB
                    world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1f, 1f);
                    stack.decrement(1);
                    // Give back empty bucket
                    if (!player.getInventory().insertStack(new ItemStack(Items.BUCKET))) {
                        player.dropItem(new ItemStack(Items.BUCKET), false);
                    }
                    alterBlockEntity.markDirty();
                    world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                    ((ServerWorld) world).getChunkManager().markForUpdate(pos);
                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.FAIL;
                }
            }

            // Normal altar behavior (place/remove items) - but skip if it's a water bucket
            if (alterBlockEntity.isEmpty() && !stack.isEmpty() && stack.getItem() != Items.WATER_BUCKET) {
                alterBlockEntity.setStack(0, stack.copyWithCount(1));
                world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 2f);
                stack.decrement(1);

                alterBlockEntity.markDirty();
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                if (!world.isClient) {
                    ((ServerWorld) world).getChunkManager().markForUpdate(pos);
                }
            } else if (stack.isEmpty() && !player.isSneaking()) {
                ItemStack stackOnPedestal = alterBlockEntity.getStack(0);
                player.setStackInHand(Hand.MAIN_HAND, stackOnPedestal);
                world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
                alterBlockEntity.clear();

                alterBlockEntity.markDirty();
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                if (!world.isClient) {
                    ((ServerWorld) world).getChunkManager().markForUpdate(pos);
                }
            }
        }

        return ActionResult.SUCCESS;
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, net.minecraft.world.block.WireOrientation wireOrientation, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);

        if (!world.isClient && world.isReceivingRedstonePower(pos)) {
            if (world.getBlockEntity(pos) instanceof AlterBlockEntity alterBlockEntity) {
                RitualHandler.performRitualOnRedstone((ServerWorld) world, pos, null);
            }
        }
    }
}