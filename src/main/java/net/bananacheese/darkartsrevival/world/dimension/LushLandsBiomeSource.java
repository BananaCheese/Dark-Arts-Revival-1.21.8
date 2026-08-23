package net.bananacheese.darkartsrevival.world.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.bananacheese.darkartsrevival.world.chunk.LushLandsNoiseSampler;

import java.util.stream.Stream;

/**
 * Full implementation of the Lush Lands biome source.
 *
 * This version correctly stores a RegistryEntryLookup<Biome> obtained at codec
 * build time (the standard Minecraft 1.21.x + Fabric pattern for custom biome sources).
 *
 * Biome placement:
 *  ┌───────────────────────────────┐  Y ≥ 220  PLATEAU TOP
 *  │  Scorched / Frozen / Badlands │           (extreme biomes)
 *  ├───────────────────────────────┤  Y 140-220 CLIFF BAND
 *  │       Hanging Cliffs          │           (transition zone)
 *  ├───────────────────────────────┤  Y < 140  VALLEYS
 *  │  Lush Valley / River /        │
 *  │  Mossy Wetlands               │
 *  └───────────────────────────────┘
 */
public class LushLandsBiomeSource extends BiomeSource {

    public static final MapCodec<LushLandsBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.LONG.fieldOf("seed").forGetter(s -> s.seed),
                    RegistryOps.getEntryLookupCodec(RegistryKeys.BIOME)
            ).apply(instance, LushLandsBiomeSource::new)
    );

    // ── Height thresholds ─────────────────────────────────────────────────────
    private static final int VALLEY_MAX_Y  = 140;
    private static final int PLATEAU_MIN_Y = 220;

    // ── Fields ────────────────────────────────────────────────────────────────
    private final long seed;
    private final LushLandsNoiseSampler noiseSampler;

    // Valley biomes
    private final RegistryEntry<Biome> plains;
    private final RegistryEntry<Biome> sunflowerPlains;
    private final RegistryEntry<Biome> forest;
    private final RegistryEntry<Biome> birchForest;
    private final RegistryEntry<Biome> flowerForest;
    private final RegistryEntry<Biome> river;
    private final RegistryEntry<Biome> swamp;
    private final RegistryEntry<Biome> mangroveSwamp;

    // Cliff / transition biomes
    private final RegistryEntry<Biome> stonyPeaks;
    private final RegistryEntry<Biome> windsweptHills;

    // Plateau top biomes
    private final RegistryEntry<Biome> basaltDeltas;     // scorched
    private final RegistryEntry<Biome> netherWastes;     // scorched alt
    private final RegistryEntry<Biome> iceSpikes;        // frozen
    private final RegistryEntry<Biome> frozenPeaks;      // frozen alt
    private final RegistryEntry<Biome> jaggedPeaks;      // frozen alt
    private final RegistryEntry<Biome> badlands;         // badlands
    private final RegistryEntry<Biome> erodedBadlands;   // badlands alt
    private final RegistryEntry<Biome> woodedBadlands;   // badlands alt

    public LushLandsBiomeSource(long seed, RegistryEntryLookup<Biome> lookup) {
        this.seed = seed;
        this.noiseSampler = new LushLandsNoiseSampler(seed);

        // Valley
        this.plains         = lookup.getOrThrow(BiomeKeys.PLAINS);
        this.sunflowerPlains= lookup.getOrThrow(BiomeKeys.SUNFLOWER_PLAINS);
        this.forest         = lookup.getOrThrow(BiomeKeys.FOREST);
        this.birchForest    = lookup.getOrThrow(BiomeKeys.BIRCH_FOREST);
        this.flowerForest   = lookup.getOrThrow(BiomeKeys.FLOWER_FOREST);
        this.river          = lookup.getOrThrow(BiomeKeys.RIVER);
        this.swamp          = lookup.getOrThrow(BiomeKeys.SWAMP);
        this.mangroveSwamp  = lookup.getOrThrow(BiomeKeys.MANGROVE_SWAMP);

        // Cliffs
        this.stonyPeaks     = lookup.getOrThrow(BiomeKeys.STONY_PEAKS);
        this.windsweptHills = lookup.getOrThrow(BiomeKeys.WINDSWEPT_HILLS);

        // Plateau - scorched
        this.basaltDeltas   = lookup.getOrThrow(BiomeKeys.BASALT_DELTAS);
        this.netherWastes   = lookup.getOrThrow(BiomeKeys.NETHER_WASTES);

        // Plateau - frozen
        this.iceSpikes      = lookup.getOrThrow(BiomeKeys.ICE_SPIKES);
        this.frozenPeaks    = lookup.getOrThrow(BiomeKeys.FROZEN_PEAKS);
        this.jaggedPeaks    = lookup.getOrThrow(BiomeKeys.JAGGED_PEAKS);

        // Plateau - badlands
        this.badlands       = lookup.getOrThrow(BiomeKeys.BADLANDS);
        this.erodedBadlands = lookup.getOrThrow(BiomeKeys.ERODED_BADLANDS);
        this.woodedBadlands = lookup.getOrThrow(BiomeKeys.WOODED_BADLANDS);
    }

    public static void register() {
        Registry.register(
                net.minecraft.registry.Registries.BIOME_SOURCE,
                Identifier.of(DarkArtsRevival.MOD_ID, "lush_lands_full"),
                CODEC
        );
    }

    @Override
    protected MapCodec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public RegistryEntry<Biome> getBiome(int biomeX, int biomeY, int biomeZ,
                                         MultiNoiseUtil.MultiNoiseSampler noise) {
        int blockX = biomeX << 2;
        int blockY = biomeY << 2;
        int blockZ = biomeZ << 2;

        boolean isMesa = noiseSampler.isMesaColumn(blockX, blockZ);
        float h = noiseSampler.getBiomeVariety(blockX, blockZ);

        // ── Plateau top ───────────────────────────────────────────────────────
        if (blockY >= PLATEAU_MIN_Y) {
            // Three plateau zones divided by horizontal noise
            if (h < 0.33f) {
                // Scorched / volcanic third
                return h < 0.16f ? basaltDeltas : netherWastes;
            } else if (h < 0.66f) {
                // Frozen third
                if (h < 0.44f) return iceSpikes;
                if (h < 0.55f) return frozenPeaks;
                return jaggedPeaks;
            } else {
                // Badlands third
                if (h < 0.77f) return badlands;
                if (h < 0.88f) return erodedBadlands;
                return woodedBadlands;
            }
        }

        // ── Cliff / shaft band ────────────────────────────────────────────────
        if (blockY >= VALLEY_MAX_Y || isMesa) {
            return h < 0.5f ? stonyPeaks : windsweptHills;
        }

        // ── Valley floor ──────────────────────────────────────────────────────
        // River bands: low horizontal noise values = wetter / more river-like
        if (h < 0.12f) return river;
        if (h < 0.25f) return swamp;
        if (h < 0.35f) return mangroveSwamp;
        if (h < 0.50f) return plains;
        if (h < 0.60f) return sunflowerPlains;
        if (h < 0.72f) return forest;
        if (h < 0.85f) return birchForest;
        return flowerForest;
    }

    /** Smooth noise-based variety in [0,1) varying over ~800 blocks. */
    private float horizontalVariety(int bx, int bz) {
        int blockX = bx << 2;
        int blockZ = bz << 2;
        return noiseSampler.getBiomeVariety(blockX, blockZ);
    }

    @Override
    protected Stream<RegistryEntry<Biome>> biomeStream() {
        return Stream.of(
                plains, sunflowerPlains, forest, birchForest, flowerForest,
                river, swamp, mangroveSwamp,
                stonyPeaks, windsweptHills,
                basaltDeltas, netherWastes,
                iceSpikes, frozenPeaks, jaggedPeaks,
                badlands, erodedBadlands, woodedBadlands
        );
    }
}