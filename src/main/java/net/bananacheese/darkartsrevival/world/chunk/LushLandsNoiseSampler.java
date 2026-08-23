package net.bananacheese.darkartsrevival.world.chunk;

import net.minecraft.util.math.MathHelper;

/**
 * LushLandsNoiseSampler
 *
 * Produces the distinctive "extruded mesa with overhangs" terrain shape
 * for The Lush Lands dimension.
 *
 * ────────────────────────────────────────────────────────
 * TERRAIN CONCEPT
 * ────────────────────────────────────────────────────────
 *
 *        ╔══════════╗  ← plateau top  (~200-280 Y)
 *        ║ OVERHANG ║    edges bulge outward 10-24 blocks
 *       ╔╝ (cliff)  ╚╗
 *       ║   shaft    ║  ← vertical shaft / column (130-200 Y)
 *       ║            ║
 *  ─────╨────────────╨─────  ← valley floor (~60-120 Y)
 *   VALLEY  (lush, rivers)
 *
 * The extrusions are large (80-200 blocks wide) and separated by valleys
 * (~60-120 blocks wide).  Overhangs extend 10-24 blocks beyond the shaft
 * edge and taper back in as you go higher.
 *
 * ────────────────────────────────────────────────────────
 * NOISE LAYERS
 * ────────────────────────────────────────────────────────
 *
 *  baseNoise        – large-scale (512 blocks) Perlin for valley/mesa decision
 *  detailNoise      – medium-scale (128 blocks) Perlin adds cliff roughness
 *  overhangNoise    – separate noise for overhang magnitude variation
 *  surfaceNoise     – small-scale (32 blocks) for valley floor detail
 *  caveNoise        – 3D noise for cave carving inside shafts
 */
public class LushLandsNoiseSampler {

    // ── World height constants ────────────────────────────────────────────────
    public static final int MIN_Y           = -64;
    public static final int MAX_Y           = 448;
    public static final int VALLEY_FLOOR    = 72;   // where valley terrain tops out
    public static final int SHAFT_BASE      = 130;  // bottom of the clean vertical shaft
    public static final int OVERHANG_START  = 200;  // where the undercut begins
    public static final int PLATEAU_SURFACE = 240;  // top of the overhang / plateau cap
    public static final int PLATEAU_MAX     = 340;  // max mountain height on plateau

    // ── Noise scales ─────────────────────────────────────────────────────────
    // Large scale: controls how wide the mesa footprints are (~500 block features)
    private static final double BASE_SCALE      = 1.0 / 500.0;
    // Detail scale: roughens the edges of the shaft walls
    private static final double DETAIL_SCALE    = 1.0 / 80.0;
    // Overhang variation: each column gets a slightly different overhang size
    private static final double OVERHANG_SCALE  = 1.0 / 200.0;
    // Valley surface bumps: small hills in the valley floor
    private static final double SURFACE_SCALE   = 1.0 / 60.0;
    // Plateau mountains
    private static final double MOUNTAIN_SCALE  = 1.0 / 90.0;
    // Cave noise
    private static final double CAVE_SCALE_H    = 1.0 / 35.0;
    private static final double CAVE_SCALE_V    = 1.0 / 18.0;
    // Biome variety: large scale so biomes are ~400-800 blocks wide
    private static final double BIOME_SCALE     = 1.0 / 800.0;

    // ── Mesa geometry ─────────────────────────────────────────────────────────
    /** baseNoise > this threshold = inside a mesa column */
    private static final double MESA_THRESHOLD  = 0.10;
    /** Blend zone width on either side of the threshold */
    private static final double BLEND_WIDTH     = 0.12;
    /** Overhang extends this many blocks beyond the shaft radius at its peak */
    private static final double MAX_OVERHANG    = 20.0;
    private static final double MIN_OVERHANG    = 8.0;
    /** Shaft half-width in blocks. Mesa columns are ~2x this wide. */
    private static final double BASE_SHAFT_RADIUS = 55.0;
    /** Extra radius added for stronger mesa values (wider mesas) */
    private static final double RADIUS_VARIANCE   = 40.0;

