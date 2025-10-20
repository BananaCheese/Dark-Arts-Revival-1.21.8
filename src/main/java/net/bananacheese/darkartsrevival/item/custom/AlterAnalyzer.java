package net.bananacheese.darkartsrevival.item.custom;

import net.bananacheese.darkartsrevival.ritual.AlterTier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AlterAnalyzer extends Item {
    public AlterAnalyzer(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient) {
            // Raycast to find what the player is looking at
            var hitResult = player.raycast(20, 1.0f, false);

            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();

                // Get the altar tier at this position
                AlterTier tier = AlterTier.getHighestValidTier(world, pos);

                // Send feedback
                player.sendMessage(Text.literal("§6═══════════════════════════"), false);
                player.sendMessage(Text.literal("§6Altar Analysis"), false);
                player.sendMessage(Text.literal("§7Position: §b" + pos.toShortString()), false);
                player.sendMessage(Text.literal("§7Current Tier: §b" + tier.getLevel() + " §7- " + tier.getDisplayName()), false);
                player.sendMessage(Text.literal("§6═══════════════════════════"), false);

                // Show structure status for next tier
                AlterTier nextTier = null;
                for (AlterTier t : AlterTier.values()) {
                    if (t.getLevel() == tier.getLevel() + 1) {
                        nextTier = t;
                        break;
                    }
                }

                if (nextTier != null) {
                    player.sendMessage(Text.literal("§7To upgrade to §bTier " + nextTier.getLevel() + "§7:"), false);
                    player.sendMessage(Text.literal("§7" + nextTier.getDisplayName()), false);
                } else {
                    player.sendMessage(Text.literal("§aAltar is at maximum tier!"), false);
                }

                return ActionResult.SUCCESS;
            } else {
                player.sendMessage(Text.literal("§cYou must look at a block!"), true);
                return ActionResult.FAIL;
            }
        }

        return ActionResult.PASS;
    }
}