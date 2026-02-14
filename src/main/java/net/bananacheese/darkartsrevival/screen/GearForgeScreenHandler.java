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
import net.minecraft.text.Text;
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
        // Check state BEFORE the click
        ItemStack frameBeforeClick = inventory.getStack(FRAME_SLOT).copy();
        boolean hadFrameBefore = frameBeforeClick.getItem() instanceof ArmorFrameItem;

        super.onSlotClick(slotIndex, button, actionType, player);

        // Check state AFTER the click
        ItemStack frameAfterClick = inventory.getStack(FRAME_SLOT);
        boolean hasFrameAfter = frameAfterClick.getItem() instanceof ArmorFrameItem;

        // Case 1: Frame was just placed
        if (!hadFrameBefore && hasFrameAfter) {
            // Load existing components from frame into slots
            syncFrameWithComponents();
        }
        // Case 2: Frame was removed manually (picked up with mouse)
        else if (hadFrameBefore && !hasFrameAfter && slotIndex == FRAME_SLOT) {
            // Save components to the frame BEFORE it's taken
            saveComponentsToFrameAndConsume(frameBeforeClick, player);

            // Clear component slots (they were consumed into the frame or returned to player)
            for (int i = 1; i <= 6; i++) {
                inventory.setStack(i, ItemStack.EMPTY);
            }

            // Replace cursor with our updated frame
            ItemStack cursorStack = getCursorStack();
            if (cursorStack.getItem() instanceof ArmorFrameItem) {
                setCursorStack(frameBeforeClick);
            }
        }
        // Case 3: Component was added/removed (frame still present)
        else if (hasFrameAfter && slotIndex >= UPGRADE_SLOTS_START && slotIndex < INVENTORY_START) {
            // Update frame stats in real-time
            updateFrameStatsPreview(player);
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
                            inventory.setStack(i + 1, new ItemStack(item, 1));
                        }
                    }
                } else {
                    inventory.setStack(i + 1, ItemStack.EMPTY);
                }
            }
        }
    }

    private void updateFrameStatsPreview(PlayerEntity player) {
        ItemStack frameStack = inventory.getStack(FRAME_SLOT);
        if (frameStack.isEmpty() || !(frameStack.getItem() instanceof ArmorFrameItem)) {
            return;
        }

        // Clear existing components from frame
        NbtCompound nbt = new NbtCompound();
        nbt.put("Components", new NbtList());
        frameStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

        // Add current components from slots (just for preview stats)
        boolean hadDuplicate = false;
        for (int i = 1; i <= 6; i++) {
            ItemStack componentStack = inventory.getStack(i);
            if (componentStack.getItem() instanceof ArmorComponentItem component) {
                String componentId = Registries.ITEM.getId(componentStack.getItem()).toString();
                boolean added = ArmorFrameItem.addComponent(
                        frameStack,
                        componentId,
                        component.getComponentGroup(),
                        component.getDefenseBonus(),
                        component.getDurabilityBonus(),
                        component.getToughnessBonus()
                );

                // If not added, it's a duplicate group
                if (!added) {
                    hadDuplicate = true;
                }
            }
        }

        // Notify player if duplicate was detected
        if (hadDuplicate && player != null) {
            player.sendMessage(Text.literal("§cCannot add duplicate component types!"), true);
        }

        // Update the armor's attribute modifiers
        ArmorFrameAttributeModifiers.updateAttributes(frameStack);
        inventory.markDirty();
    }

    /**
     * Save components to frame and consume them.
     * Returns any duplicate/rejected components to the player.
     */
    private void saveComponentsToFrameAndConsume(ItemStack frameStack, PlayerEntity player) {
        if (!(frameStack.getItem() instanceof ArmorFrameItem)) {
            return;
        }

        // Clear existing components
        NbtCompound nbt = new NbtCompound();
        nbt.put("Components", new NbtList());
        frameStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

        // Track which components were successfully added
        boolean[] addedSuccessfully = new boolean[6];

        // Try to add new components from slots
        for (int i = 1; i <= 6; i++) {
            ItemStack componentStack = inventory.getStack(i);
            if (componentStack.getItem() instanceof ArmorComponentItem component) {
                String componentId = Registries.ITEM.getId(componentStack.getItem()).toString();
                boolean added = ArmorFrameItem.addComponent(
                        frameStack,
                        componentId,
                        component.getComponentGroup(),
                        component.getDefenseBonus(),
                        component.getDurabilityBonus(),
                        component.getToughnessBonus()
                );

                addedSuccessfully[i - 1] = added;

                if (added) {
                    // Component was successfully added, consume it
                    inventory.setStack(i, ItemStack.EMPTY);
                } else {
                    // Component was rejected (duplicate), return it to player
                    ItemStack rejected = componentStack.copy();
                    inventory.setStack(i, ItemStack.EMPTY); // Clear the slot

                    // Try to add to player inventory, or drop if full
                    if (!player.getInventory().insertStack(rejected)) {
                        player.dropItem(rejected, false);
                    }

                    // Notify player
                    player.sendMessage(Text.literal("§eComponent rejected: " +
                            component.getComponentType().getDisplayName() +
                            " (duplicate type)"), true);
                }
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
                    saveComponentsToFrameAndConsume(originalStack, player);
                }

                if (!this.insertItem(originalStack, INVENTORY_START, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from inventory to forge
                boolean wasInserted = false;

                if (!this.insertItem(originalStack, FRAME_SLOT, FRAME_SLOT + 1, false)) {
                    if (this.insertItem(originalStack, UPGRADE_SLOTS_START, INVENTORY_START, false)) {
                        wasInserted = true;
                        // Component was shift-clicked into a slot, update frame preview
                        updateFrameStatsPreview(player);
                    }
                } else {
                    // Frame was shift-clicked in
                    wasInserted = true;
                    syncFrameWithComponents();
                }

                if (!wasInserted) {
                    return ItemStack.EMPTY;
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

        ItemStack frameStack = inventory.getStack(FRAME_SLOT);

        // If there's a frame with components, save and consume them (returning duplicates)
        if (frameStack.getItem() instanceof ArmorFrameItem) {
            saveComponentsToFrameAndConsume(frameStack, player);
        }

        // Drop all remaining items to player
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