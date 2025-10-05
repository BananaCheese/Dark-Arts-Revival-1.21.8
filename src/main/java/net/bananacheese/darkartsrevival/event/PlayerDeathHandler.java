package net.bananacheese.darkartsrevival.event;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.block.Blocks;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class PlayerDeathHandler {

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register(PlayerDeathHandler::onPlayerDeath);
    }

    private static void onPlayerDeath(LivingEntity entity, DamageSource damageSource) {
        // Only handle player deaths
        if (!(entity instanceof PlayerEntity player)) {
            return;
        }

        // Only in hardcore worlds or if you want it in all worlds, remove this check
        if (!player.getWorld().getLevelProperties().isHardcore()) {
            return;
        }

        ServerWorld world = (ServerWorld) player.getWorld();
        BlockPos deathPos = player.getBlockPos();

        // Find a suitable position to place the head (on top of a solid block)
        BlockPos headPos = findSuitableHeadPosition(world, deathPos);

        if (headPos != null) {
            // Place the player head
            world.setBlockState(headPos, Blocks.PLAYER_HEAD.getDefaultState());

            // Set the skull owner to the dead player
            if (world.getBlockEntity(headPos) instanceof net.minecraft.block.entity.SkullBlockEntity skullEntity) {
                // Convert GameProfile to ProfileComponent
                ProfileComponent profileComponent = new ProfileComponent(player.getGameProfile());
                skullEntity.setOwner(profileComponent);
                DarkArtsRevival.LOGGER.info("Placed death marker head for player {} at {}",
                        player.getName().getString(), headPos);
            }
        }
    }

    private static BlockPos findSuitableHeadPosition(ServerWorld world, BlockPos startPos) {
        // Try the death position first
        if (canPlaceHead(world, startPos)) {
            return startPos;
        }

        // Try one block up
        BlockPos upPos = startPos.up();
        if (canPlaceHead(world, upPos)) {
            return upPos;
        }

        // Try one block down
        BlockPos downPos = startPos.down();
        if (canPlaceHead(world, downPos)) {
            return downPos;
        }

        // Try adjacent positions
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;
                BlockPos adjacentPos = startPos.add(x, 0, z);
                if (canPlaceHead(world, adjacentPos)) {
                    return adjacentPos;
                }
            }
        }

        return null; // Could not find suitable position
    }

    private static boolean canPlaceHead(ServerWorld world, BlockPos pos) {
        // Check if the position is air and the block below is solid
        return world.getBlockState(pos).isAir() &&
                world.getBlockState(pos.down()).isSolidBlock(world, pos.down());
    }
}