package net.bananacheese.darkartsrevival.client.renderer;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.bananacheese.darkartsrevival.item.custom.ArmorFrameItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages dynamic texture generation for modular armor/tools
 * Composites base textures with component overlays at runtime
 */
public class DynamicTextureManager {
    private static final Map<String, Identifier> TEXTURE_CACHE = new HashMap<>();
    private static final Map<String, NativeImageBackedTexture> TEXTURE_OBJECTS = new HashMap<>();

    /**
     * Gets or generates a composite texture for an armor frame
     * @param stack The armor frame ItemStack
     * @return Identifier of the composite texture
     */
    public static Identifier getOrCreateArmorTexture(ItemStack stack) {
        DarkArtsRevival.LOGGER.info("DEBUG: getOrCreateArmorTexture called!");

        if (!(stack.getItem() instanceof ArmorFrameItem frameItem)) {
            return getFallbackTexture(stack);
        }

        // Build cache key from frame type and components
        String cacheKey = buildCacheKey(stack);

        // Return cached texture if exists
        if (TEXTURE_CACHE.containsKey(cacheKey)) {
            return TEXTURE_CACHE.get(cacheKey);
        }

        // Generate new composite texture
        Identifier compositeId = generateCompositeTexture(stack, frameItem, cacheKey);
        TEXTURE_CACHE.put(cacheKey, compositeId);

        return compositeId;
    }

    /**
     * Builds a unique cache key from the item's configuration
     */
    private static String buildCacheKey(ItemStack stack) {
        ArmorFrameItem frameItem = (ArmorFrameItem) stack.getItem();
        StringBuilder key = new StringBuilder(frameItem.getFrameType().name().toLowerCase());

        List<ArmorFrameItem.ComponentData> components = ArmorFrameItem.getComponents(stack);

        // Sort components by ID for consistent cache keys
        components.stream()
                .sorted((a, b) -> a.id().compareTo(b.id()))
                .forEach(comp -> {
                    String itemName = comp.id().substring(comp.id().lastIndexOf(':') + 1);
                    key.append("_").append(itemName);
                });

        return key.toString();
    }

    /**
     * Generates a new composite texture by overlaying component textures on base
     */
    private static Identifier generateCompositeTexture(ItemStack stack, ArmorFrameItem frameItem, String cacheKey) {
        MinecraftClient client = MinecraftClient.getInstance();

        try {
            // Load base frame texture
            String frameName = frameItem.getFrameType().name().toLowerCase();
            Identifier baseTextureId = Identifier.of(DarkArtsRevival.MOD_ID,
                    "textures/item/armor/frames/" + frameName + "_frame.png");

            NativeImage baseImage = loadTexture(baseTextureId);
            if (baseImage == null) {
                DarkArtsRevival.LOGGER.warn("Failed to load base texture: " + baseTextureId);
                return getFallbackTexture(stack);
            }

            // Get components and overlay them
            List<ArmorFrameItem.ComponentData> components = ArmorFrameItem.getComponents(stack);
            for (ArmorFrameItem.ComponentData comp : components) {
                String componentTexturePath = getComponentTexturePath(comp.id());
                Identifier compTextureId = Identifier.of(DarkArtsRevival.MOD_ID,
                        "textures/item/armor/components/item/" + componentTexturePath + ".png");

                NativeImage compImage = loadTexture(compTextureId);
                if (compImage != null) {
                    overlayImage(baseImage, compImage);
                    compImage.close();
                } else {
                    DarkArtsRevival.LOGGER.warn("Failed to load component texture: " + compTextureId);
                }
            }

            // Register composite texture with texture manager
            Identifier compositeId = Identifier.of(DarkArtsRevival.MOD_ID,
                    "dynamic/armor_frames/" + cacheKey);

            // FIXED: Use the constructor that takes (String, int, int, boolean)
            // Create a new texture with the dimensions, then upload our image
            int width = baseImage.getWidth();
            int height = baseImage.getHeight();

            NativeImageBackedTexture texture = new NativeImageBackedTexture(
                    cacheKey,  // name/identifier string
                    width,     // width
                    height,    // height
                    false      // mipmap
            );

            // Get the texture's internal image and copy our composite to it
            NativeImage textureImage = texture.getImage();
            if (textureImage != null) {
                // Copy pixel by pixel
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        textureImage.setColorArgb(x, y, baseImage.getColorArgb(x, y));
                    }
                }
            }

