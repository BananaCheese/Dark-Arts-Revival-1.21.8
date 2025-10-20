package net.bananacheese.darkartsrevival.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

public class ReviveCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("revive")
                    .requires(source -> source.hasPermissionLevel(2)) // OP only
                    .then(CommandManager.argument("target", EntityArgumentType.player())
                            .executes(ctx -> {
                                ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "target");

                                if (target.isSpectator()) {
                                    target.changeGameMode(GameMode.SURVIVAL);
                                    target.setHealth(2.0F); // revive with 1 heart
                                    target.sendMessage(Text.literal("You have been revived!"), false);
                                    return 1;
                                } else {
                                    ctx.getSource().sendError(Text.literal("That player is not dead!"));
                                    return 0;
                                }
                            })
                    )
            );
        });
    }
}
