package net.bananacheese.darkartsrevival.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class GearForgeScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    // Define slot indices
    private static final int CORE_SLOT = 0; // Center of hexagon - armor/tool core
    private static final int UPGRADE_SLOTS_START = 1;
    private static final int UPGRADE_SLOTS_COUNT = 6; // 6 surrounding slots
    private static final int INVENTORY_START = UPGRADE_SLOTS_START + UPGRADE_SLOTS_COUNT;

    public GearForgeScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(7)); // 1 core + 6 upgrades
    }

    public GearForgeScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(DAScreenHandlers.GEAR_FORGE_SCREEN_HANDLER, syncId);

        checkSize(inventory, 7);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        // Gear Forge Hexagon Slots

        // Center slot - Core armor/tool piece
        this.addSlot(new Slot(inventory, CORE_SLOT, 113, 38));

        // Surrounding upgrade slots (hexagon pattern)
        this.addSlot(new Slot(inventory, 1, 101, 17));  // Top-left
        this.addSlot(new Slot(inventory, 2, 125, 17));  // Top-right
        this.addSlot(new Slot(inventory, 3, 89, 38));   // Middle-left
        this.addSlot(new Slot(inventory, 4, 137, 38));  // Middle-right
        this.addSlot(new Slot(inventory, 5, 101, 59));  // Bottom-left
        this.addSlot(new Slot(inventory, 6, 125, 59));  // Bottom-right

        // Player Inventory (3x9) - same as loom
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        8 + col * 18, 87 + row * 18));
            }
        }

        // Player Hotbar (1x9) - same as loom
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 145));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (invSlot < INVENTORY_START) {
                // From forge to player inventory
                if (!this.insertItem(originalStack, INVENTORY_START, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player inventory to forge
                // Try center slot first, then upgrade slots
                if (!this.insertItem(originalStack, CORE_SLOT, CORE_SLOT + 1, false)) {
                    if (!this.insertItem(originalStack, UPGRADE_SLOTS_START, INVENTORY_START, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    // Getter for accessing inventory from screen (for stats display)
    public Inventory getInventory() {
        return this.inventory;
    }
}