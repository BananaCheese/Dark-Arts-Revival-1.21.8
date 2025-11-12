package net.bananacheese.darkartsrevival.ritual;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.bananacheese.darkartsrevival.block.entity.custom.AlterBlockEntity;
import net.bananacheese.darkartsrevival.block.entity.custom.PedestalBlockEntity;
import net.bananacheese.darkartsrevival.item.custom.SoulSyringe;
import net.bananacheese.darkartsrevival.recipe.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RitualHandler {

    /**
     * Attempts to perform a ritual based on what's being held
     */
    public static boolean attemptRitual(ServerWorld world, BlockPos pos, PlayerEntity performer, ItemStack heldStack) {
        // Check for Soul Syringe (Revival ritual) - kept as special case
        if (heldStack.getItem() instanceof SoulSyringe) {
            return RevivalRitual.perform(world, pos, performer, heldStack);
        }

        performer.sendMessage(Text.literal("§7Activate the altar with redstone or place items"), true);
        return false;
    }

    /**
     * Performs rituals when the altar receives a redstone pulse
     * Now uses the recipe system to check for valid rituals
     */
    public static boolean performRitualOnRedstone(ServerWorld world, BlockPos pos, PlayerEntity performer) {
        if (!(world.getBlockEntity(pos) instanceof AlterBlockEntity alterEntity)) {
            return false;
        }

        // Get current altar tier
        AlterTier currentTier = AlterTier.getHighestValidTier(world, pos);
        ItemStack altarStack = alterEntity.getStack(0);
        int fluidAmount = alterEntity.getFluidAmount();

        // Get items from surrounding pedestals
        List<ItemStack> pedestalItems = getPedestalItems(world, pos, currentTier);

        // Create recipe input
        RitualRecipeInput input = new RitualRecipeInput(
                currentTier.getLevel(),
                altarStack,
                fluidAmount,
                pedestalItems
        );

        // First check for revival ritual (special handling)
        Optional<RevivalRitualRecipe> optionalRevivalRecipe = world.getRecipeManager()
                .getFirstMatch(RevivalRitualRecipeType.INSTANCE, input, world)
                .map(entry -> entry.value());

        if (optionalRevivalRecipe.isPresent()) {
            RevivalRitualRecipe recipe = optionalRevivalRecipe.get();
            DarkArtsRevival.LOGGER.info("Found matching revival ritual recipe!");

            // Perform the revival ritual
            return performRevivalRitual(world, pos, alterEntity, recipe, performer, input);
        }

        // Check for normal ritual recipes
        Optional<RitualRecipe> optionalRecipe = world.getRecipeManager()
                .getFirstMatch(RitualRecipeType.INSTANCE, input, world)
                .map(entry -> entry.value());

        if (optionalRecipe.isPresent()) {
            RitualRecipe recipe = optionalRecipe.get();

            DarkArtsRevival.LOGGER.info("Found matching ritual recipe! Tier: {}, Result: {}",
                    recipe.getMinTier(), recipe.getResultItem());

            // Consume ingredients
            consumeRitualIngredients(world, pos, alterEntity, recipe.getFluidAmount(), recipe.getPedestalItems());

            // Create result item
            ItemStack result = recipe.craft(input, world.getRegistryManager());
            alterEntity.setStack(0, result);
            alterEntity.markDirty();

            // Play ritual effects
            playRitualEffects(world, pos, recipe.getMinTier(), recipe.getEffectName());

            // Update block state
            BlockState state = world.getBlockState(pos);
            world.updateListeners(pos, state, state, net.minecraft.block.Block.NOTIFY_ALL);
            world.getChunkManager().markForUpdate(pos);

            // Send success message
            if (performer != null) {
                performer.sendMessage(Text.literal("§a✓ Ritual complete!"), false);
            }

            return true;
        }

        // No matching recipe
        if (performer != null) {
            performer.sendMessage(Text.literal("§c✗ No valid ritual found"), true);
        }
        return false;
    }

    /**
     * Performs a revival ritual with special handling
     */
    private static boolean performRevivalRitual(ServerWorld world, BlockPos pos, AlterBlockEntity alterEntity,
                                                RevivalRitualRecipe recipe, PlayerEntity performer,
                                                RitualRecipeInput input) {
        ItemStack syringeStack = alterEntity.getStack(0);

        // Get the target player UUID from syringe
        java.util.UUID targetUuid = net.bananacheese.darkartsrevival.item.custom.SoulSyringe.getTargetPlayer(syringeStack);
        if (targetUuid == null) {
            if (performer != null) {
                performer.sendMessage(Text.literal("§c✗ The Soul Syringe has no target player!"), false);
            }
            return false;
        }

        // Find the target player
        net.minecraft.server.network.ServerPlayerEntity targetPlayer = world.getServer().getPlayerManager().getPlayer(targetUuid);
        if (targetPlayer == null) {
            if (performer != null) {
                performer.sendMessage(Text.literal("§c✗ Target player is not online!"), false);
            }
            return false;
        }

        // Check if target is actually dead (in spectator)
        if (!targetPlayer.isSpectator()) {
            if (performer != null) {
                performer.sendMessage(Text.literal("§c✗ " + targetPlayer.getName().getString() + " is not dead!"), false);
            }
            return false;
        }

        // Consume ingredients (including the syringe)
        consumeRitualIngredients(world, pos, alterEntity, recipe.getFluidAmount(), recipe.getPedestalItems());

        // Perform the revival!
        targetPlayer.changeGameMode(net.minecraft.world.GameMode.SURVIVAL);
        targetPlayer.setHealth(2.0F); // Revive with 1 heart

        // Teleport to ritual location (slightly above the altar)
        targetPlayer.requestTeleport(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);

        // Play dramatic ritual effects
        RitualEffects.play(world, pos, "revival");

        // Send messages
        targetPlayer.sendMessage(Text.literal("§a✓ You have been revived by " +
                (performer != null ? performer.getName().getString() : "the ritual") + "!"), false);
        if (performer != null) {
            performer.sendMessage(Text.literal("§a✓ Successfully revived " + targetPlayer.getName().getString() + "!"), false);
        }

        // Broadcast to server
        world.getServer().getPlayerManager().broadcast(
                Text.literal("§6✦ " + targetPlayer.getName().getString() + " has been brought back from the dead!"),
                false
        );

        return true;
    }

    /**
     * Plays special effects for revival ritual
     */
    private static void playRevivalRitualEffects(ServerWorld world, BlockPos pos) {
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

            world.spawnParticles(ParticleTypes.SOUL, x, y, z, 1, 0, 0.5, 0, 0.02);
            world.spawnParticles(ParticleTypes.ENCHANT, x, y + 1, z, 1, 0, 0, 0, 0.1);
        }

        // Spawn particles above the altar
        world.spawnParticles(ParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                100, 0.3, 0.3, 0.3, 0.5);
    }

    /**
     * Gets all items from pedestals around the altar based on tier
     */
    private static List<ItemStack> getPedestalItems(ServerWorld world, BlockPos altarPos, AlterTier tier) {
        List<ItemStack> items = new ArrayList<>();
        List<BlockPos> pedestalPositions = getPedestalPositions(altarPos, tier);

        for (BlockPos pedestalPos : pedestalPositions) {
            if (world.getBlockEntity(pedestalPos) instanceof PedestalBlockEntity pedestalEntity) {
                ItemStack stack = pedestalEntity.getStack(0);
                if (!stack.isEmpty()) {
                    items.add(stack.copy());
                }
            }
        }

        return items;
    }

    /**
     * Gets pedestal positions based on altar tier
     */
    private static List<BlockPos> getPedestalPositions(BlockPos altarPos, AlterTier tier) {
        List<BlockPos> positions = new ArrayList<>();

        switch (tier) {
            case TIER_3 -> {
                // 4 pedestals at corners (distance 2)
                positions.add(altarPos.add(2, 0, 2));
                positions.add(altarPos.add(-2, 0, 2));
                positions.add(altarPos.add(2, 0, -2));
                positions.add(altarPos.add(-2, 0, -2));
            }
            case TIER_4 -> {
                // 8 pedestals in octagon pattern (distance 3 and 2)
                positions.add(altarPos.add(3, 0, 0));
                positions.add(altarPos.add(-3, 0, 0));
                positions.add(altarPos.add(0, 0, 3));
                positions.add(altarPos.add(0, 0, -3));
                positions.add(altarPos.add(2, 0, 2));
                positions.add(altarPos.add(2, 0, -2));
                positions.add(altarPos.add(-2, 0, 2));
                positions.add(altarPos.add(-2, 0, -2));
            }
        }

        return positions;
    }

    /**
     * Consumes ingredients from altar and pedestals
     */
    private static void consumeRitualIngredients(ServerWorld world, BlockPos altarPos,
                                                 AlterBlockEntity alterEntity,
                                                 int fluidAmount,
                                                 List<Ingredient> pedestalIngredients) {
        // Remove altar item
        alterEntity.removeStack(0);

        // Drain fluid
        alterEntity.drainFluid(fluidAmount);

        // Remove pedestal items (match ingredients)
        AlterTier tier = AlterTier.getHighestValidTier(world, altarPos);
        List<BlockPos> pedestalPositions = getPedestalPositions(altarPos, tier);

        List<Integer> usedPedestals = new ArrayList<>();

        for (net.minecraft.recipe.Ingredient ingredient : pedestalIngredients) {
            for (int i = 0; i < pedestalPositions.size(); i++) {
                if (usedPedestals.contains(i)) continue;

                BlockPos pedestalPos = pedestalPositions.get(i);
                if (world.getBlockEntity(pedestalPos) instanceof PedestalBlockEntity pedestalEntity) {
                    ItemStack stack = pedestalEntity.getStack(0);
                    if (ingredient.test(stack)) {
                        pedestalEntity.removeStack(0);

                        // Update pedestal block state to sync with client
                        BlockState pedestalState = world.getBlockState(pedestalPos);
                        world.updateListeners(pedestalPos, pedestalState, pedestalState, net.minecraft.block.Block.NOTIFY_ALL);
                        world.getChunkManager().markForUpdate(pedestalPos);

                        usedPedestals.add(i);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Plays ritual effects based on tier
     */
    private static void playRitualEffects(ServerWorld world, BlockPos pos, int tier, String effectName) {
        // Sound effects - more dramatic for higher tiers
        if (tier >= 3) {
            world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_CLUSTER_STEP,
                    SoundCategory.BLOCKS, 1.0F, 1.2F);
        }
        if (tier >= 4) {
            world.playSound(null, pos, SoundEvents.BLOCK_PORTAL_TRIGGER,
                    SoundCategory.BLOCKS, 0.5F, 1.5F);
        }

        // Particle effects
        int particleCount = 20 + (tier * 10);
        double radius = 1.0 + (tier * 0.5);

        for (int i = 0; i < particleCount; i++) {
            double angle = (i / (double) particleCount) * Math.PI * 2;
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
            double y = pos.getY() + 0.5;

            world.spawnParticles(ParticleTypes.ENCHANT, x, y, z, 1, 0, 0.1, 0, 0.05);
            if (tier >= 3) {
                world.spawnParticles(ParticleTypes.GLOW, x, y + 0.5, z, 1, 0, 0.1, 0, 0.05);
            }
        }

        // Central pillar effect for high tier rituals
        if (tier >= 4) {
            world.spawnParticles(ParticleTypes.PORTAL,
                    pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                    50, 0.2, 0.2, 0.2, 0.3);
        }
    }
}