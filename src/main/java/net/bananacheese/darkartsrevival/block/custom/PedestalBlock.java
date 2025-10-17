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
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

public class PedestalBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final MapCodec<PedestalBlock> CODEC = PedestalBlock.createCodec(PedestalBlock::new);

    // Pedestal is smaller than altar
    private static final VoxelShape SHAPE = VoxelShapes.cuboid(0.25f, 0, 0.25f, 0.75f, 0.75f, 0.75f);

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
        if(world.getBlockEntity(pos) instanceof PedestalBlockEntity pedestalEntity) {
            // Place item on pedestal
            if(pedestalEntity.isEmpty() && !stack.isEmpty()) {
                pedestalEntity.setStack(0, stack.copyWithCount(1));
                world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 2f);
                stack.decrement(1);

                pedestalEntity.markDirty();
                world.updateListeners(pos, state, state, net.minecraft.block.Block.NOTIFY_ALL);
                if (!world.isClient) {
                    ((net.minecraft.server.world.ServerWorld) world).getChunkManager().markForUpdate(pos);
                }
            }
            // Remove item from pedestal
            else if(stack.isEmpty() && !player.isSneaking()) {
                ItemStack stackOnPedestal = pedestalEntity.getStack(0);
                player.setStackInHand(Hand.MAIN_HAND, stackOnPedestal);
                world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
                pedestalEntity.clear();

                pedestalEntity.markDirty();
                world.updateListeners(pos, state, state, net.minecraft.block.Block.NOTIFY_ALL);
                if (!world.isClient) {
                    ((net.minecraft.server.world.ServerWorld) world).getChunkManager().markForUpdate(pos);
                }
            }
        }

        return ActionResult.SUCCESS;
    }
}
