package net.bananacheese.darkartsrevival.item.custom;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.bananacheese.darkartsrevival.component.DAComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class SoulSyringe extends Item {
    private static final int MAX_FILL_LEVEL = 4;

    public SoulSyringe(Settings settings) {
        super(settings);
    }

    // Method 1: Hitting a mob fills slot 1
    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player && !player.getWorld().isClient) {
            int currentLevel = getFillLevel(stack);

            // Only fill if we're at level 0 (empty)
            if (currentLevel == 0) {
                setFillLevel(stack, 1);
                player.sendMessage(Text.literal("Soul Syringe: Slot 1 filled (Combat)"), true);
            }
        }
        super.postHit(stack, target, attacker);
    }

    // Method 2 & 3: Right-clicking (sneak for slot 2, block for slot 3)
    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (!world.isClient) {
            int currentLevel = getFillLevel(stack);

            // Sneak right-click fills slot 2
            if (player.isSneaking()) {
                if (currentLevel == 1) {
                    setFillLevel(stack, 2);
                    player.sendMessage(Text.literal("Soul Syringe: Slot 2 filled (Essence)"), true);
                    return ActionResult.SUCCESS;
                }
            }
        }

        return ActionResult.PASS;
    }

    // Method 3: Right-clicking a specific block
    @Override
    public ActionResult useOnBlock(net.minecraft.item.ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();

        if (!world.isClient && player != null) {
            int currentLevel = getFillLevel(stack);

            // Check if the block is your target block (change this to your desired block)
            // Example: if (world.getBlockState(pos).isOf(Blocks.SOUL_SAND)) {
            // For now, using any solid block as example - CHANGE THIS
            if (world.getBlockState(pos).isSolidBlock(world, pos)) {
                if (currentLevel == 2) {
                    setFillLevel(stack, 3);
                    player.sendMessage(Text.literal("Soul Syringe: Slot 3 filled (Environmental)"), true);
                    return ActionResult.SUCCESS;
                }
            }
        }

        return ActionResult.PASS;
    }

    // Helper methods for component data
    public static int getFillLevel(ItemStack stack) {
        return stack.getOrDefault(DAComponents.SYRINGE_FILL_LEVEL, 0);
    }

    public static void setFillLevel(ItemStack stack, int level) {
        stack.set(DAComponents.SYRINGE_FILL_LEVEL, Math.min(level, MAX_FILL_LEVEL));
    }

    // Tooltip to show fill level
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        DarkArtsRevival.LOGGER.info("Tooltip called! Fill level: " + getFillLevel(stack));
        int fillLevel = getFillLevel(stack);
        tooltip.add(Text.literal("Fill Level: " + fillLevel + "/" + MAX_FILL_LEVEL));

        if (fillLevel < MAX_FILL_LEVEL) {
            tooltip.add(Text.literal(""));
            if (fillLevel == 0) {
                tooltip.add(Text.literal("§7• Hit a mob to fill slot 1"));
            } else if (fillLevel == 1) {
                tooltip.add(Text.literal("§7• Sneak + Right-click to fill slot 2"));
            } else if (fillLevel == 2) {
                tooltip.add(Text.literal("§7• Right-click a block to fill slot 3"));
            } else if (fillLevel == 3) {
                tooltip.add(Text.literal("§7• Craft with biomass to fill slot 4"));
            }
        } else {
            tooltip.add(Text.literal("§aFully Charged!"));
        }
    }
}