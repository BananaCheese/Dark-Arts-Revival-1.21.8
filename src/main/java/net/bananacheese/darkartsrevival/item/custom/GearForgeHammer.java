package net.bananacheese.darkartsrevival.item.custom;

import net.bananacheese.darkartsrevival.block.DABlocks;
import net.bananacheese.darkartsrevival.block.entity.custom.GearForgeBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GearForgeHammer extends Item {

    public GearForgeHammer(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        BlockState state = world.getBlockState(pos);

        if (player == null) {
            return ActionResult.PASS;
        }

        // Check if clicking on Gear Forge block
        if (state.isOf(DABlocks.GEAR_FORGE_BLOCK)) {
            if (!world.isClient) {
                if (world.getBlockEntity(pos) instanceof GearForgeBlockEntity forge) {
                    if (forge.isFormed()) {
                        // Unform the multiblock
                        forge.unformMultiblock();
                        player.sendMessage(Text.literal("Multiblock unformed"), true);
                        world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    } else {
                        // Try to form the multiblock
                        forge.tryFormMultiblock(world, pos);

                        if (forge.isFormed()) {
                            player.sendMessage(Text.literal("Multiblock formed!"), true);
                            world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0f, 1.2f);
                        } else {
                            player.sendMessage(Text.literal("Invalid structure - check pattern"), true);
                            world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.5f, 0.8f);
                        }
                    }
                }
            }
            return ActionResult.SUCCESS;
        }

        // Check if clicking on a dummy block
        if (state.isOf(DABlocks.MULTIBLOCK_DUMMY)) {
            if (!world.isClient) {
                // Find the master block and unform
                BlockPos masterPos = findMasterBlock(world, pos);
                if (masterPos != null && world.getBlockEntity(masterPos) instanceof GearForgeBlockEntity forge) {
                    forge.unformMultiblock();
                    player.sendMessage(Text.literal("Multiblock unformed"), true);
                    world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private BlockPos findMasterBlock(World world, BlockPos dummyPos) {
        // Search in a 5x5x5 area for the master block
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos checkPos = dummyPos.add(x, y, z);
                    if (world.getBlockEntity(checkPos) instanceof GearForgeBlockEntity forge) {
                        if (forge.isFormed() && forge.isDummyPartOfMultiblock(dummyPos)) {
                            return checkPos;
                        }
                    }
                }
            }
        }
        return null;
    }
}
