package net.bananacheese.darkartsrevival.block.entity.custom;

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

import java.util.ArrayList;
import java.util.List;

public class GearForgeBlockEntity extends BlockEntity {
    private boolean isFormed = false;
    private final List<BlockPos> multiblockPositions = new ArrayList<>();
    private int checkCooldown = 0;
    private static final int CHECK_INTERVAL = 20; // Check every second

    public GearForgeBlockEntity(BlockPos pos, BlockState state) {
        super(DABlockEntities.GEAR_FORGE_BLOCK_ENTITY, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;

        checkCooldown--;
        if (checkCooldown <= 0) {
            checkCooldown = CHECK_INTERVAL;
            boolean wasFormed = isFormed;
            checkAndFormMultiblock(world, pos);

            // Sync to client if state changed
            if (wasFormed != isFormed) {
                markDirty();
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }
        }
    }

    private void checkAndFormMultiblock(World world, BlockPos controllerPos) {
        // Define the multiblock pattern
        // This is a simple 3x3x2 structure as an example
        // Adjust positions based on your desired structure

        BlockPos[] requiredPositions = {
                // Bottom layer (Y = 0)
                controllerPos.add(-1, 0, -1), // Iron blocks in corners
                controllerPos.add(1, 0, -1),
                controllerPos.add(-1, 0, 1),
                controllerPos.add(1, 0, 1),

                // Middle positions (optional)
                controllerPos.add(-1, 0, 0),
                controllerPos.add(1, 0, 0),
                controllerPos.add(0, 0, -1),
                controllerPos.add(0, 0, 1),

                // Top layer (Y = 1)
                controllerPos.add(0, 1, 0) // Center top - anvil
        };

        Block[] requiredBlocks = {
                // Bottom layer - iron blocks
                Blocks.IRON_BLOCK,
                Blocks.IRON_BLOCK,
                Blocks.IRON_BLOCK,
                Blocks.IRON_BLOCK,

                // Middle - smooth stone
                Blocks.SMOOTH_STONE,
                Blocks.SMOOTH_STONE,
                Blocks.SMOOTH_STONE,
                Blocks.SMOOTH_STONE,

                // Top - anvil
                Blocks.ANVIL
        };

        boolean structureValid = true;
        List<BlockPos> tempPositions = new ArrayList<>();

        // Check all required blocks
        for (int i = 0; i < requiredPositions.length; i++) {
            BlockState state = world.getBlockState(requiredPositions[i]);
            if (!state.isOf(requiredBlocks[i])) {
                structureValid = false;
                break;
            }
            tempPositions.add(requiredPositions[i]);
        }

        if (structureValid && !isFormed) {
            // Form the multiblock
            isFormed = true;
            multiblockPositions.clear();
            multiblockPositions.addAll(tempPositions);
            markDirty();
        } else if (!structureValid && isFormed) {
            // Unform the multiblock
            unformMultiblock();
        }
    }

    public void unformMultiblock() {
        if (isFormed) {
            isFormed = false;
            multiblockPositions.clear();
            markDirty();
        }
    }

    public boolean isFormed() {
        return isFormed;
    }

    protected void readNbt(ReadView view) {
        super.readData(view);
        isFormed = view.getBoolean("IsFormed", true);

        // Read multiblock positions if needed for more complex structures
        if (view.contains("MultiblockSize")) {
            int size = view.getInt("MultiblockSize", 2);
            multiblockPositions.clear();
            for (int i = 0; i < size; i++) {
                long posLong = view.getLong("Pos" + i, 0);
                multiblockPositions.add(BlockPos.fromLong(posLong));
            }
        }
    }

    protected void writeNbt(WriteView view) {
        super.writeData(view);
        view.putBoolean("IsFormed", isFormed);

        // Save multiblock positions
        view.putInt("MultiblockSize", multiblockPositions.size());
        for (int i = 0; i < multiblockPositions.size(); i++) {
            view.putLong("Pos" + i, multiblockPositions.get(i).asLong());
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}