    // ── Noise generators ─────────────────────────────────────────────────────
    private final SimplexOctaveNoise baseNoise;
    private final SimplexOctaveNoise detailNoise;
    private final SimplexOctaveNoise overhangNoise;
    private final SimplexOctaveNoise surfaceNoise;
    private final SimplexOctaveNoise mountainNoise;
    private final SimplexOctaveNoise caveNoise;
    private final SimplexOctaveNoise biomeNoise;

    public LushLandsNoiseSampler(long seed) {
        this.baseNoise     = new SimplexOctaveNoise(seed,       5, 0.50);
        this.detailNoise   = new SimplexOctaveNoise(seed + 1,   4, 0.55);
        this.overhangNoise = new SimplexOctaveNoise(seed + 2,   3, 0.50);
        this.surfaceNoise  = new SimplexOctaveNoise(seed + 3,   4, 0.60);
        this.mountainNoise = new SimplexOctaveNoise(seed + 4,   5, 0.50);
        this.caveNoise     = new SimplexOctaveNoise(seed + 5,   3, 0.50);
        this.biomeNoise    = new SimplexOctaveNoise(seed + 6,   3, 0.50);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Density at (x, y, z). Positive = solid, negative = air.
     */
    public double getDensity(int x, int y, int z) {
        double mesaValue = baseMesaValue(x, z);

        // Per-column overhang magnitude
        double oh = MathHelper.lerp(
                MathHelper.clamp((overhangNoise.sample(x * OVERHANG_SCALE, 0, z * OVERHANG_SCALE) + 1) * 0.5, 0, 1),
                MIN_OVERHANG, MAX_OVERHANG
        );

        if (mesaValue > MESA_THRESHOLD) {
            return mesaDensity(x, y, z, mesaValue, oh);
        } else if (mesaValue > MESA_THRESHOLD - BLEND_WIDTH) {
            // Blend zone: smoothly interpolate between valley and mesa
            double t = (mesaValue - (MESA_THRESHOLD - BLEND_WIDTH)) / BLEND_WIDTH;
            t = t * t * (3 - 2 * t); // smoothstep
            double mesa   = mesaDensity(x, y, z, mesaValue, oh);
            double valley = valleyDensity(x, y, z);
            return MathHelper.lerp(t, valley, mesa);
        } else {
            return valleyDensity(x, y, z);
        }
    }

    /**
     * Returns the base noise value used to decide mesa vs valley for this column.
     * Exposed so the biome source can use it for height-band decisions.
     */
    public double baseMesaValue(int x, int z) {
        double raw = baseNoise.sample(x * BASE_SCALE, 0, z * BASE_SCALE);
        // Small detail added to break up perfectly circular edges
        double edge = detailNoise.sample(x * DETAIL_SCALE * 0.3, 0, z * DETAIL_SCALE * 0.3) * 0.08;
        return raw + edge;
    }

    /**
     * Smooth biome variety noise in [0, 1) varying over ~800 blocks.
     * Used by LushLandsBiomeSource to assign horizontal biome variety
     * without checkerboard artifacts.
     */
    public float getBiomeVariety(int x, int z) {
        double raw = biomeNoise.sample(x * BIOME_SCALE, 0, z * BIOME_SCALE);
        return (float) MathHelper.clamp((raw + 1.0) * 0.5, 0.0, 1.0);
    }

    // ── Mesa density ──────────────────────────────────────────────────────────

    private double mesaDensity(int x, int y, int z, double mesaStrength, double overhangMag) {
        double dy = y;

        // Shaft radius: stronger mesa = wider shaft
        double strengthFactor = MathHelper.clamp((mesaStrength - MESA_THRESHOLD) / (1.0 - MESA_THRESHOLD), 0, 1);
        double shaftRadius = BASE_SHAFT_RADIUS + strengthFactor * RADIUS_VARIANCE;

        // Real horizontal distance from the mesa center approximation.
        // We treat the gradient of baseNoise as a proxy for distance from edge.
        // edgeDist > 0 means we're inside the mesa, edgeDist < 0 means outside.
        // Scale from noise-space to block-space using the BASE_SCALE.
        double edgeDist = (mesaStrength - MESA_THRESHOLD) / BASE_SCALE;

        // ── Deep underground: always solid ──
        if (dy < MIN_Y + 8) return 10.0;

        // ── Below valley floor: solid foundation ──
        if (dy < VALLEY_FLOOR - 5) return 8.0;

        // ── Taper zone: shaft rises from valley floor ──
        // At VALLEY_FLOOR the shaft is very narrow; by SHAFT_BASE it's full width.
        if (dy < SHAFT_BASE) {
            double taper = (dy - (VALLEY_FLOOR - 5)) / (double)(SHAFT_BASE - (VALLEY_FLOOR - 5));
            taper = MathHelper.clamp(taper, 0, 1);
            taper = taper * taper; // ease-in: narrow at bottom, wider at top

            double effectiveRadius = shaftRadius * taper;

            // Valley surface height for this column (so the shaft rises from the actual ground)
            double surfY = VALLEY_FLOOR + surfaceNoise.sample(x * SURFACE_SCALE, 0, z * SURFACE_SCALE) * 15.0;

            // Below the valley surface: solid
            if (dy < surfY) return 2.0;

            // Above surface but in taper zone: solid if within effective radius
            double wallNoise = detailNoise.sample(x * DETAIL_SCALE, dy * DETAIL_SCALE * 0.4, z * DETAIL_SCALE) * 6.0;
            return edgeDist - (effectiveRadius + wallNoise);
        }

        // ── Main shaft: clean vertical walls ──
        if (dy < OVERHANG_START) {
            double wallNoise = detailNoise.sample(x * DETAIL_SCALE, dy * DETAIL_SCALE * 0.3, z * DETAIL_SCALE) * 5.0;
            return edgeDist - (shaftRadius + wallNoise);
        }

        // ── Overhang bell ──
        if (dy < PLATEAU_SURFACE) {
            double progress = (dy - OVERHANG_START) / (double)(PLATEAU_SURFACE - OVERHANG_START);
            double bell = overhangBell(progress);
            double overhangRadius = shaftRadius + overhangMag * bell;

            double roughness = detailNoise.sample(x * DETAIL_SCALE * 1.5, dy * DETAIL_SCALE * 0.5, z * DETAIL_SCALE * 1.5) * 4.0;
            return edgeDist - (overhangRadius + roughness);
        }

        // ── Plateau cap ──
        if (dy <= PLATEAU_SURFACE + 3) return 5.0;

        // ── Plateau mountains ──
        return plateauTopDensity(x, dy, z);
    }

    /**
     * Bell curve for the overhang undercut.
     * t=0: bottom of overhang zone (shaft width)
     * t~0.4: maximum extension outward
     * t=1: back to shaft width at plateau surface
     */
    private double overhangBell(double t) {
        if (t < 0.4) {
            // Ramp up
            double s = t / 0.4;
            return s * s * (3 - 2 * s); // smoothstep
        } else {
            // Ramp back down (slower fade for a dramatic undercut)
            double s = (t - 0.4) / 0.6;
            return 1.0 - s * s * (3 - 2 * s);
        }
    }

    // ── Valley density ────────────────────────────────────────────────────────

    private double valleyDensity(int x, int y, int z) {
        double dy = y;
        if (dy < MIN_Y + 8) return 10.0;

        double surfaceY = VALLEY_FLOOR + surfaceNoise.sample(x * SURFACE_SCALE, 0, z * SURFACE_SCALE) * 18.0;

        if (dy < surfaceY - 1) {
            // Cave carving
            double cave = caveNoise.sample(x * CAVE_SCALE_H, dy * CAVE_SCALE_V, z * CAVE_SCALE_H);
            if (cave > 0.70 && dy > MIN_Y + 10 && dy < surfaceY - 5) return -1.0;
            return 2.0;
        }

        if (dy <= surfaceY + 1) return surfaceY - dy + 0.5;

        return -1.0;
    }

    // ── Plateau top ───────────────────────────────────────────────────────────

    private double plateauTopDensity(int x, double dy, int z) {
        double mountainHeight = PLATEAU_SURFACE + 10
                + mountainNoise.sample(x * MOUNTAIN_SCALE, 0, z * MOUNTAIN_SCALE) * 80.0;
        if (dy < mountainHeight - 1) return 2.0;
        if (dy <= mountainHeight + 1) return mountainHeight - dy + 0.5;
        return -1.0;
    }

    // ── Height / surface queries ──────────────────────────────────────────────

    public int getSurfaceY(int x, int z) {
        double mv = baseMesaValue(x, z);
        if (mv > MESA_THRESHOLD) {
            return (int)(PLATEAU_SURFACE + 10 + mountainNoise.sample(x * MOUNTAIN_SCALE, 0, z * MOUNTAIN_SCALE) * 80.0);
        }
        return (int)(VALLEY_FLOOR + surfaceNoise.sample(x * SURFACE_SCALE, 0, z * SURFACE_SCALE) * 18.0);
    }

    public boolean isMesaColumn(int x, int z) {
        return baseMesaValue(x, z) > MESA_THRESHOLD;
    }

    // ── Inner noise class ─────────────────────────────────────────────────────

    public static final class SimplexOctaveNoise {
        private final long[] seeds;
        private final int octaves;
        private final double persistence;

        public SimplexOctaveNoise(long baseSeed, int octaves, double persistence) {
            this.octaves     = octaves;
            this.persistence = persistence;
            this.seeds       = new long[octaves];
            long s = baseSeed;
            for (int i = 0; i < octaves; i++) {
                s = s * 6364136223846793005L + 1442695040888963407L;
                seeds[i] = s;
            }
        }

        public double sample(double x, double y, double z) {
            double value = 0, amplitude = 1, frequency = 1, maxAmp = 0;
            for (int i = 0; i < octaves; i++) {
                value   += perlinValue(x * frequency, y * frequency, z * frequency, seeds[i]) * amplitude;
                maxAmp  += amplitude;
                amplitude *= persistence;
                frequency *= 2;
            }
            return value / maxAmp;
        }

        private double perlinValue(double x, double y, double z, long seed) {
            int xi = MathHelper.floor(x), yi = MathHelper.floor(y), zi = MathHelper.floor(z);
            double fx = x - xi, fy = y - yi, fz = z - zi;
            double ux = fade(fx), uy = fade(fy), uz = fade(fz);

            double v000 = grad(hash(xi,   yi,   zi,   seed), fx,   fy,   fz  );
            double v100 = grad(hash(xi+1, yi,   zi,   seed), fx-1, fy,   fz  );
            double v010 = grad(hash(xi,   yi+1, zi,   seed), fx,   fy-1, fz  );
            double v110 = grad(hash(xi+1, yi+1, zi,   seed), fx-1, fy-1, fz  );
            double v001 = grad(hash(xi,   yi,   zi+1, seed), fx,   fy,   fz-1);
            double v101 = grad(hash(xi+1, yi,   zi+1, seed), fx-1, fy,   fz-1);
            double v011 = grad(hash(xi,   yi+1, zi+1, seed), fx,   fy-1, fz-1);
            double v111 = grad(hash(xi+1, yi+1, zi+1, seed), fx-1, fy-1, fz-1);

            return MathHelper.lerp3(ux, uy, uz, v000, v100, v010, v110, v001, v101, v011, v111);
        }

        private double fade(double t) { return t * t * t * (t * (t * 6 - 15) + 10); }

        private int hash(int x, int y, int z, long seed) {
            long h = seed ^ ((long)x * 1234567891L) ^ ((long)y * 987654321L) ^ ((long)z * 111111111L);
            h ^= h >>> 33; h *= 0xff51afd7ed558ccdL; h ^= h >>> 33;
            return (int)(h & 0xF);
        }

        private double grad(int hash, double x, double y, double z) {
            int h = hash & 15;
            double u = h < 8 ? x : y;
            double v = h < 4 ? y : (h == 12 || h == 14 ? x : z);
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
        }
    }
}