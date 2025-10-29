package net.bananacheese.darkartsrevival.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BarrierFieldBlock extends Block {
    public static final MapCodec<BarrierFieldBlock> CODEC = createCodec(BarrierFieldBlock::new);

    public BarrierFieldBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE; // Completely invisible
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        // Check if the entity causing collision is a mob
        if (context instanceof net.minecraft.block.EntityShapeContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity instanceof MobEntity) {
                return VoxelShapes.fullCube(); // Blocks mobs
            }
        }

        return VoxelShapes.empty(); // Allows players and block placement
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return VoxelShapes.empty(); // No visual outline
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, net.minecraft.entity.ai.pathing.NavigationType type) {
        return false; // Tell mob AI this block is solid for pathfinding
    }

    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty(); // Don't cull faces behind it (invisible)
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        // Push mobs back gently when they hit the barrier
        if (entity instanceof MobEntity mob) {
            // Slight knockback effect
            double dx = mob.getX() - (pos.getX() + 0.5);
            double dz = mob.getZ() - (pos.getZ() + 0.5);
            double distance = Math.sqrt(dx * dx + dz * dz);

            if (distance > 0) {
                mob.setVelocity(dx / distance * 0.1, 0, dz / distance * 0.1);
            }
        }
    }
}