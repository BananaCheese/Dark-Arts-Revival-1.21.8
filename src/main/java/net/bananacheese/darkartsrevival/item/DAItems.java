package net.bananacheese.darkartsrevival.item;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.bananacheese.darkartsrevival.item.custom.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class DAItems {

    public static final Item SOUL_SYRINGE = registerItem("soul_syringe",
            new SoulSyringe(new Item.Settings()
                    .maxCount(1)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "soul_syringe")))));

    public static final Item ALTER_ANALYZER = registerItem("alter_analyzer",
            new AlterAnalyzer(new Item.Settings()
                    .maxCount(1)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "alter_analyzer")))));

    public static final Item GEAR_FORGE_HAMMER = registerItem("gear_forge_hammer",
            new GearForgeHammer(new Item.Settings()
                    .maxCount(1)
                    .maxDamage(250)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "gear_forge_hammer")))));

    public static final Item DARK_CRYSTAL = registerItem("dark_crystal",
            new Item(new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "dark_crystal")))));

    // Armor Frames - Now wearable!
    public static final Item HELMET_FRAME = registerItem("helmet_frame",
            new ArmorFrameItem(new Item.Settings()
                    .maxCount(1)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "helmet_frame"))),
                    ArmorFrameItem.ArmorFrameType.HELMET));

    public static final Item CHESTPLATE_FRAME = registerItem("chestplate_frame",
            new ArmorFrameItem(new Item.Settings()
                    .maxCount(1)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "chestplate_frame"))),
                    ArmorFrameItem.ArmorFrameType.CHESTPLATE));

    public static final Item LEGGINGS_FRAME = registerItem("leggings_frame",
            new ArmorFrameItem(new Item.Settings()
                    .maxCount(1)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "leggings_frame"))),
                    ArmorFrameItem.ArmorFrameType.LEGGINGS));

    public static final Item BOOTS_FRAME = registerItem("boots_frame",
            new ArmorFrameItem(new Item.Settings()
                    .maxCount(1)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "boots_frame"))),
                    ArmorFrameItem.ArmorFrameType.BOOTS));

    // Armor Components with Groups
    // Format: (settings, type, group, defense, durability, toughness)

    // PAULDRONS group - only one type of pauldrons allowed
    public static final Item IRON_PAULDRONS = registerItem("iron_pauldrons",
            new ArmorComponentItem(new Item.Settings()
                    .maxCount(16)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "iron_pauldrons"))),
                    ArmorComponentItem.ComponentType.PAULDRONS, "pauldrons", 2, 50, 0.5));

    // GORGET group - only one type of gorget allowed
    public static final Item IRON_GORGET = registerItem("iron_gorget",
            new ArmorComponentItem(new Item.Settings()
                    .maxCount(16)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "iron_gorget"))),
                    ArmorComponentItem.ComponentType.GORGET, "gorget", 1, 30, 0.3));

    // FAULD group - only one type of fauld allowed
    public static final Item IRON_FAULD = registerItem("iron_fauld",
            new ArmorComponentItem(new Item.Settings()
                    .maxCount(16)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "iron_fauld"))),
                    ArmorComponentItem.ComponentType.FAULD, "fauld", 2, 40, 0.4));

    // REINFORCEMENT group - only one reinforcement allowed
    public static final Item STEEL_REINFORCEMENT = registerItem("steel_reinforcement",
            new ArmorComponentItem(new Item.Settings()
                    .maxCount(16)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "steel_reinforcement"))),
                    ArmorComponentItem.ComponentType.REINFORCEMENT, "reinforcement", 1, 100, 1.0));

    // Example: When you add diamond pauldrons later, they'll use the same "pauldrons" group
    // public static final Item DIAMOND_PAULDRONS = registerItem("diamond_pauldrons",
    //         new ArmorComponentItem(new Item.Settings()...
    //                 ArmorComponentItem.ComponentType.PAULDRONS, "pauldrons", 4, 100, 1.5));
    // This way you can't have both iron and diamond pauldrons on the same frame!

    private static <T extends Item> T registerItem(String name, T item) {
        return Registry.register(Registries.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, name), item);
    }

    public static void registerModItems() {
        DarkArtsRevival.LOGGER.info("Registering Mod Items for " + DarkArtsRevival.MOD_ID);
    }
}