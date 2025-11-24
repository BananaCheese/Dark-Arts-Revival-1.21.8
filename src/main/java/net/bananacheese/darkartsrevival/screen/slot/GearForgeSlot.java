package net.bananacheese.darkartsrevival.screen.slot;

import net.bananacheese.darkartsrevival.item.custom.ArmorComponentItem;
import net.bananacheese.darkartsrevival.item.custom.ArmorCoreItem;
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
            case CORE -> stack.getItem() instanceof ArmorCoreItem;
            case COMPONENT -> {
                // Check if there's a core in the center slot
                ItemStack coreStack = inventory.getStack(0);
                if (!(coreStack.getItem() instanceof ArmorCoreItem coreItem)) {
                    yield false; // No core, can't add components
                }

                // Check if the component is compatible with the core
                if (stack.getItem() instanceof ArmorComponentItem component) {
                    yield component.getComponentType().isCompatibleWith(coreItem.getCoreType());
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
        CORE,
        COMPONENT
    }
}