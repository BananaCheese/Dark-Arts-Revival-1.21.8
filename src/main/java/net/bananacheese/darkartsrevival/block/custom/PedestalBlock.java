package net.bananacheese.darkartsrevival.block.custom;

import net.bananacheese.darkartsrevival.block.entity.custom.PedestalBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PedestalBlock extends BlockWithEntity implements BlockEntityProvider {
    private static final VoxelShape SHAPE = Block.createCuboidShape(4, 0, 4, 12, 13, 12);
    public static final MapCodec<PedestalBlock> CODEC = PedestalBlock.createCodec(PedestalBlock::new);

    public PedestalBlock(AbstractBlock.Settings settings) {
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
        return new PedestalBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                         PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof PedestalBlockEntity pedestalEntity)) {
            return ActionResult.PASS;
        }

        if (player.isSneaking() && !pedestalEntity.isEmpty()) {
            // Sneak: Pick up directly to inventory (no empty hand needed)
            ItemStack onPedestal = pedestalEntity.removeStack(0);
            if (!player.getInventory().insertStack(onPedestal)) {
                // Inventory full: Put back
                pedestalEntity.setStack(0, onPedestal);
                return ActionResult.FAIL;
            }
            world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
            return ActionResult.SUCCESS;
        } else if (pedestalEntity.isEmpty() && !stack.isEmpty()) {
            // Place item on empty pedestal
            pedestalEntity.setStack(0, stack.copyWithCount(1));
            world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 2f);
            stack.decrement(1);
            return ActionResult.SUCCESS;
        } else if (stack.isEmpty() && !player.isSneaking() && !pedestalEntity.isEmpty()) {
            // Non-sneak empty hand: Pick up to hand
            ItemStack onPedestal = pedestalEntity.removeStack(0);
            player.setStackInHand(hand, onPedestal);
            world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
