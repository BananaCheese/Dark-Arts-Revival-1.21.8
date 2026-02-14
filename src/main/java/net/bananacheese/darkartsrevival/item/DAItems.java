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

    public static final Item HELMET_FRAME = registerItem("helmet_frame",
            new ArmorFrameItem(new Item.Settings()
                    .maxCount(1)
                    .maxDamage(100)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "helmet_frame"))),
                    ArmorFrameItem.ArmorFrameType.HELMET));

    public static final Item CHESTPLATE_FRAME = registerItem("chestplate_frame",
            new ArmorFrameItem(new Item.Settings()
                    .maxCount(1)
                    .maxDamage(100)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "chestplate_frame"))),
                    ArmorFrameItem.ArmorFrameType.CHESTPLATE));

    public static final Item LEGGINGS_FRAME = registerItem("leggings_frame",
            new ArmorFrameItem(new Item.Settings()
                    .maxCount(1)
                    .maxDamage(100)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "leggings_frame"))),
                    ArmorFrameItem.ArmorFrameType.LEGGINGS));

    public static final Item BOOTS_FRAME = registerItem("boots_frame",
            new ArmorFrameItem(new Item.Settings()
                    .maxCount(1)
                    .maxDamage(100)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "boots_frame"))),
                    ArmorFrameItem.ArmorFrameType.BOOTS));

    // Armor Components - Examples
    public static final Item IRON_PAULDRONS = registerItem("iron_pauldrons",
            new ArmorComponentItem(new Item.Settings()
                    .maxCount(16)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "iron_pauldrons"))),
                    ArmorComponentItem.ComponentType.PAULDRONS, 2, 50));

    public static final Item IRON_GORGET = registerItem("iron_gorget",
            new ArmorComponentItem(new Item.Settings()
                    .maxCount(16)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "iron_gorget"))),
                    ArmorComponentItem.ComponentType.GORGET, 1, 30));

    public static final Item IRON_FAULD = registerItem("iron_fauld",
            new ArmorComponentItem(new Item.Settings()
                    .maxCount(16)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "iron_fauld"))),
                    ArmorComponentItem.ComponentType.FAULD, 2, 40));

    public static final Item STEEL_REINFORCEMENT = registerItem("steel_reinforcement",
            new ArmorComponentItem(new Item.Settings()
                    .maxCount(16)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, "steel_reinforcement"))),
                    ArmorComponentItem.ComponentType.REINFORCEMENT, 1, 100));

    private static <T extends Item> T registerItem(String name, T item) {
        return Registry.register(Registries.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, name), item);
    }

    public static void registerModItems() {
        DarkArtsRevival.LOGGER.info("Registering Mod Items for " + DarkArtsRevival.MOD_ID);
    }
}
