package net.bananacheese.darkartsrevival.block.entity.custom;

import net.bananacheese.darkartsrevival.block.DABlocks;
import net.bananacheese.darkartsrevival.block.entity.DABlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GearForgeBlockEntity extends BlockEntity {
    private boolean isFormed = false;
    private final Map<BlockPos, BlockState> originalBlocks = new HashMap<>();
    private int checkCooldown = 0;
    private static final int CHECK_INTERVAL = 40; // Check every 2 seconds

    public GearForgeBlockEntity(BlockPos pos, BlockState state) {
        super(DABlockEntities.GEAR_FORGE_BLOCK_ENTITY, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;

        checkCooldown--;
        if (checkCooldown <= 0) {
            checkCooldown = CHECK_INTERVAL;

            // Only check if not already formed, or verify structure integrity if formed
            if (!isFormed) {
                boolean wasFormed = isFormed;
                checkAndFormMultiblock(world, pos);

                if (wasFormed != isFormed) {
                    markDirty();
                    world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                }
            } else {
                // Verify structure is still intact
                if (!verifyStructure(world, pos)) {
                    unformMultiblock();
                    markDirty();
                    world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                }
            }
        }
    }

    private boolean verifyStructure(World world, BlockPos controllerPos) {
        // Check if all dummy blocks are still in place
        for (BlockPos dummyPos : originalBlocks.keySet()) {
            if (!world.getBlockState(dummyPos).isOf(DABlocks.MULTIBLOCK_DUMMY)) {
                return false;
            }
        }
        return true;
    }

    private void checkAndFormMultiblock(World world, BlockPos controllerPos) {
        // Define the multiblock pattern
        BlockPos[] requiredPositions = {
                // Bottom layer (Y = 0)
                controllerPos.add(-1, 0, -1),
                controllerPos.add(1, 0, -1),
                controllerPos.add(-1, 0, 1),
                controllerPos.add(1, 0, 1),

                // Middle positions
                controllerPos.add(-1, 0, 0),
                controllerPos.add(1, 0, 0),
                controllerPos.add(0, 0, -1),
                controllerPos.add(0, 0, 1),

                // Top layer (Y = 1)
                controllerPos.add(0, 1, 0)
        };

        Block[] requiredBlocks = {
                Blocks.IRON_BLOCK,
                Blocks.IRON_BLOCK,
                Blocks.IRON_BLOCK,
                Blocks.IRON_BLOCK,

                Blocks.SMOOTH_STONE,
                Blocks.SMOOTH_STONE,
                Blocks.SMOOTH_STONE,
                Blocks.SMOOTH_STONE,

                Blocks.ANVIL
        };

        boolean structureValid = true;
        Map<BlockPos, BlockState> tempOriginalBlocks = new HashMap<>();

        // Check all required blocks
        for (int i = 0; i < requiredPositions.length; i++) {
            BlockState state = world.getBlockState(requiredPositions[i]);
            if (!state.isOf(requiredBlocks[i])) {
                structureValid = false;
                break;
            }
            tempOriginalBlocks.put(requiredPositions[i].toImmutable(), state);
        }

        if (structureValid && !isFormed) {
            // Form the multiblock
            isFormed = true;
            originalBlocks.clear();
            originalBlocks.putAll(tempOriginalBlocks);

            // Replace all blocks with dummy blocks
            for (BlockPos targetPos : requiredPositions) {
                world.setBlockState(targetPos, DABlocks.MULTIBLOCK_DUMMY.getDefaultState(), 3);
            }

            markDirty();
        }
    }

    public void unformMultiblock() {
        if (isFormed && world != null) {
            isFormed = false;

            // Restore original blocks
            for (Map.Entry<BlockPos, BlockState> entry : originalBlocks.entrySet()) {
                BlockPos targetPos = entry.getKey();
                BlockState originalState = entry.getValue();

                // Only restore if it's still a dummy block
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

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        isFormed = view.getBoolean("IsFormed", false);

        // Read original blocks
        originalBlocks.clear();
        if (view.contains("OriginalBlocksCount")) {
            int count = view.getInt("OriginalBlocksCount", 0);
            for (int i = 0; i < count; i++) {
                long posLong = view.getLong("BlockPos" + i, 0);
                if (posLong != 0) {
                    BlockPos pos = BlockPos.fromLong(posLong);
                    originalBlocks.put(pos, Blocks.AIR.getDefaultState());
                }
            }
        }
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putBoolean("IsFormed", isFormed);

        // Save original blocks
        view.putInt("OriginalBlocksCount", originalBlocks.size());
        int index = 0;
        for (Map.Entry<BlockPos, BlockState> entry : originalBlocks.entrySet()) {
            view.putLong("BlockPos" + index, entry.getKey().asLong());
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