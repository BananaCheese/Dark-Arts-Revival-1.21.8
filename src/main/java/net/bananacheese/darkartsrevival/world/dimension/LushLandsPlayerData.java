package net.bananacheese.darkartsrevival.world.dimension;

import net.bananacheese.darkartsrevival.component.DimComponents;
import net.bananacheese.darkartsrevival.component.ReturnPositionComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Persists per-player data for The Lush Lands teleporter.
 *
 * Stores the player's overworld XYZ so they can return to the same spot.
 * Data is stored in the player's persistent NBT ("PlayerData" compound)
 * and survives server restarts.
 *
 * Usage:
 *   LushLandsPlayerData.saveOverworldPos(player);
 *   Vec3d pos = LushLandsPlayerData.getOverworldPos(player);
 */
public class LushLandsPlayerData {

    private static final String NBT_KEY  = "LushLandsReturnPos";
    private static final String KEY_X    = "ReturnX";
    private static final String KEY_Y    = "ReturnY";
    private static final String KEY_Z    = "ReturnZ";

    /**
     * Saves the player's current position as their overworld return point.
     * Call this just before teleporting them into The Lush Lands.
     */
    public static void saveOverworldPos(ServerPlayerEntity player) {
        DimComponents.RETURN_POS.get(player)
                .setPos(player.getX(), player.getY(), player.getZ());
    }

    /**
     * Retrieves the saved overworld return position, or null if none was saved.
     */
    public static Vec3d getOverworldPos(ServerPlayerEntity player) {
        ReturnPositionComponent comp = DimComponents.RETURN_POS.get(player);
        return comp.hasPos() ? comp.getPos() : null;
    }

    /**
     * Clears the saved return position (e.g., after a successful return trip).
     */
    public static void clearOverworldPos(ServerPlayerEntity player) {
        DimComponents.RETURN_POS.get(player).clear();
    }
}