package net.bananacheese.darkartsrevival.client;

import net.bananacheese.darkartsrevival.block.custom.BarrierFieldBlock;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;

public class BarrierParticleRenderer {
    private static final int PARTICLE_RANGE = 5; // Show particles within 5 blocks
    private static final int PARTICLE_CHECK_INTERVAL = 20; // Check every 5 ticks
    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null && client.player != null) {
                tickCounter++;
                if (tickCounter >= PARTICLE_CHECK_INTERVAL) {
                    tickCounter = 0;
                    spawnBarrierParticles(client);
                }
            }
        });
    }

    private static void spawnBarrierParticles(MinecraftClient client) {
        ClientWorld world = client.world;
        if (world == null || client.player == null) return;

        BlockPos playerPos = client.player.getBlockPos();

        // Check blocks in a cube around the player
        for (BlockPos pos : BlockPos.iterate(
                playerPos.add(-PARTICLE_RANGE, -PARTICLE_RANGE, -PARTICLE_RANGE),
                playerPos.add(PARTICLE_RANGE, PARTICLE_RANGE, PARTICLE_RANGE))) {

            if (world.getBlockState(pos).getBlock() instanceof BarrierFieldBlock) {
                // Spawn particles at edges of the barrier block
                spawnParticlesForBarrier(world, pos);
            }
        }
    }

    private static void spawnParticlesForBarrier(ClientWorld world, BlockPos pos) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        // Spawn particles in a grid pattern on the barrier face
        // This creates a "forcefield" effect
        double density = 0.5; // Distance between particles

        for (double dy = 0; dy <= 1; dy += density) {
            for (double dx = 0; dx <= 1; dx += density) {
                for (double dz = 0; dz <= 1; dz += density) {
                    // Only spawn on edges/faces, not interior
                    if (isOnEdge(dx, dy, dz)) {
                        world.addParticleClient(
                                ParticleTypes.END_ROD,
                                x + dx,
                                y + dy,
                                z + dz,
                                0, 0, 0
                        );
                    }
                }
            }
        }
    }

    private static boolean isOnEdge(double x, double y, double z) {
        // Return true if at least one coordinate is on an edge (0 or 1)
        return x == 0 || x == 1 || y == 0 || y == 1 || z == 0 || z == 1;
    }
}
