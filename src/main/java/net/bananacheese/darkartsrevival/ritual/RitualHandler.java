package net.bananacheese.darkartsrevival.ritual;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.bananacheese.darkartsrevival.item.custom.SoulSyringe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class RitualHandler {

    /**
     * Attempts to perform a ritual based on what's being held
     */
    public static boolean attemptRitual(ServerWorld world, BlockPos pos, PlayerEntity performer, ItemStack heldStack) {
        // Check for Soul Syringe (Revival ritual)
        if (heldStack.getItem() instanceof SoulSyringe) {
            return RevivalRitual.perform(world, pos, performer, heldStack);
        }

        performer.sendMessage(Text.literal("§7Activate the altar with redstone or place items"), true);
        return false;
    }

    /**
     * Performs rituals when the altar receives a redstone pulse
     * Checks what's in the altar and performs appropriate ritual
     */
    public static boolean performRitualOnRedstone(ServerWorld world, BlockPos pos, PlayerEntity performer) {
        if (!(world.getBlockEntity(pos) instanceof net.bananacheese.darkartsrevival.block.entity.custom.AlterBlockEntity alterEntity)) {
            return false;
        }

        ItemStack altarStack = alterEntity.getStack(0);
        int fluidAmount = alterEntity.getFluidAmount();

        DarkArtsRevival.LOGGER.info("Ritual check - Item: {}, Fluid: {}", altarStack.getItem(), fluidAmount);

        // Analyzer Creation: Gold Ingot + Water
        if (altarStack.getItem() == Items.GOLD_INGOT && fluidAmount >= 1000) {
            DarkArtsRevival.LOGGER.info("Performing analyzer ritual!");

            // Remove old items first
            alterEntity.removeStack(0);
            alterEntity.drainFluid(1000);

            // Perform ritual (which sets new analyzer)
            boolean success = AlterAnalyzerRitual.perform(world, pos, performer, altarStack);

            if (success) {
                alterEntity.markDirty();

                // Get the block state and update all listeners
                BlockState state = world.getBlockState(pos);
                world.updateListeners(pos, state, state, net.minecraft.block.Block.NOTIFY_ALL);
                world.getChunkManager().markForUpdate(pos);

                DarkArtsRevival.LOGGER.info("Analyzer ritual complete!");
            }
            return success;
        }

        return false;
    }
}