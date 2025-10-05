package net.bananacheese.darkartsrevival;

import net.bananacheese.darkartsrevival.block.DABlocks;
import net.bananacheese.darkartsrevival.block.entity.DABlockEntities;
import net.bananacheese.darkartsrevival.commands.ReviveCommand;
import net.bananacheese.darkartsrevival.component.DAComponents;
import net.bananacheese.darkartsrevival.event.PlayerDeathHandler;
import net.bananacheese.darkartsrevival.item.DAItemGroup;
import net.bananacheese.darkartsrevival.item.DAItems;
import net.bananacheese.darkartsrevival.item.custom.SoulSyringe;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DarkArtsRevival implements ModInitializer {
	public static final String MOD_ID = "darkartsrevival";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        DAComponents.registerComponents();
        DAItemGroup.registerItemGroups();
        DABlocks.registerModBlocks();
        DABlockEntities.registerBlockEntities();
        DAItems.registerModItems();
        ReviveCommand.register();
        PlayerDeathHandler.register();

        // Register tooltip callback
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            if (stack.getItem() instanceof SoulSyringe) {
                int fillLevel = SoulSyringe.getFillLevel(stack);
                lines.add(Text.literal("Fill Level: " + fillLevel + "/4"));

                // Show target player if set
                java.util.UUID targetUuid = SoulSyringe.getTargetPlayer(stack);
                if (targetUuid != null) {
                    String playerName = getPlayerName(tooltipContext, targetUuid);
                    lines.add(Text.literal("§6Target: " + playerName));
                }

                if (fillLevel < 4) {
                    lines.add(Text.literal(""));
                    if (fillLevel == 0) {
                        lines.add(Text.literal("§7• Hit a mob to fill slot 1"));
                    } else if (fillLevel == 1) {
                        lines.add(Text.literal("§7• Sneak + Right-click to fill slot 2"));
                    } else if (fillLevel == 2) {
                        lines.add(Text.literal("§7• Right-click a player head to fill slot 3"));
                    } else if (fillLevel == 3) {
                        lines.add(Text.literal("§7• Right-click with biomass in offhand"));
                        lines.add(Text.literal("§7  (32 meat or 64 plant matter)"));
                    }
                } else {
                    lines.add(Text.literal("§aFully Charged!"));
                    lines.add(Text.literal("§7Use on an altar to revive"));
                }
            }
        });
    }

    private static String getPlayerName(net.minecraft.item.Item.TooltipContext context, java.util.UUID uuid) {
        // Try to get player from client world if available
        if (net.minecraft.client.MinecraftClient.getInstance() != null &&
                net.minecraft.client.MinecraftClient.getInstance().world != null) {
            var player = net.minecraft.client.MinecraftClient.getInstance().world.getPlayerByUuid(uuid);
            if (player != null) {
                return player.getGameProfile().getName();
            }
        }
        // If player not found, show first 8 characters of UUID
        return uuid.toString().substring(0, 8) + "...";
    }
}