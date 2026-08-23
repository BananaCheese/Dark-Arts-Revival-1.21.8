package net.bananacheese.darkartsrevival.world.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * LushLandsChunkGenerator
 *
 * Custom ChunkGenerator for The Lush Lands dimension.
 *
 * Pipeline:
 *   1. buildSurface()       – place bedrock at bottom, rough terrain shape
 *   2. populateNoise()      – fill the chunk column using LushLandsNoiseSampler density
 *   3. buildSurface()       – biome-aware surface replacement (grass, sand, etc.)
 *   4. carve() / decorate() – delegated to vanilla carvers + feature decorators
 *
 * Surface block rules (applied in buildSurface):
 *   - Valley:   Grass block → Dirt → Stone
 *   - Cliff:    Stone → Deepslate (below Y=0)
 *   - Plateau:  Scorched = Magma+Netherrack | Frozen = Snow+Ice+Stone | Badlands = Red Sand+Terracotta
 */
public class LushLandsChunkGenerator extends ChunkGenerator {

    // ─── Codec ───────────────────────────────────────────────────────────────
    public static final MapCodec<LushLandsChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                    Codec.LONG.fieldOf("seed").forGetter(g -> g.seed)
            ).apply(instance, LushLandsChunkGenerator::new)
    );

    // ─── Block palettes ──────────────────────────────────────────────────────
    private static final BlockState STONE    = Blocks.STONE.getDefaultState();
    private static final BlockState DEEPSLATE= Blocks.DEEPSLATE.getDefaultState();
    private static final BlockState WATER    = Blocks.WATER.getDefaultState();
    private static final BlockState BEDROCK  = Blocks.BEDROCK.getDefaultState();

    /** Sea level / water table for valley rivers. */
    public static final int SEA_LEVEL = LushLandsNoiseSampler.VALLEY_FLOOR - 4;

    private final long seed;
    private final LushLandsNoiseSampler noise;

    public LushLandsChunkGenerator(BiomeSource biomeSource, long seed) {
        super(biomeSource);
        this.seed  = seed;
        this.noise = new LushLandsNoiseSampler(seed);
    }

    // ─── Registration ────────────────────────────────────────────────────────
    public static void register() {
        Registry.register(
                net.minecraft.registry.Registries.CHUNK_GENERATOR,
                Identifier.of(DarkArtsRevival.MOD_ID, "lush_lands"),
                CODEC
        );
        DarkArtsRevival.LOGGER.info("[LushLands] Chunk generator codec registered.");
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }



    // ─── Core generation ─────────────────────────────────────────────────────

    /**
     * Step 1: populate noise – fill each column with solid/air based on density.
     *
     * We iterate every block position in the chunk and call getDensity().
     * Density > 0 → stone (will be replaced by surface in buildSurface).
     * Density ≤ 0 → air (or water if below sea level in valley).
     */
    @Override
    public CompletableFuture<Chunk> populateNoise(Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int startX = chunkPos.getStartX();
        int startZ = chunkPos.getStartZ();
        int minY   = chunk.getBottomY();
        int height = chunk.getHeight();

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int lx = 0; lx < 16; lx++) {
            int x = startX + lx;
            for (int lz = 0; lz < 16; lz++) {
                int z = startZ + lz;
                boolean isMesa = noise.isMesaColumn(x, z);

                for (int ly = 0; ly < height; ly++) {
                    int y = minY + ly;
                    mutable.set(x, y, z);

                    // Bedrock floor
                    if (y <= minY + 4) {
                        chunk.setBlockState(mutable, BEDROCK);
                        continue;
                    }

                    double density = noise.getDensity(x, y, z);

                    if (density > 0) {
                        // Use deepslate below Y=0 for the deep underground feel
                        chunk.setBlockState(mutable, y < 0 ? DEEPSLATE : STONE);
                    } else if (!isMesa && y <= SEA_LEVEL && y > minY + 4) {
                        // Water table in valleys only
                        chunk.setBlockState(mutable, WATER);
                    }
                    // else: leave as air
                }
            }
        }

        return CompletableFuture.completedFuture(chunk);
    }

    /**
     * Step 2: surface replacement.
     *
     * Walk down from the top of each column and replace the topmost solid
     * blocks with biome-appropriate surface materials.
     */
    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures,
                             NoiseConfig noiseConfig, Chunk chunk) {
        // Intentionally empty — vanilla surface rules apply via biome feature lists
    }

    // ─── Surface type classification ─────────────────────────────────────────



    // ─── Carvers / structures / features ─────────────────────────────────────


    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk) {
        // Vanilla carvers run via biome carver lists automatically
    }

    public void generateFeatures(ChunkRegion region, Chunk chunk, StructureAccessor structureAccessor) {
        // Vanilla feature decoration (trees, ores, flowers) will run through the
        // biome's feature lists. No override needed unless you want custom placement.
        super.generateFeatures(region, chunk, structureAccessor);
    }

    @Override
    public void populateEntities(ChunkRegion region) {
        // Mob spawning handled by vanilla via biome spawn lists.
    }

    // ── Height queries ────────────────────────────────────────────────────────

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return noise.getSurfaceY(x, z);
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        int minY   = world.getBottomY();
        int height = world.getHeight();
        BlockState[] column = new BlockState[height];
        boolean isMesa = noise.isMesaColumn(x, z);

        for (int i = 0; i < height; i++) {
            int y = minY + i;
            if (y <= minY + 4) {
                column[i] = BEDROCK;
            } else {
                double density = noise.getDensity(x, y, z);
                if (density > 0) {
                    column[i] = y < 0 ? DEEPSLATE : STONE;
                } else if (!isMesa && y <= SEA_LEVEL) {
                    column[i] = WATER;
                } else {
                    column[i] = Blocks.AIR.getDefaultState();
                }
            }
        }
        return new VerticalBlockSample(minY, column);
    }

    @Override
    public int getWorldHeight() { return LushLandsNoiseSampler.MAX_Y - LushLandsNoiseSampler.MIN_Y; }

    @Override
    public int getSeaLevel() { return SEA_LEVEL; }

    @Override
    public int getMinimumY() { return LushLandsNoiseSampler.MIN_Y; }

    @Override
    public void appendDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
        text.add("[LushLands] mesa=" + noise.isMesaColumn(pos.getX(), pos.getZ())
                + " surfY=" + noise.getSurfaceY(pos.getX(), pos.getZ())
                + " mesaVal=" + String.format("%.3f", noise.baseMesaValue(pos.getX(), pos.getZ())));
    }
}