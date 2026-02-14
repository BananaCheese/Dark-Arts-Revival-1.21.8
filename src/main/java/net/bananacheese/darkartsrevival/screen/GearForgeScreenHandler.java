package net.bananacheese.darkartsrevival.screen;

import net.bananacheese.darkartsrevival.item.custom.ArmorComponentItem;
import net.bananacheese.darkartsrevival.item.custom.ArmorFrameAttributeModifiers;
import net.bananacheese.darkartsrevival.item.custom.ArmorFrameItem;
import net.bananacheese.darkartsrevival.screen.slot.GearForgeSlot;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

import java.util.List;

public class GearForgeScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    private static final int FRAME_SLOT = 0;
    private static final int UPGRADE_SLOTS_START = 1;
    private static final int UPGRADE_SLOTS_COUNT = 6;
    private static final int INVENTORY_START = UPGRADE_SLOTS_START + UPGRADE_SLOTS_COUNT;

    public GearForgeScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(7));
    }

    public GearForgeScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(DAScreenHandlers.GEAR_FORGE_SCREEN_HANDLER, syncId);

        checkSize(inventory, 7);
        this.inventory = inventory;

        this.addSlot(new GearForgeSlot(inventory, FRAME_SLOT, 113, 38, GearForgeSlot.SlotType.FRAME));

        this.addSlot(new GearForgeSlot(inventory, 1, 101, 17, GearForgeSlot.SlotType.COMPONENT));
        this.addSlot(new GearForgeSlot(inventory, 2, 125, 17, GearForgeSlot.SlotType.COMPONENT));
        this.addSlot(new GearForgeSlot(inventory, 3, 89, 38, GearForgeSlot.SlotType.COMPONENT));
        this.addSlot(new GearForgeSlot(inventory, 4, 137, 38, GearForgeSlot.SlotType.COMPONENT));
        this.addSlot(new GearForgeSlot(inventory, 5, 101, 59, GearForgeSlot.SlotType.COMPONENT));
        this.addSlot(new GearForgeSlot(inventory, 6, 125, 59, GearForgeSlot.SlotType.COMPONENT));

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        8 + col * 18, 87 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 145));
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);

        // After any slot click, check if we need to update the frame
        ItemStack frameStack = inventory.getStack(FRAME_SLOT);

        if (frameStack.getItem() instanceof ArmorFrameItem) {
            // If clicking on the frame slot itself
            if (slotIndex == FRAME_SLOT) {
                // If frame was just placed, sync components FROM frame TO slots
                syncFrameWithComponents();
            }
            // If clicking on any component slot (slots 1-6)
            else if (slotIndex >= UPGRADE_SLOTS_START && slotIndex < INVENTORY_START) {
                // Component was added or removed, save current state to frame
                saveComponentsToFrameRealtime();
            }
        }
        // If the frame was removed, return components to player
        else if (slotIndex == FRAME_SLOT && frameStack.isEmpty()) {
            // Frame was removed, return all components to player
            for (int i = 1; i <= 6; i++) {
                ItemStack componentStack = inventory.getStack(i);
                if (!componentStack.isEmpty()) {
                    player.getInventory().offerOrDrop(componentStack);
                    inventory.setStack(i, ItemStack.EMPTY);
                }
            }
        }
    }

    private void syncFrameWithComponents() {
        ItemStack frameStack = inventory.getStack(FRAME_SLOT);

        if (frameStack.getItem() instanceof ArmorFrameItem) {
            List<ArmorFrameItem.ComponentData> components = ArmorFrameItem.getComponents(frameStack);

            for (int i = 0; i < 6; i++) {
                if (i < components.size()) {
                    ArmorFrameItem.ComponentData data = components.get(i);
                    Identifier itemId = Identifier.tryParse(data.id());
                    if (itemId != null) {
                        Item item = Registries.ITEM.get(itemId);
                        if (item instanceof ArmorComponentItem) {
                            inventory.setStack(i + 1, new ItemStack(item));
                        }
                    }
                } else {
                    inventory.setStack(i + 1, ItemStack.EMPTY);
                }
            }
        }
    }

    /**
     * Saves components to frame in real-time WITHOUT consuming them
     * This updates the frame's stats as you add/remove components
     */
    private void saveComponentsToFrameRealtime() {
        ItemStack frameStack = inventory.getStack(FRAME_SLOT);
        if (frameStack.isEmpty() || !(frameStack.getItem() instanceof ArmorFrameItem)) {
            return;
        }

        // Clear existing components
        NbtCompound nbt = new NbtCompound();
        nbt.put("Components", new NbtList());
        frameStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

        // Add current components from slots WITHOUT consuming them
        for (int i = 1; i <= 6; i++) {
            ItemStack componentStack = inventory.getStack(i);
            if (componentStack.getItem() instanceof ArmorComponentItem component) {
                String componentId = Registries.ITEM.getId(componentStack.getItem()).toString();
                ArmorFrameItem.addComponent(
                        frameStack,
                        componentId,
                        component.getDefenseBonus(),
                        component.getDurabilityBonus()
                );
                // DON'T consume here - only preview the stats
            }
        }

        // Update the armor's attribute modifiers
        ArmorFrameAttributeModifiers.updateAttributes(frameStack);
    }

    /**
     * Final save when closing - this consumes the components
     */
    private void saveComponentsToFrameAndConsume() {
        ItemStack frameStack = inventory.getStack(FRAME_SLOT);
        if (frameStack.isEmpty() || !(frameStack.getItem() instanceof ArmorFrameItem)) {
            return;
        }

        // Clear existing components
        NbtCompound nbt = new NbtCompound();
        nbt.put("Components", new NbtList());
        frameStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

        // Add new components from slots and CONSUME them
        for (int i = 1; i <= 6; i++) {
            ItemStack componentStack = inventory.getStack(i);
            if (componentStack.getItem() instanceof ArmorComponentItem component) {
                String componentId = Registries.ITEM.getId(componentStack.getItem()).toString();
                ArmorFrameItem.addComponent(
                        frameStack,
                        componentId,
                        component.getDefenseBonus(),
                        component.getDurabilityBonus()
                );
                // CONSUME the component item
                componentStack.decrement(1);
            }
        }

        // Update the armor's attribute modifiers
        ArmorFrameAttributeModifiers.updateAttributes(frameStack);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (invSlot < INVENTORY_START) {
                // Moving from forge to inventory
                if (invSlot == FRAME_SLOT) {
                    // Shift-clicking frame out - save and consume components first
                    saveComponentsToFrameAndConsume();

                    // Clear component slots since they were consumed
                    for (int i = 1; i <= 6; i++) {
                        inventory.setStack(i, ItemStack.EMPTY);
                    }
                }

                if (!this.insertItem(originalStack, INVENTORY_START, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from inventory to forge
                if (!this.insertItem(originalStack, FRAME_SLOT, FRAME_SLOT + 1, false)) {
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

        // Save components to frame and consume them when GUI closes
        saveComponentsToFrameAndConsume();

        // Drop any remaining items (frame and any empty component slots)
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                player.getInventory().offerOrDrop(stack);
            }
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}