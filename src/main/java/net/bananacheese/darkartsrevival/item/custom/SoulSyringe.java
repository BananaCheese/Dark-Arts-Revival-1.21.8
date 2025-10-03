package net.bananacheese.darkartsrevival.item.custom;

import net.bananacheese.darkartsrevival.component.DAComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    // Method 2, 3 & 4: Right-clicking (sneak for slot 2, offhand biomass for slot 4)
    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        ItemStack offhandStack = player.getOffHandStack();

        if (!world.isClient && hand == Hand.MAIN_HAND) {
            int currentLevel = getFillLevel(stack);

            // Sneak right-click fills slot 2 (damages player)
            if (player.isSneaking()) {
                if (currentLevel == 1) {
                    setFillLevel(stack, 2);
                    if (world instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                        player.damage(serverWorld, player.getDamageSources().magic(), 4.0F); //2 hearts of damage
                    }
                    player.sendMessage(Text.literal("Soul Syringe: Slot 2 filled (Living Soul)"), true);
                    return ActionResult.SUCCESS;
                }
            }

            // Right-click with biomass in offhand fills slot 4
            if (currentLevel == 3 && !offhandStack.isEmpty()) {
                BiomassType biomassType = getBiomassType(offhandStack);
                int required = biomassType.getRequiredAmount();

                if (biomassType != BiomassType.NONE && offhandStack.getCount() >= required) {
                    offhandStack.decrement(required);
                    setFillLevel(stack, 4);
                    player.sendMessage(Text.literal("Soul Syringe: Slot 4 filled (Biomass)"), true);
                    return ActionResult.SUCCESS;
                } else if (biomassType != BiomassType.NONE) {
                    player.sendMessage(Text.literal("§cNeed " + required + " " + offhandStack.getName().getString()), true);
                    return ActionResult.FAIL;
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
                    player.sendMessage(Text.literal("Soul Syringe: Slot 3 filled (Dead Soul)"), true);
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

    // Biomass checking
    private static BiomassType getBiomassType(ItemStack stack) {
        // Meat items (32 required)
        if (stack.isOf(Items.BEEF) || stack.isOf(Items.COOKED_BEEF) ||
                stack.isOf(Items.PORKCHOP) || stack.isOf(Items.COOKED_PORKCHOP) ||
                stack.isOf(Items.MUTTON) || stack.isOf(Items.COOKED_MUTTON) ||
                stack.isOf(Items.CHICKEN) || stack.isOf(Items.COOKED_CHICKEN) ||
                stack.isOf(Items.RABBIT) || stack.isOf(Items.COOKED_RABBIT) ||
                stack.isOf(Items.COD) || stack.isOf(Items.COOKED_COD) ||
                stack.isOf(Items.SALMON) || stack.isOf(Items.COOKED_SALMON) ||
                stack.isOf(Items.ROTTEN_FLESH)) {
            return BiomassType.MEAT;
        }

        // Plant matter (64 required)
        if (stack.isOf(Items.WHEAT) || stack.isOf(Items.WHEAT_SEEDS) ||
                stack.isOf(Items.CARROT) || stack.isOf(Items.POTATO) ||
                stack.isOf(Items.BEETROOT) || stack.isOf(Items.BEETROOT_SEEDS) ||
                stack.isOf(Items.APPLE) || stack.isOf(Items.MELON_SLICE) ||
                stack.isOf(Items.PUMPKIN) || stack.isOf(Items.KELP) ||
                stack.isOf(Items.SWEET_BERRIES)) {
            return BiomassType.PLANT;
        }

        return BiomassType.NONE;
    }

    // Enum for biomass types
    private enum BiomassType {
        MEAT(32),
        PLANT(64),
        NONE(0);

        private final int required;

        BiomassType(int required) {
            this.required = required;
        }

        public int getRequiredAmount() {
            return required;
        }
    }
}