package net.bananacheese.darkartsrevival.commands;

import net.bananacheese.darkartsrevival.ritual.AlterTier;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class AlterTierCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("altertier")
                    .requires(source -> source.hasPermissionLevel(2)) // OP only
                    .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                            .executes(ctx -> {
                                BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
                                ServerWorld world = ctx.getSource().getWorld();

                                // Get the highest valid tier at this position
                                AlterTier tier = AlterTier.getHighestValidTier(world, pos);

                                // Send result to player
                                ctx.getSource().sendFeedback(() -> Text.literal(
                                        "§6Altar Tier at " + pos.toShortString() + ": §b" + tier.getLevel() +
                                                " §7(" + tier.getDisplayName() + ")"
                                ), false);

                                // Show structure validation status for all tiers
                                ctx.getSource().sendFeedback(() -> Text.literal("§7Structure Status:"), false);
                                for (AlterTier checkTier : AlterTier.values()) {
                                    boolean valid = checkTier.isStructureValid(world, pos);
                                    String status = valid ? "§a✓" : "§c✗";
                                    ctx.getSource().sendFeedback(() -> Text.literal(
                                            "  " + status + " §7Tier " + checkTier.getLevel() + ": " + checkTier.getDisplayName()
                                    ), false);
                                }

                                return 1;
                            })
                    )
                    // Also allow checking the block player is looking at
                    .executes(ctx -> {
                        // Get the block the player is looking at
                        var hitResult = ctx.getSource().getPlayer().raycast(20, 1.0f, false);
                        if (hitResult.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
                            BlockPos pos = ((net.minecraft.util.hit.BlockHitResult) hitResult).getBlockPos();
                            ServerWorld world = ctx.getSource().getWorld();

                            // Get the highest valid tier at this position
                            AlterTier tier = AlterTier.getHighestValidTier(world, pos);

                            // Send result to player
                            ctx.getSource().sendFeedback(() -> Text.literal(
                                    "§6Altar Tier at " + pos.toShortString() + ": §b" + tier.getLevel() +
                                            " §7(" + tier.getDisplayName() + ")"
                            ), false);

                            // Show structure validation status for all tiers
                            ctx.getSource().sendFeedback(() -> Text.literal("§7Structure Status:"), false);
                            for (AlterTier checkTier : AlterTier.values()) {
                                boolean valid = checkTier.isStructureValid(world, pos);
                                String status = valid ? "§a✓" : "§c✗";
                                ctx.getSource().sendFeedback(() -> Text.literal(
                                        "  " + status + " §7Tier " + checkTier.getLevel() + ": " + checkTier.getDisplayName()
                                ), false);
                            }

                            return 1;
                        } else {
                            ctx.getSource().sendError(Text.literal("§cYou must look at a block!"));
                            return 0;
                        }
                    })
            );
        });
    }
}