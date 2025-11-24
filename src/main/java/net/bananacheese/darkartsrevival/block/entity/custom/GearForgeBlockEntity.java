package net.bananacheese.darkartsrevival.block.entity.custom;

import net.bananacheese.darkartsrevival.block.DABlocks;
import net.bananacheese.darkartsrevival.block.entity.DABlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.Registries;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GearForgeBlockEntity extends BlockEntity implements Inventory {
    private boolean isFormed = false;
    private final Map<BlockPos, BlockState> originalBlocks = new HashMap<>();
    private int checkCooldown = 0;
    private static final int CHECK_INTERVAL = 40;

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(7, ItemStack.EMPTY);

    public GearForgeBlockEntity(BlockPos pos, BlockState state) {
        super(DABlockEntities.GEAR_FORGE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(inventory, slot, amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (world.getBlockEntity(pos) != this) {
            return false;
        }
        return player.squaredDistanceTo((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;

        checkCooldown--;
        if (checkCooldown <= 0 && isFormed) {
            checkCooldown = CHECK_INTERVAL;

            if (!verifyStructure(world, pos)) {
                unformMultiblock();
                markDirty();
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }
        }
    }

    public void tryFormMultiblock(World world, BlockPos pos) {
        if (!isFormed) {
            checkAndFormMultiblock(world, pos);
            if (isFormed) {
                markDirty();
                BlockState state = world.getBlockState(pos);
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }
        }
    }

    private boolean verifyStructure(World world, BlockPos controllerPos) {
        for (BlockPos dummyPos : originalBlocks.keySet()) {
            if (!world.getBlockState(dummyPos).isOf(DABlocks.MULTIBLOCK_DUMMY)) {
                return false;
            }
        }
        return true;
    }

    private void checkAndFormMultiblock(World world, BlockPos controllerPos) {
        // Bottom layer corners (must be crying obsidian)
        BlockPos[] bottomCorners = {
                controllerPos.add(-1, 0, -1),
                controllerPos.add(1, 0, -1),
                controllerPos.add(-1, 0, 1),
                controllerPos.add(1, 0, 1)
        };

        // Bottom layer sides (must be smooth stone)
        BlockPos[] bottomSides = {
                controllerPos.add(-1, 0, 0),
                controllerPos.add(1, 0, 0),
                controllerPos.add(0, 0, -1),
                controllerPos.add(0, 0, 1)
        };

        // Top layer corners (can be any of the 4 required blocks)
        BlockPos[] topCorners = {
                controllerPos.add(1, 1, 1),
                controllerPos.add(1, 1, -1),
                controllerPos.add(-1, 1, 1),
                controllerPos.add(-1, 1, -1)
        };

        // Required blocks for top corners (order doesn't matter)
        Block[] requiredTopBlocks = {
                Blocks.ANVIL,
                Blocks.GRINDSTONE,
                Blocks.SMITHING_TABLE,
                Blocks.BLAST_FURNACE
        };

        boolean structureValid = true;
        Map<BlockPos, BlockState> tempOriginalBlocks = new HashMap<>();

        // Check bottom corners (must be crying obsidian)
        for (BlockPos cornerPos : bottomCorners) {
            BlockState state = world.getBlockState(cornerPos);
            if (!state.isOf(Blocks.CRYING_OBSIDIAN)) {
                structureValid = false;
                break;
            }
            tempOriginalBlocks.put(cornerPos.toImmutable(), state);
        }

        // Check bottom sides (must be smooth stone)
        if (structureValid) {
            for (BlockPos sidePos : bottomSides) {
                BlockState state = world.getBlockState(sidePos);
                if (!state.isOf(Blocks.SMOOTH_STONE)) {
                    structureValid = false;
                    break;
                }
                tempOriginalBlocks.put(sidePos.toImmutable(), state);
            }
        }

        // Check top corners (must have all 4 required blocks in any order)
        if (structureValid) {
            List<Block> foundBlocks = new ArrayList<>();
            for (BlockPos cornerPos : topCorners) {
                BlockState state = world.getBlockState(cornerPos);
                Block block = state.getBlock();
                foundBlocks.add(block);
                tempOriginalBlocks.put(cornerPos.toImmutable(), state);
            }

            // Verify all 4 required blocks are present
            for (Block requiredBlock : requiredTopBlocks) {
                if (!foundBlocks.contains(requiredBlock)) {
                    structureValid = false;
                    break;
                }
            }
        }

        if (structureValid && !isFormed) {
            isFormed = true;
            originalBlocks.clear();
            originalBlocks.putAll(tempOriginalBlocks);

            for (BlockPos targetPos : tempOriginalBlocks.keySet()) {
                world.setBlockState(targetPos, DABlocks.MULTIBLOCK_DUMMY.getDefaultState(), 3);
            }

            markDirty();
        }
    }

    public void unformMultiblock() {
        if (isFormed && world != null) {
            isFormed = false;

            for (Map.Entry<BlockPos, BlockState> entry : originalBlocks.entrySet()) {
                BlockPos targetPos = entry.getKey();
                BlockState originalState = entry.getValue();

                if (world.getBlockState(targetPos).isOf(DABlocks.MULTIBLOCK_DUMMY)) {
                    world.setBlockState(targetPos, originalState, 3);
                }
            }

            originalBlocks.clear();
            markDirty();
        }
    }

    public boolean isFormed() {
        return isFormed;
    }

    public boolean isDummyPartOfMultiblock(BlockPos dummyPos) {
        return originalBlocks.containsKey(dummyPos);
    }

    public BlockState getOriginalBlock(BlockPos pos) {
        return originalBlocks.get(pos);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        isFormed = view.getBoolean("IsFormed", false);

        inventory.clear();
        int inventorySize = view.getInt("InventorySize", 0);
        for (int i = 0; i < inventorySize && i < inventory.size(); i++) {
            String itemId = view.getString("ItemId" + i, "");
            int count = view.getInt("ItemCount" + i, 0);

            if (!itemId.isEmpty() && count > 0) {
                Item item = Registries.ITEM.get(Identifier.tryParse(itemId));
                if (item != null) {
                    ItemStack stack = new ItemStack(item, count);
                    inventory.set(i, stack);
                }
            }
        }

        originalBlocks.clear();
        if (view.contains("OriginalBlocksCount")) {
            int count = view.getInt("OriginalBlocksCount", 0);
            for (int i = 0; i < count; i++) {
                long posLong = view.getLong("BlockPos" + i, 0);
                String blockId = view.getString("BlockId" + i, "minecraft:air");
                if (posLong != 0) {
                    BlockPos pos = BlockPos.fromLong(posLong);
                    Block block = Registries.BLOCK.get(Identifier.tryParse(blockId));
                    originalBlocks.put(pos, block.getDefaultState());
                }
            }
        }
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putBoolean("IsFormed", isFormed);

        int nonEmptySlots = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                Identifier itemId = Registries.ITEM.getId(stack.getItem());
                view.putString("ItemId" + i, itemId.toString());
                view.putInt("ItemCount" + i, stack.getCount());
                nonEmptySlots++;
            }
        }
        view.putInt("InventorySize", nonEmptySlots);

        view.putInt("OriginalBlocksCount", originalBlocks.size());
        int index = 0;
        for (Map.Entry<BlockPos, BlockState> entry : originalBlocks.entrySet()) {
            view.putLong("BlockPos" + index, entry.getKey().asLong());
            Block block = entry.getValue().getBlock();
            Identifier blockId = Registries.BLOCK.getId(block);
            view.putString("BlockId" + index, blockId.toString());
            index++;
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}