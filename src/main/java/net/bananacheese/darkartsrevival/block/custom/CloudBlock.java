package net.bananacheese.darkartsrevival.block.custom;

import com.mojang.serialization.MapCodec;
import net.bananacheese.darkartsrevival.block.base.ConnectedTextureBlock;
import net.minecraft.block.Block;
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

    // Jump cooldown tracker per player
    private static final java.util.Map<java.util.UUID, Long> JUMP_COOLDOWNS = new java.util.HashMap<>();
    private static final long JUMP_COOLDOWN_MS = 500; // 0.5 second cooldown

    public CloudBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // If entity is sneaking, no collision (they fall through)
        if (context instanceof net.minecraft.block.EntityShapeContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity != null && entity.isSneaking()) {
                return VoxelShapes.empty();
            }
        }
        return VoxelShapes.fullCube();
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        // Slow falling when falling through (crouching)
        if (entity.isSneaking() && entity.getVelocity().y < 0) {
            Vec3d velocity = entity.getVelocity();
            entity.setVelocity(velocity.x, Math.max(velocity.y, -0.2), velocity.z);
        }

        // Check for player jump
        if (!world.isClient && entity instanceof PlayerEntity player) {
            Vec3d velocity = player.getVelocity();

            // Debug logging
            if (velocity.y > 0.2) {
                net.bananacheese.darkartsrevival.DarkArtsRevival.LOGGER.info(
                        "Player velocity Y: {}, onGround: {}", velocity.y, player.isOnGround()
                );
            }

            // Detect jump: upward velocity in jump range
            if (velocity.y > 0.35 && velocity.y < 0.55) {
                java.util.UUID playerId = player.getUuid();
                long currentTime = System.currentTimeMillis();

                Long lastJump = JUMP_COOLDOWNS.get(playerId);
                if (lastJump == null || (currentTime - lastJump) > JUMP_COOLDOWN_MS) {
                    net.bananacheese.darkartsrevival.DarkArtsRevival.LOGGER.info("LAUNCHING PLAYER!");
                    launchPlayerWithWindCharge(world, pos, player);
                    JUMP_COOLDOWNS.put(playerId, currentTime);
                }
            }
        }
    }

    protected void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        // Reduce fall damage significantly
        entity.handleFallDamage(fallDistance, 0.1F, world.getDamageSources().fall());
    }

    private void launchPlayerWithWindCharge(World world, BlockPos pos, PlayerEntity player) {
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