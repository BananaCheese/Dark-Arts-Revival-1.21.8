package net.bananacheese.darkartsrevival.ritual;

import net.bananacheese.darkartsrevival.item.custom.SoulSyringe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

import java.util.UUID;

public class RevivalRitual {

    /**
     * Attempts to perform a revival ritual using a soul syringe
     * @param world The server world
     * @param pos The position of the altar
     * @param performer The player performing the ritual
     * @param syringeStack The soul syringe item stack
     * @return true if the ritual was successful
     */
    public static boolean performRitual(ServerWorld world, BlockPos pos, PlayerEntity performer, ItemStack syringeStack) {
        // Validate the syringe
        if (!(syringeStack.getItem() instanceof SoulSyringe)) {
            performer.sendMessage(Text.literal("§cYou need a Soul Syringe to perform this ritual!"), false);
            return false;
        }

        // Check if syringe is fully charged
        int fillLevel = SoulSyringe.getFillLevel(syringeStack);
        if (fillLevel < 4) {
            performer.sendMessage(Text.literal("§cThe Soul Syringe is not fully charged! (" + fillLevel + "/4)"), false);
            return false;
        }

        // Get the target player UUID
        UUID targetUuid = SoulSyringe.getTargetPlayer(syringeStack);
        if (targetUuid == null) {
            performer.sendMessage(Text.literal("§cThe Soul Syringe has no target player!"), false);
            return false;
        }

        // Find the target player
        ServerPlayerEntity targetPlayer = world.getServer().getPlayerManager().getPlayer(targetUuid);
        if (targetPlayer == null) {
            performer.sendMessage(Text.literal("§cTarget player is not online!"), false);
            return false;
        }

        // Check if target is actually dead (in spectator)
        if (!targetPlayer.isSpectator()) {
            performer.sendMessage(Text.literal("§c" + targetPlayer.getName().getString() + " is not dead!"), false);
            return false;
        }

        // Play ritual effects
        playRitualEffects(world, pos, performer);

        // Perform the revival!
        targetPlayer.changeGameMode(GameMode.SURVIVAL);
        targetPlayer.setHealth(2.0F); // Revive with 1 heart

        // Teleport to ritual location (slightly above the altar)
        targetPlayer.requestTeleport(
                pos.getX() + 0.5,
                pos.getY() + 1.5,
                pos.getZ() + 0.5
        );

        // Send messages
        targetPlayer.sendMessage(Text.literal("§aYou have been revived by " + performer.getName().getString() + "!"), false);
        performer.sendMessage(Text.literal("§aSuccessfully revived " + targetPlayer.getName().getString() + "!"), false);

        // Broadcast to server
        world.getServer().getPlayerManager().broadcast(
                Text.literal("§6" + targetPlayer.getName().getString() + " has been brought back from the dead!"),
                false
        );

        // Consume the syringe
        syringeStack.decrement(1);

        return true;
    }

    private static void playRitualEffects(ServerWorld world, BlockPos pos, PlayerEntity performer) {
        // Play dramatic sound
        world.playSound(null, pos, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.BLOCKS, 1.0F, 0.8F);
        world.playSound(null, pos, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.BLOCKS, 0.5F, 1.5F);

        // Spawn particle effects in a circle around the altar
        for (int i = 0; i < 50; i++) {
            double angle = (i / 50.0) * Math.PI * 2;
            double radius = 2.0;
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
            double y = pos.getY() + 0.5;

            // Soul particles
            world.spawnParticles(ParticleTypes.SOUL, x, y, z, 1, 0, 0.5, 0, 0.02);
            // Enchant particles
            world.spawnParticles(ParticleTypes.ENCHANT, x, y + 1, z, 1, 0, 0, 0, 0.1);
        }

        // Spawn particles above the altar
        world.spawnParticles(ParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                100, 0.3, 0.3, 0.3, 0.5);
    }
}