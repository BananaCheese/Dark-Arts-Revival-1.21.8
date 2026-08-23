package net.bananacheese.darkartsrevival.world.biome;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

/**
 * Programmatic biome definitions for The Lush Lands.
 *
 * These are registered via the Fabric RegistryEvents bootstrap pattern.
 * Each biome must have a corresponding JSON file under:
 *   data/darkartsrevival/worldgen/biome/<name>.json
 *
 * The Java definitions here serve as the canonical source; the JSON files
 * can supplement or override settings for datapack customization.
 *
 * Bootstrap this class in your DynamicRegistrySetupCallback or equivalent.
 */
public class LushLandsBiomes {

    // ─── Registry keys ────────────────────────────────────────────────────────
    public static final RegistryKey<Biome> LUSH_VALLEY     = key("lush_valley");
    public static final RegistryKey<Biome> CRYSTAL_RIVER   = key("crystal_river");
    public static final RegistryKey<Biome> MOSSY_WETLANDS  = key("mossy_wetlands");
    public static final RegistryKey<Biome> HANGING_CLIFFS  = key("hanging_cliffs");
    public static final RegistryKey<Biome> SCORCHED_PLATEAU= key("scorched_plateau");
    public static final RegistryKey<Biome> FROZEN_PEAKS    = key("frozen_peaks");
    public static final RegistryKey<Biome> PLATEAU_BADLANDS= key("plateau_badlands");

    private static RegistryKey<Biome> key(String name) {
        return RegistryKey.of(RegistryKeys.BIOME, Identifier.of(DarkArtsRevival.MOD_ID, name));
    }

    // ─── Bootstrap (called from DynamicRegistrySetupCallback) ────────────────

    /**
     * Register all Lush Lands biomes into the dynamic registry.
     *
     * Wire this up like:
     * <pre>{@code
     *   DynamicRegistrySetupCallback.EVENT.register(registryView -> {
     *       registryView.registerEntryAdder(RegistryKeys.BIOME, (ctx, key) -> {
     *           // no-op; biomes come from JSON
     *       });
     *   });
     * }</pre>
     *
     * Or more simply, rely entirely on the JSON data files at
     * data/darkartsrevival/worldgen/biome/*.json – Minecraft will load them
     * automatically as long as they are on the data path.
     *
     * This bootstrap method provides the Java-side biome objects for any
     * code that needs to create them programmatically (e.g., tests, tools).
     */
    public static void bootstrap(Registerable<Biome> registerable) {
        registerable.register(LUSH_VALLEY,     lushValley());
        registerable.register(CRYSTAL_RIVER,   crystalRiver());
        registerable.register(MOSSY_WETLANDS,  mossyWetlands());
        registerable.register(HANGING_CLIFFS,  hangingCliffs());
        registerable.register(SCORCHED_PLATEAU,scorchedPlateau());
        registerable.register(FROZEN_PEAKS,    frozenPeaks());
        registerable.register(PLATEAU_BADLANDS,plateauBadlands());
    }

    // ─── Biome builders ──────────────────────────────────────────────────────

