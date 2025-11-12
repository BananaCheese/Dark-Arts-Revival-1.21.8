package net.bananacheese.darkartsrevival.block.custom;

import com.mojang.serialization.MapCodec;
import net.bananacheese.darkartsrevival.block.base.ConnectedTextureBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CloudBlock extends ConnectedTextureBlock {
    public static final MapCodec<CloudBlock> CODEC = createCodec(CloudBlock::new);

    // Only the top surface is solid (like a lily pad)
    private static final VoxelShape TOP_SHAPE = VoxelShapes.cuboid(0, 0.875, 0, 1, 1, 1);

    // Jump cooldown tracker per player
    private static final java.util.Map<java.util.UUID, Long> JUMP_COOLDOWNS = new java.util.HashMap<>();
    private static final long JUMP_COOLDOWN_MS = 500; // 0.5 second cooldown

    public CloudBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends ConnectedTextureBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // If entity is sneaking, no collision at all (fall through)
        if (context instanceof net.minecraft.block.EntityShapeContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity != null && entity.isSneaking()) {
                return VoxelShapes.empty();
            }

            // One-way platform: only solid if entity is above the block
            if (entity != null) {
                // Check if entity's bottom (feet) is above the top surface
                double entityBottom = entity.getY();
                double blockTop = pos.getY() + 0.9375; // Top of the thin collision shape

                // Only provide collision if entity is coming from above
                if (entityBottom > blockTop) {
                    return TOP_SHAPE;
                }

                // No collision if entity is below or inside (allows jumping through)
                return VoxelShapes.empty();
            }
        }

        // Default: only solid from above
        return TOP_SHAPE;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube(); // Visual outline is still full cube
    }

    @Override
    protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // No camera collision - prevents forcing crouch when inside
        return VoxelShapes.empty();
    }

    @Override
    protected boolean isSideInvisible(BlockState state, BlockState stateFrom, net.minecraft.util.math.Direction direction) {
        // For cloud blocks, we want special culling behavior
        if (stateFrom.isOf(this)) {
            // NEVER cull the top face - players need to see what they're standing on
            if (direction == net.minecraft.util.math.Direction.UP) {
                return false;
            }

            // NEVER cull the bottom face - helps with depth perception when looking down through clouds
            if (direction == net.minecraft.util.math.Direction.DOWN) {
                return false;
            }

            // Cull horizontal faces between connected clouds for seamless look
            return true;
        }

        return super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        // Full brightness - clouds don't cast shadows
        return 1.0F;
    }

    @Override
    protected boolean isTransparent(BlockState state) {
        return true;
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        entity.handleFallDamage(fallDistance, 0.1F, world.getDamageSources().fall());
    }

    public static void onPlayerJump(World world, BlockPos pos, PlayerEntity player) {
        if (world.isClient) return;

        java.util.UUID playerId = player.getUuid();
        long currentTime = System.currentTimeMillis();

        Long lastJump = JUMP_COOLDOWNS.get(playerId);
        if (lastJump == null || (currentTime - lastJump) > JUMP_COOLDOWN_MS) {
            launchPlayerWithWindCharge(world, pos, player);
            JUMP_COOLDOWNS.put(playerId, currentTime);
        }
    }

    private static void launchPlayerWithWindCharge(World world, BlockPos pos, PlayerEntity player) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        // Spawn wind charge below player
        Vec3d playerPos = player.getPos();
        WindChargeEntity windCharge = new WindChargeEntity(player, world, playerPos.x, pos.getY() + 0.5, playerPos.z);

        // Set wind charge velocity downward
        windCharge.setVelocity(0, -0.5, 0);
        world.spawnEntity(windCharge);

        // Launch player upward
        player.setVelocity(player.getVelocity().x, 1.5, player.getVelocity().z);
        player.velocityModified = true;

        // Play sound effect
        world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_WIND_CHARGE_THROW,
                SoundCategory.BLOCKS,
                1.0F,
                1.2F
        );

        // Visual effect
        serverWorld.spawnParticles(
                net.minecraft.particle.ParticleTypes.CLOUD,
                playerPos.x,
                pos.getY() + 1.0,
                playerPos.z,
                15,
                0.3, 0.1, 0.3,
                0.05
        );
    }
}