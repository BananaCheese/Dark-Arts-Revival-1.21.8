package net.bananacheese.darkartsrevival.datagen;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.bananacheese.darkartsrevival.item.DAItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class DAModelProvider extends FabricModelProvider {
    public DAModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        // Generate connected texture blockstates
        generateConnectedTextureBlock("dark_barrier",
                "block/dark_barrier_inactive",
                "block/dark_barrier_active");
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        // Regular items
        itemModelGenerator.register(DAItems.ALTER_ANALYZER, Models.HANDHELD);
        itemModelGenerator.register(DAItems.DARK_CRYSTAL, Models.HANDHELD);
        itemModelGenerator.register(DAItems.GEAR_FORGE_HAMMER, Models.HANDHELD);

        // Armor frames - with custom texture paths
        //itemModelGenerator.register(DAItems.HELMET_FRAME);
        //itemModelGenerator.register(DAItems.CHESTPLATE_FRAME);
        //itemModelGenerator.register(DAItems.LEGGINGS_FRAME);
        //itemModelGenerator.register(DAItems.BOOTS_FRAME);

        // Armor components - with custom texture paths
        registerArmorComponent(itemModelGenerator, DAItems.IRON_PAULDRONS, "iron_pauldrons");
        registerArmorComponent(itemModelGenerator, DAItems.IRON_FAULD, "iron_fauld");
        registerArmorComponent(itemModelGenerator, DAItems.IRON_GORGET, "iron_gorget");
        registerArmorComponent(itemModelGenerator, DAItems.STEEL_REINFORCEMENT, "steel_reinforcement");
    }

    /**
     * Registers an armor component item with texture path pointing to armor/components/item/
     */
    private void registerArmorComponent(ItemModelGenerator generator, net.minecraft.item.Item item, String name) {
        Identifier textureId = Identifier.of(DarkArtsRevival.MOD_ID, "item/armor/components/item/" + name);

        // Upload the model using Models.GENERATED with custom texture path
        Identifier modelId = Models.GENERATED.upload(
                item,
                TextureMap.layer0(textureId),
                generator.modelCollector
        );

        // Register the item with the generated model
        generator.output.accept(item, ItemModels.basic(modelId));
    }

    private void generateConnectedTextureBlock(String blockName, String baseTexture, String activeTexture) {
        // We'll manually write the JSON files here
        // DataGen doesn't have a built-in way to handle multipart with many conditions
    }

    /**
     * Generates a complete connected texture blockstate JSON
     */
    public static String generateConnectedBlockstate(String modId, String blockName,
                                                     String baseTexture,
                                                     String activeTexture,
                                                     String connectedTexture) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"multipart\": [\n");

        // Base model - always rendered
        json.append("    {\n");
        json.append("      \"apply\": { \"model\": \"").append(modId).append(":block/").append(blockName).append("_base\" }\n");
        json.append("    }");

        // For each direction, add connection overlays
        for (Direction direction : Direction.values()) {
            String dirName = direction.name();
            String connectedProp = "connected_" + dirName;

            json.append(",\n");

            // Connected overlay
            json.append("    {\n");
            json.append("      \"when\": { \"").append(connectedProp).append("\": \"true\" },\n");
            json.append("      \"apply\": { \"model\": \"").append(modId).append(":block/").append(blockName)
                    .append("_overlay_").append(dirName).append("_connected\" }\n");
            json.append("    }");
        }

        json.append("\n  ]\n");
        json.append("}\n");

        return json.toString();
    }

    /**
     * Generates overlay model JSON for a specific face
     */
    public static String generateOverlayModel(String modId, String texturePath, Direction face) {
        String faceKey = face.name();

        return String.format("""
            {
              "parent": "block/block",
              "textures": {
                "%s": "%s:%s"
              },
              "elements": [
                {
                  "from": [0, 0, 0],
                  "to": [16, 16, 16],
                  "faces": {
                    "%s": {"uv": [0, 0, 16, 16], "texture": "#%s", "cullface": "%s"}
                  }
                }
              ]
            }
            """, faceKey, modId, texturePath, faceKey, faceKey, faceKey);
    }

    /**
     * Generates base model JSON (full cube with one texture)
     */
    public static String generateBaseModel(String modId, String texturePath) {
        return String.format("""
            {
              "parent": "block/cube_all",
              "textures": {
                "all": "%s:%s"
              }
            }
            """, modId, texturePath);
    }
}