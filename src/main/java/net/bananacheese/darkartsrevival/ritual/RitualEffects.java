package net.bananacheese.darkartsrevival.ritual;


import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

/**
 * Centralized ritual effects system
 * Each effect type has a unique identifier and can be called from recipes
 */
public class RitualEffects {

    /**
     * Play ritual effect by name
     */
    public static void play(ServerWorld world, BlockPos pos, String effectName) {
        switch (effectName.toLowerCase()) {
            case "basic" -> playBasicEffect(world, pos);
            case "water" -> playWaterEffect(world, pos);
            case "enchanting" -> playEnchantingEffect(world, pos);
            case "soul" -> playSoulEffect(world, pos);
            case "fire" -> playFireEffect(world, pos);
            case "portal" -> playPortalEffect(world, pos);
            case "revival" -> playRevivalEffect(world, pos);
            case "dark" -> playDarkEffect(world, pos);
            case "holy" -> playHolyEffect(world, pos);
            case "lightning" -> playLightningEffect(world, pos);
            case "nature" -> playNatureEffect(world, pos);
            case "blood" -> playBloodEffect(world, pos);
            default -> playBasicEffect(world, pos);
        }
    }

    /**
     * Basic ritual effect - simple particles and sound
     */
    public static void playBasicEffect(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.BLOCK_AMETHYST_CLUSTER_STEP, SoundCategory.BLOCKS, 1.0F, 1.2F);

        // Circle of particles
        for (int i = 0; i < 20; i++) {
            double angle = (i / 20.0) * Math.PI * 2;
            double radius = 1.5;
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
            double y = pos.getY() + 0.5;

            world.spawnParticles(ParticleTypes.ENCHANT, x, y, z, 1, 0, 0.1, 0, 0.05);
        }

