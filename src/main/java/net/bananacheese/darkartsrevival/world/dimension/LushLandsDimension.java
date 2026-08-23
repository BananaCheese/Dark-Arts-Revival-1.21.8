package net.bananacheese.darkartsrevival.world.dimension;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.bananacheese.darkartsrevival.world.biome.LushLandsBiomes;
import net.bananacheese.darkartsrevival.world.chunk.LushLandsChunkGenerator;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

/**
 * Central registration hub for The Lush Lands dimension.
 *
 * The Lush Lands is the first of three custom dimensions in Dark Arts Revival.
 * It features:
 *  - Tall, extruded "mesa" landmasses rising straight up from valley floors
 *  - Dramatically overhanging edges on each extrusion (up to 20 blocks of overhang)
 *  - Lush valleys (plains, rivers, wetlands) between the extrusions
 *  - Extreme biomes (volcanoes, frozen peaks) atop the plateaus
 *  - Overall build-height of 512 blocks (minY = -64, maxY = 448)
 *
 * Registration flow:
 *  1. This class exposes the RegistryKey constants used elsewhere.
 *  2. The actual DimensionType + ChunkGenerator are defined in JSON data files
 *     under data/darkartsrevival/dimension/ and data/darkartsrevival/dimension_type/.
 *  3. LushLandsChunkGenerator is registered as a codec so Minecraft can deserialize it.
 *  4. Call LushLandsDimension.register() from your ModInitializer BEFORE world gen.
 */
public class LushLandsDimension {

    /** The world registry key – used anywhere you need a reference to this dimension's World. */
    public static final RegistryKey<World> WORLD_KEY = RegistryKey.of(
            RegistryKeys.WORLD,
            Identifier.of(DarkArtsRevival.MOD_ID, "lush_lands")
    );

    /** The dimension type key – referenced inside the dimension JSON. */
    public static final RegistryKey<DimensionType> DIMENSION_TYPE_KEY = RegistryKey.of(
            RegistryKeys.DIMENSION_TYPE,
            Identifier.of(DarkArtsRevival.MOD_ID, "lush_lands")
    );

    public static void register() {
        // Register the custom chunk generator codec so the game can deserialize it from JSON
        LushLandsChunkGenerator.register();

        // Register biome sources
        LushLandsBiomeSource.register();

        DarkArtsRevival.LOGGER.info("[LushLands] Dimension systems registered.");
    }
}