            // Upload the texture to GPU
            texture.upload();

            // Register with texture manager
            client.getTextureManager().registerTexture(compositeId, texture);
            TEXTURE_OBJECTS.put(cacheKey, texture);

            // Clean up our base image
            baseImage.close();

            DarkArtsRevival.LOGGER.info("Generated dynamic texture: " + compositeId);
            return compositeId;

        } catch (Exception e) {
            DarkArtsRevival.LOGGER.error("Failed to generate composite texture for " + cacheKey, e);
            return getFallbackTexture(stack);
        }
    }

    /**
     * Loads a texture as NativeImage from resource manager
     */
    private static NativeImage loadTexture(Identifier id) {
        MinecraftClient client = MinecraftClient.getInstance();
        try {
            InputStream stream = client.getResourceManager()
                    .getResource(id)
                    .orElseThrow()
                    .getInputStream();
            return NativeImage.read(stream);
        } catch (Exception e) {
            DarkArtsRevival.LOGGER.debug("Could not load texture: " + id);
            return null;
        }
    }

    /**
     * Overlays one image on top of another with alpha blending
     */
    private static void overlayImage(NativeImage base, NativeImage overlay) {
        int width = Math.min(base.getWidth(), overlay.getWidth());
        int height = Math.min(base.getHeight(), overlay.getHeight());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Use getColorArgb - returns color in ARGB format
                int overlayPixel = overlay.getColorArgb(x, y);
                int overlayAlpha = (overlayPixel >> 24) & 0xFF;

                // Only process non-transparent pixels
                if (overlayAlpha > 0) {
                    int basePixel = base.getColorArgb(x, y);
                    int blended = blendPixels(basePixel, overlayPixel, overlayAlpha);
                    // Use setColorArgb to set the blended pixel
                    base.setColorArgb(x, y, blended);
                }
            }
        }
    }

    /**
     * Blends two pixels using alpha compositing
     * Colors are in ARGB format
     */
    private static int blendPixels(int base, int overlay, int overlayAlpha) {
        if (overlayAlpha == 255) {
            return overlay; // Fully opaque - just use overlay
        }

        float alpha = overlayAlpha / 255f;

        // Extract ARGB components
        int baseA = (base >> 24) & 0xFF;
        int baseR = (base >> 16) & 0xFF;
        int baseG = (base >> 8) & 0xFF;
        int baseB = base & 0xFF;

        int overlayR = (overlay >> 16) & 0xFF;
        int overlayG = (overlay >> 8) & 0xFF;
        int overlayB = overlay & 0xFF;

        // Alpha blending formula: result = overlay * alpha + base * (1 - alpha)
        int r = (int) (overlayR * alpha + baseR * (1 - alpha));
        int g = (int) (overlayG * alpha + baseG * (1 - alpha));
        int b = (int) (overlayB * alpha + baseB * (1 - alpha));
        int a = Math.max(baseA, overlayAlpha);

        // Combine back to ARGB
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Converts component ID to texture path
     * Example: "darkartsrevival:iron_pauldrons" -> "iron_pauldrons"
     */
    private static String getComponentTexturePath(String componentId) {
        return componentId.substring(componentId.lastIndexOf(':') + 1);
    }

    /**
     * Gets fallback texture if dynamic generation fails
     */
    private static Identifier getFallbackTexture(ItemStack stack) {
        if (stack.getItem() instanceof ArmorFrameItem frameItem) {
            String frameName = frameItem.getFrameType().name().toLowerCase();
            return Identifier.of(DarkArtsRevival.MOD_ID,
                    "item/" + frameName + "_frame");
        }
        return Identifier.of("minecraft", "item/barrier");
    }

    /**
     * Clears the texture cache (call when reloading resources)
     */
    public static void clearCache() {
        // Clean up texture objects
        for (NativeImageBackedTexture texture : TEXTURE_OBJECTS.values()) {
            texture.close();
        }
        TEXTURE_OBJECTS.clear();
        TEXTURE_CACHE.clear();
        DarkArtsRevival.LOGGER.info("Cleared dynamic texture cache");
    }
}