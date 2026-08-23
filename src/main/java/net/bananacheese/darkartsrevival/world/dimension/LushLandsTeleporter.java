package net.bananacheese.darkartsrevival.world.dimension;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

/**
 * LushLandsTeleporter
 *
 * Handles player travel between the overworld and The Lush Lands.
 *
 * Entry method:
 *   A Tier-3+ ritual using the Altar creates a Lush Lands Portal block.
 *   Right-clicking the portal block teleports the player (server-side only).
 *
 * Return method:
 *   Right-clicking the same portal block type while in The Lush Lands
 *   returns the player to their last overworld position.
 *
 * Note: This class registers a UseBlockCallback.  Call LushLandsTeleporter.register()
 * from your ModInitializer AFTER blocks are registered.
 *
 * The actual portal block registration is handled separately in DABlocks
 * (add a LUSH_LANDS_PORTAL block there).
 */
public class LushLandsTeleporter {

    /** Y offset above the dimension floor where players land on entry. */
    private static final int ARRIVAL_Y_OFFSET = 5;

    public static void register() {
        UseBlockCallback.EVENT.register(LushLandsTeleporter::onUseBlock);
        DarkArtsRevival.LOGGER.info("[LushLands] Teleporter registered.");
    }

    private static ActionResult onUseBlock(PlayerEntity player, World world,
                                           Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.PASS;
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return ActionResult.PASS;

        BlockPos pos = hit.getBlockPos();

        // Check if the block at the hit position is our portal block.
        // Replace Blocks.CRYING_OBSIDIAN with your actual portal block when ready.
        // TODO: Replace with DABlocks.LUSH_LANDS_PORTAL
        if (!world.getBlockState(pos).isOf(Blocks.CRYING_OBSIDIAN)) {
            return ActionResult.PASS;
        }

        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;

        MinecraftServer server = serverPlayer.getServer();
        if (server == null) return ActionResult.PASS;

        if (world.getRegistryKey() == LushLandsDimension.WORLD_KEY) {
            // In Lush Lands → go back to overworld
            teleportToOverworld(serverPlayer, server);
        } else {
            // In another dimension → go to Lush Lands
            teleportToLushLands(serverPlayer, server);
        }

        return ActionResult.SUCCESS;
    }

    // ─── Teleport to Lush Lands ───────────────────────────────────────────────

    private static void teleportToLushLands(ServerPlayerEntity player, MinecraftServer server) {
        ServerWorld target = server.getWorld(LushLandsDimension.WORLD_KEY);
        if (target == null) {
            player.sendMessage(Text.literal("§c[LushLands] Dimension not loaded!"), false);
            DarkArtsRevival.LOGGER.error("[LushLands] World not found: {}", LushLandsDimension.WORLD_KEY.getValue());
            return;
        }

        // Store current overworld position for return trip
        LushLandsPlayerData.saveOverworldPos(player);

        // Find a safe landing spot at the destination XZ
        int destX = (int)(player.getX() / 8.0); // slight XZ scale
        int destZ = (int)(player.getZ() / 8.0);
        int destY = findSafeLandingY(target, destX, destZ);

        player.teleportTo(new TeleportTarget(
                target,
                player,
                TeleportTarget.ADD_PORTAL_CHUNK_TICKET
        ));

        player.setPosition(
                destX + 0.5,
                destY + ARRIVAL_Y_OFFSET,
                destZ + 0.5
        );

        player.sendMessage(Text.literal("§aYou enter §2The Lush Lands§a…"), false);
    }

    // ─── Teleport back to overworld ───────────────────────────────────────────

    private static void teleportToOverworld(ServerPlayerEntity player, MinecraftServer server) {
        ServerWorld overworld = server.getOverworld();

        Vec3d savedPos = LushLandsPlayerData.getOverworldPos(player);
        double ox, oy, oz;
        if (savedPos != null) {
            ox = savedPos.x;
            oy = savedPos.y;
            oz = savedPos.z;
        } else {
            // No saved pos – use world spawn
            BlockPos spawn = overworld.getSpawnPos();
            ox = spawn.getX() + 0.5;
            oy = spawn.getY() + 1;
            oz = spawn.getZ() + 0.5;
        }

        player.teleportTo(new TeleportTarget(
                overworld,
                player,
                TeleportTarget.ADD_PORTAL_CHUNK_TICKET
        ));

        player.setPosition(ox, oy, oz);

        player.sendMessage(Text.literal("§7You return to the overworld."), false);
    }

    // ─── Utility ─────────────────────────────────────────────────────────────

    /**
     * Scans downward from maxY to find the first non-air block and returns
     * the Y one above it.  Falls back to sea level if nothing is found.
     */
    private static int findSafeLandingY(ServerWorld world, int x, int z) {
        int maxY = world.getTopYInclusive();
        int minY = world.getBottomY();

        for (int y = maxY - 1; y > minY; y--) {
            BlockPos check = new BlockPos(x, y, z);
            if (!world.getBlockState(check).isAir()) {
                return y + 1;
            }
        }

        return 80; // fallback to valley floor baseline
    }
}