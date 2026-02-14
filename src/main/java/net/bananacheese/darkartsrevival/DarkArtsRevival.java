package net.bananacheese.darkartsrevival;

import net.bananacheese.darkartsrevival.block.DABlocks;
import net.bananacheese.darkartsrevival.block.entity.DABlockEntities;
import net.bananacheese.darkartsrevival.commands.AlterTierCommand;
import net.bananacheese.darkartsrevival.commands.ReviveCommand;
import net.bananacheese.darkartsrevival.component.DAComponents;
import net.bananacheese.darkartsrevival.event.PlayerDeathHandler;
import net.bananacheese.darkartsrevival.item.DAItemGroup;
import net.bananacheese.darkartsrevival.item.DAItems;
import net.bananacheese.darkartsrevival.item.custom.ArmorComponentItem;
import net.bananacheese.darkartsrevival.item.custom.ArmorFrameItem;
import net.bananacheese.darkartsrevival.item.custom.SoulSyringe;
import net.bananacheese.darkartsrevival.recipe.DARecipes;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DarkArtsRevival implements ModInitializer {
    public static final String MOD_ID = "darkartsrevival";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // CRITICAL FIX: Register components, blocks, and items BEFORE the item group
        // The item group references these items, so they must exist first
        DAComponents.registerComponents();
        DABlocks.registerModBlocks();
        DABlockEntities.registerBlockEntities();
        DAItems.registerModItems();
        DAItemGroup.registerItemGroups();  // Moved AFTER items/blocks registration

        // Register other systems
        ReviveCommand.register();
        AlterTierCommand.register();
        PlayerDeathHandler.register();
        DARecipes.registerRecipes();

        // Register tooltip callback for Soul Syringe
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            if (stack.getItem() instanceof SoulSyringe) {
                int fillLevel = SoulSyringe.getFillLevel(stack);
                lines.add(Text.literal("Fill Level: " + fillLevel + "/4"));

                // Show target player name if set
                String targetName = SoulSyringe.getTargetName(stack);
                if (targetName != null) {
                    lines.add(Text.literal("§6Target: " + targetName));
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

        // Register tooltip for Armor Cores - ENHANCED VERSION
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            if (stack.getItem() instanceof ArmorFrameItem coreItem) {
                lines.add(Text.literal("§7" + coreItem.getFrameType().getDisplayName() + " Core"));
                lines.add(Text.literal("§8Place in Gear Forge to customize"));

                // Show total stats
                int totalDefense = ArmorFrameItem.getTotalDefense(stack);
                int totalDurability = ArmorFrameItem.getTotalDurability(stack);

                lines.add(Text.literal(""));
                lines.add(Text.literal("Defense: " + totalDefense).formatted(Formatting.BLUE));
                lines.add(Text.literal("Durability: " + totalDurability).formatted(Formatting.GREEN));

                // Show attached components
                List<ArmorFrameItem.ComponentData> components = ArmorFrameItem.getComponents(stack);
                if (!components.isEmpty()) {
                    lines.add(Text.literal(""));
                    lines.add(Text.literal("Attached Components:").formatted(Formatting.GOLD));
                    for (ArmorFrameItem.ComponentData comp : components) {
                        String componentName = comp.id().substring(comp.id().lastIndexOf(':') + 1)
                                .replace('_', ' ');
                        lines.add(Text.literal("  • " + componentName).formatted(Formatting.GRAY));
                    }
                }

                lines.add(Text.literal(""));
                lines.add(Text.literal(ArmorFrameItem.getComponentCount(stack) + "/6 Component Slots")
                        .formatted(Formatting.DARK_GRAY));
            }
        });

        // Register tooltip for Armor Components
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            if (stack.getItem() instanceof ArmorComponentItem component) {
                lines.add(Text.literal("§9" + component.getComponentType().getDisplayName()));

                if (component.getDefenseBonus() > 0) {
                    lines.add(Text.literal("§a+%d Defense".formatted(component.getDefenseBonus())));
                }

                if (component.getDurabilityBonus() > 0) {
                    lines.add(Text.literal("§a+%d Durability".formatted(component.getDurabilityBonus())));
                }

                lines.add(Text.literal("§8Compatible: " + component.getComponentType().getCompatibility()));
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