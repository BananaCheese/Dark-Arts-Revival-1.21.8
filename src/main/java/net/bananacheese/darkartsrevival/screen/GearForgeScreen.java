package net.bananacheese.darkartsrevival.screen;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GearForgeScreen extends HandledScreen<GearForgeScreenHandler> {
    private static final Identifier TEXTURE =
            Identifier.of("darkartsrevival", "textures/gui/gear_forge.png");

    public GearForgeScreen(GearForgeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 169; // Same as loom
        this.playerInventoryTitleY = this.backgroundHeight - 93;
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);

        // Draw stats in the left panel
        drawStatsPanel(context, mouseX, mouseY);
    }

    private void drawStatsPanel(DrawContext context, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // Stats panel area: X=7, Y=12 to X=64, Y=69 (relative to GUI)
        int panelX = x + 8;
        int panelY = y + 16;

        // Get the frame item from the center slot
        ItemStack frameItem = handler.getInventory().getStack(0);

        if (!frameItem.isEmpty()) {
            // TODO: Calculate actual stats from item and upgrades
            // For now, display placeholder text
            context.drawText(textRenderer, Text.literal("Stats:"), panelX + 2, panelY + 2, 0x404040, false);
            context.drawText(textRenderer, Text.literal("Defense: 5"), panelX + 2, panelY + 12, 0x404040, false);
            context.drawText(textRenderer, Text.literal("Durability: 100"), panelX + 2, panelY + 22, 0x404040, false);

            // Display upgrade effects (placeholder)
            context.drawText(textRenderer, Text.literal("+2 Defense"), panelX + 2, panelY + 35, 0x00AA00, false);
            context.drawText(textRenderer, Text.literal("+10 Dura"), panelX + 2, panelY + 45, 0x00AA00, false);
        } else {
            // No item in frame slot
            context.drawText(textRenderer, Text.literal("Place gear"), panelX + 2, panelY + 2, 0x808080, false);
            context.drawText(textRenderer, Text.literal("to modify"), panelX + 2, panelY + 12, 0x808080, false);
        }
    }
}