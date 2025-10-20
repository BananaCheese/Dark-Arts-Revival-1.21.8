package net.bananacheese.darkartsrevival.ritual;

import net.bananacheese.darkartsrevival.item.DAItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class AlterAnalyzerRitual {

    private static final int FLUID_REQUIRED = 1000; // mB of water required

    /**
     * Performs the Analyzer Creation ritual - requires Tier 1 Altar + Water + Gold Ingot
     * Creates an Altar Analyzer item in the altar
     */
    public static boolean perform(ServerWorld world, BlockPos pos, PlayerEntity performer, ItemStack consumedStack) {
        AlterTier currentTier = AlterTier.getHighestValidTier(world, pos);

        if (currentTier.getLevel() < 1) {
            if (performer != null) {
                performer.sendMessage(Text.literal("§c✗ This is not an altar!"), false);
            }
            return false;
        }

        if (performer != null) {
            performer.sendMessage(Text.literal("§a✓ Altar Analyzer created!"), false);
        }

        // Create the analyzer and place it in the altar
        ItemStack analyzer = new ItemStack(DAItems.ALTER_ANALYZER);
        if (world.getBlockEntity(pos) instanceof net.bananacheese.darkartsrevival.block.entity.custom.AlterBlockEntity alterEntity) {
            alterEntity.setStack(0, analyzer);
            alterEntity.markDirty();
        }

        // Play ritual effects
        playRitualEffects(world, pos);

        return true;
    }

    /**
     * Plays ritual effects for analyzer creation
     */
    private static void playRitualEffects(ServerWorld world, BlockPos pos) {
        // Play mystical sound
        world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_CLUSTER_STEP, SoundCategory.BLOCKS, 1.0F, 1.2F);
        world.playSound(null, pos, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 0.5F, 1.0F);

        // Spawn water droplet particles
        for (int i = 0; i < 30; i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = 1.5 + Math.random() * 0.5;
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
            double y = pos.getY() + 0.5;

            world.spawnParticles(ParticleTypes.DRIPPING_WATER, x, y, z, 1, 0, 0.1, 0, 0.05);
            world.spawnParticles(ParticleTypes.SPLASH, x, y + 0.5, z, 1, 0, 0.1, 0, 0.05);
        }

        // Glow particles above altar
        world.spawnParticles(ParticleTypes.GLOW, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                30, 0.2, 0.2, 0.2, 0.1);
    }
}