        // Center glow
        world.spawnParticles(ParticleTypes.GLOW, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                15, 0.2, 0.2, 0.2, 0.1);
    }

    /**
     * Water-themed effect - dripping water and splashes
     */
    public static void playWaterEffect(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 0.8F, 1.2F);

        // Water droplets in circle
        for (int i = 0; i < 30; i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = 1.5 + Math.random() * 0.5;
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
            double y = pos.getY() + 0.5;

            world.spawnParticles(ParticleTypes.DRIPPING_WATER, x, y + 1, z, 1, 0, 0.1, 0, 0.05);
            world.spawnParticles(ParticleTypes.SPLASH, x, y, z, 1, 0, 0.1, 0, 0.05);
        }

        // Bubble column effect
        world.spawnParticles(ParticleTypes.BUBBLE_COLUMN_UP,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                20, 0.3, 0.5, 0.3, 0.1);
    }

    /**
     * Enchanting effect - purple particles and book sounds
     */
    public static void playEnchantingEffect(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);

        // Spiral of enchanting particles
        for (int i = 0; i < 50; i++) {
            double angle = (i / 50.0) * Math.PI * 4; // Two rotations
            double height = (i / 50.0) * 2.0;
            double radius = 1.5;
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
            double y = pos.getY() + 0.5 + height;

            world.spawnParticles(ParticleTypes.ENCHANT, x, y, z, 1, 0, 0, 0, 0.05);
        }

        // Center portal particles
        world.spawnParticles(ParticleTypes.PORTAL,
                pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                30, 0.2, 0.2, 0.2, 0.3);
    }

    /**
     * Soul effect - blue soul particles and eerie sounds
     */
    public static void playSoulEffect(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.BLOCKS, 1.0F, 0.8F);

        // Soul particles rising
        for (int i = 0; i < 40; i++) {
            double angle = (i / 40.0) * Math.PI * 2;
            double radius = 2.0;
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
            double y = pos.getY() + 0.5;

            world.spawnParticles(ParticleTypes.SOUL, x, y, z, 2, 0, 0.5, 0, 0.03);
            world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 1, 0, 0.1, 0, 0.02);
        }

        // Center soul explosion
        world.spawnParticles(ParticleTypes.SOUL,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                50, 0.3, 0.3, 0.3, 0.1);
    }

    /**
     * Fire effect - flames and smoke
     */
    public static void playFireEffect(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);

        // Ring of fire
        for (int i = 0; i < 30; i++) {
            double angle = (i / 30.0) * Math.PI * 2;
            double radius = 1.8;
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
            double y = pos.getY() + 0.5;

            world.spawnParticles(ParticleTypes.FLAME, x, y, z, 2, 0, 0.3, 0, 0.05);
            world.spawnParticles(ParticleTypes.SMOKE, x, y, z, 1, 0, 0.2, 0, 0.02);
        }

        // Center lava particles
        world.spawnParticles(ParticleTypes.LAVA,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                20, 0.3, 0.1, 0.3, 0.0);
    }

    /**
     * Portal effect - dimensional tears
     */
    public static void playPortalEffect(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, 1.5F);

        // Swirling portal particles
        for (int i = 0; i < 100; i++) {
            double angle = (i / 100.0) * Math.PI * 4;
            double radius = 1.0 + Math.random();
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
            double y = pos.getY() + 0.5 + Math.random() * 2.0;

            world.spawnParticles(ParticleTypes.PORTAL, x, y, z, 1, 0, 0, 0, 0.5);
            world.spawnParticles(ParticleTypes.REVERSE_PORTAL, x, y, z, 1, 0, 0, 0, 0.3);
        }
    }

    /**
     * Revival effect - dramatic resurrection effects
     */
    public static void playRevivalEffect(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.BLOCKS, 1.0F, 0.8F);
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.BLOCKS, 0.5F, 1.5F);

        // Circle of soul particles
        for (int i = 0; i < 50; i++) {
            double angle = (i / 50.0) * Math.PI * 2;
            double radius = 2.0;
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
            double y = pos.getY() + 0.5;

            world.spawnParticles(ParticleTypes.SOUL, x, y, z, 1, 0, 0.5, 0, 0.02);
            world.spawnParticles(ParticleTypes.ENCHANT, x, y + 1, z, 1, 0, 0, 0, 0.1);
        }

        // Central pillar
        world.spawnParticles(ParticleTypes.PORTAL,
                pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                100, 0.3, 0.3, 0.3, 0.5);

        // Totem particles
        world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                30, 0.5, 0.5, 0.5, 0.2);
    }

    /**
     * Dark effect - ominous darkness
     */
    public static void playDarkEffect(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.BLOCKS, 0.5F, 0.5F);

        // Dark smoke circles
        for (int i = 0; i < 40; i++) {
            double angle = (i / 40.0) * Math.PI * 2;
            double radius = 2.0;
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
            double y = pos.getY() + 0.5;

            world.spawnParticles(ParticleTypes.SMOKE, x, y, z, 2, 0, 0.5, 0, 0.03);
            world.spawnParticles(ParticleTypes.LARGE_SMOKE, x, y + 1, z, 1, 0, 0.3, 0, 0.02);
        }

        // Center darkness
        world.spawnParticles(ParticleTypes.SQUID_INK,
                pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                50, 0.3, 0.3, 0.3, 0.1);
    }

    /**
     * Holy effect - divine light
     */
    public static void playHolyEffect(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1.0F, 1.5F);

        // Rays of light
        for (int i = 0; i < 8; i++) {
            double angle = (i / 8.0) * Math.PI * 2;
            for (int j = 0; j < 10; j++) {
                double distance = j * 0.3;
                double x = pos.getX() + 0.5 + Math.cos(angle) * distance;
                double z = pos.getZ() + 0.5 + Math.sin(angle) * distance;
                double y = pos.getY() + 2.0;

                world.spawnParticles(ParticleTypes.END_ROD, x, y, z, 1, 0, -0.5, 0, 0.05);
                world.spawnParticles(ParticleTypes.GLOW, x, y, z, 1, 0, -0.3, 0, 0.03);
            }
        }

        // Falling sparkles
        world.spawnParticles(ParticleTypes.SCRAPE,
                pos.getX() + 0.5, pos.getY() + 3.0, pos.getZ() + 0.5,
                40, 0.5, 0.1, 0.5, 0.1);
    }

    /**
     * Lightning effect - electrical energy
     */
    public static void playLightningEffect(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.BLOCKS, 0.8F, 1.0F);

        // Electric sparks
        for (int i = 0; i < 60; i++) {
            double x = pos.getX() + 0.5 + (Math.random() - 0.5) * 3.0;
            double y = pos.getY() + Math.random() * 3.0;
            double z = pos.getZ() + 0.5 + (Math.random() - 0.5) * 3.0;

            world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 1, 0, 0, 0, 0.1);
            world.spawnParticles(ParticleTypes.FIREWORK, x, y, z, 1, 0, 0, 0, 0.05);
        }
    }

    /**
     * Nature effect - plants and growth
     */
    public static void playNatureEffect(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1.0F, 1.2F);

        // Growing particles
        for (int i = 0; i < 40; i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = Math.random() * 2.0;
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
            double y = pos.getY() + 0.5;

            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 2, 0, 0.5, 0, 0.05);
            world.spawnParticles(ParticleTypes.COMPOSTER, x, y, z, 1, 0, 0.3, 0, 0.03);
        }

        // Spore cloud
        world.spawnParticles(ParticleTypes.SPORE_BLOSSOM_AIR,
                pos.getX() + 0.5, pos.getY() + 2.0, pos.getZ() + 0.5,
                30, 0.5, 0.5, 0.5, 0.02);
    }

    /**
     * Blood effect - crimson and dark
     */
    public static void playBloodEffect(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.BLOCKS, 0.8F, 0.5F);

        // Crimson particles
        for (int i = 0; i < 50; i++) {
            double angle = (i / 50.0) * Math.PI * 2;
            double radius = 1.5;
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
            double y = pos.getY() + 0.5;

            world.spawnParticles(ParticleTypes.CRIMSON_SPORE, x, y, z, 2, 0, 0.5, 0, 0.05);
            world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, x, y + 1, z, 1, 0, 0, 0, 0.1);
        }

        // Dripping effect
        world.spawnParticles(ParticleTypes.DRIPPING_DRIPSTONE_LAVA,
                pos.getX() + 0.5, pos.getY() + 2.0, pos.getZ() + 0.5,
                20, 0.3, 0.1, 0.3, 0.05);
    }
}