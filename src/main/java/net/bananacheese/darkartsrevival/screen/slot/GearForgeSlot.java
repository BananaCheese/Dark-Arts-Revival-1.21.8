package net.bananacheese.darkartsrevival.screen.slot;

import net.bananacheese.darkartsrevival.item.custom.ArmorComponentItem;
import net.bananacheese.darkartsrevival.item.custom.ArmorFrameItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class GearForgeSlot extends Slot {
    private final SlotType slotType;
    private final Inventory inventory;

    public GearForgeSlot(Inventory inventory, int index, int x, int y, SlotType slotType) {
        super(inventory, index, x, y);
        this.slotType = slotType;
        this.inventory = inventory;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return switch (slotType) {
            case FRAME -> stack.getItem() instanceof ArmorFrameItem;
            case COMPONENT -> {
                // Check if there's a frame in the center slot
                ItemStack frameStack = inventory.getStack(0);
                if (!(frameStack.getItem() instanceof ArmorFrameItem frameItem)) {
                    yield false; // No frame, can't add components
                }

                // Check if the component is compatible with the frame
                if (stack.getItem() instanceof ArmorComponentItem component) {
                    yield component.getComponentType().isCompatibleWith(frameItem.getFrameType());
                }
                yield false;
            }
        };
    }

    @Override
    public int getMaxItemCount() {
        return 1; // All slots only accept 1 item
    }

    public enum SlotType {
        FRAME,
        COMPONENT
    }
}