    /** Green, lush valley floor. Warm, rainy, full of trees and tall grass. */
    private static Biome lushValley() {
        SpawnSettings.Builder spawns = new SpawnSettings.Builder();
        spawns.creatureSpawnProbability(0.1f);
        addBasicMonsters(spawns);
        spawns.spawn(net.minecraft.entity.SpawnGroup.CREATURE, 8,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.COW, 8, 4));
        spawns.spawn(net.minecraft.entity.SpawnGroup.CREATURE, 12,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.SHEEP, 12, 4));
        spawns.spawn(net.minecraft.entity.SpawnGroup.CREATURE, 10,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.PIG,   10, 4));

        return new Biome.Builder()
                .precipitation(true)
                .temperature(0.8f)
                .downfall(0.9f)
                .effects(new BiomeEffects.Builder()
                        .skyColor(8103167)
                        .fogColor(12638463)
                        .waterColor(4185759)
                        .waterFogColor(329011)
                        .grassColor(5635925)
                        .foliageColor(5635925)
                        .moodSound(net.minecraft.sound.BiomeMoodSound.CAVE)
                        .build())
                .spawnSettings(spawns.build())
                .generationSettings(GenerationSettings.INSTANCE)
                .build();
    }

    /** River biome with crystal-clear blue water. Cooler, lots of aquatic life. */
    private static Biome crystalRiver() {
        SpawnSettings.Builder spawns = new SpawnSettings.Builder();
        spawns.spawn(net.minecraft.entity.SpawnGroup.WATER_CREATURE, 2,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.SQUID, 2, 1));
        spawns.spawn(net.minecraft.entity.SpawnGroup.WATER_AMBIENT, 5,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.SALMON, 5, 1));
        spawns.spawn(net.minecraft.entity.SpawnGroup.MONSTER, 100,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.DROWNED, 100, 1));

        return new Biome.Builder()
                .precipitation(true)
                .temperature(0.7f)
                .downfall(0.8f)
                .effects(new BiomeEffects.Builder()
                        .skyColor(8103167)
                        .fogColor(12638463)
                        .waterColor(4020182)
                        .waterFogColor(329011)
                        .grassColor(4963298)
                        .foliageColor(4963298)
                        .moodSound(net.minecraft.sound.BiomeMoodSound.CAVE)
                        .build())
                .spawnSettings(spawns.build())
                .generationSettings(GenerationSettings.INSTANCE)
                .build();
    }

    /** Dense, boggy wetland. Dark water, lots of mushrooms and slimes. */
    private static Biome mossyWetlands() {
        SpawnSettings.Builder spawns = new SpawnSettings.Builder();
        spawns.spawn(net.minecraft.entity.SpawnGroup.MONSTER, 100,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.SLIME, 100, 4));
        spawns.spawn(net.minecraft.entity.SpawnGroup.MONSTER, 15,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.WITCH,  15, 1));
        spawns.spawn(net.minecraft.entity.SpawnGroup.CREATURE, 10,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.FROG,   10, 2));

        return new Biome.Builder()
                .precipitation(true)
                .temperature(0.9f)
                .downfall(1.0f)
                .effects(new BiomeEffects.Builder()
                        .skyColor(7842047)
                        .fogColor(12638463)
                        .waterColor(3832892)
                        .waterFogColor(329011)
                        .grassColor(4159988)
                        .foliageColor(4159988)
                        .moodSound(net.minecraft.sound.BiomeMoodSound.CAVE)
                        .build())
                .spawnSettings(spawns.build())
                .generationSettings(GenerationSettings.INSTANCE)
                .build();
    }

    /** The vertical cliff walls between valley and plateau. Mossy, with cave spiders. */
    private static Biome hangingCliffs() {
        SpawnSettings.Builder spawns = new SpawnSettings.Builder();
        spawns.spawn(net.minecraft.entity.SpawnGroup.MONSTER, 25,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.CAVE_SPIDER, 25, 4));
        addBasicMonsters(spawns);

        return new Biome.Builder()
                .precipitation(true)
                .temperature(0.4f)
                .downfall(0.5f)
                .effects(new BiomeEffects.Builder()
                        .skyColor(8103167)
                        .fogColor(12638463)
                        .waterColor(4159204)
                        .waterFogColor(329011)
                        .grassColor(6316128)
                        .foliageColor(6316128)
                        .moodSound(net.minecraft.sound.BiomeMoodSound.CAVE)
                        .build())
                .spawnSettings(spawns.build())
                .generationSettings(GenerationSettings.INSTANCE)
                .build();
    }

    /** Hot, volcanic plateau. Magma blocks, blaze-like mobs, intense heat. */
    private static Biome scorchedPlateau() {
        SpawnSettings.Builder spawns = new SpawnSettings.Builder();
        spawns.spawn(net.minecraft.entity.SpawnGroup.MONSTER, 100,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.MAGMA_CUBE, 100, 2));
        spawns.spawn(net.minecraft.entity.SpawnGroup.MONSTER, 10,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.BLAZE, 10, 2));

        return new Biome.Builder()
                .precipitation(false)
                .temperature(2.0f)
                .downfall(0.0f)
                .effects(new BiomeEffects.Builder()
                        .skyColor(7254527)
                        .fogColor(3344392)
                        .waterColor(4341314)
                        .waterFogColor(4341314)
                        .grassColor(4674925)
                        .foliageColor(4674925)
                        .moodSound(net.minecraft.sound.BiomeMoodSound.CAVE)
                        .build())
                .spawnSettings(spawns.build())
                .generationSettings(GenerationSettings.INSTANCE)
                .build();
    }

    /** Frozen plateau peaks. Snow, packed ice, strays. */
    private static Biome frozenPeaks() {
        SpawnSettings.Builder spawns = new SpawnSettings.Builder();
        spawns.spawn(net.minecraft.entity.SpawnGroup.MONSTER, 160,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.STRAY,   160, 4));
        spawns.spawn(net.minecraft.entity.SpawnGroup.MONSTER, 100,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.SKELETON, 100, 4));
        spawns.spawn(net.minecraft.entity.SpawnGroup.CREATURE, 3,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.GOAT, 5, 1));

        return new Biome.Builder()
                .precipitation(true)
                .temperature(-0.7f)
                .downfall(0.9f)
                .effects(new BiomeEffects.Builder()
                        .skyColor(8756735)
                        .fogColor(12638463)
                        .waterColor(4020182)
                        .waterFogColor(329011)
                        .grassColor(8947848)
                        .foliageColor(8947848)
                        .moodSound(net.minecraft.sound.BiomeMoodSound.CAVE)
                        .build())
                .spawnSettings(spawns.build())
                .generationSettings(GenerationSettings.INSTANCE)
                .build();
    }

    /** Red-sand badlands on plateau top. Dry, harsh, gold-rich. */
    private static Biome plateauBadlands() {
        SpawnSettings.Builder spawns = new SpawnSettings.Builder();
        spawns.spawn(net.minecraft.entity.SpawnGroup.MONSTER, 80,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.HUSK, 80, 4));
        addBasicMonsters(spawns);

        return new Biome.Builder()
                .precipitation(false)
                .temperature(2.0f)
                .downfall(0.0f)
                .effects(new BiomeEffects.Builder()
                        .skyColor(7254527)
                        .fogColor(12638463)
                        .waterColor(4341314)
                        .waterFogColor(4341314)
                        .grassColor(9470285)
                        .foliageColor(9470285)
                        .moodSound(net.minecraft.sound.BiomeMoodSound.CAVE)
                        .build())
                .spawnSettings(spawns.build())
                .generationSettings(GenerationSettings.INSTANCE)
                .build();
    }

    // ─── Helper ───────────────────────────────────────────────────────────────
    private static void addBasicMonsters(SpawnSettings.Builder spawns) {
        spawns.spawn(net.minecraft.entity.SpawnGroup.MONSTER, 95,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.ZOMBIE,   95, 4));
        spawns.spawn(net.minecraft.entity.SpawnGroup.MONSTER, 100,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.SKELETON, 100, 4));
        spawns.spawn(net.minecraft.entity.SpawnGroup.MONSTER, 100,
                new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.CREEPER,  100, 4));
    }
}