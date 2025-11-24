package net.bananacheese.darkartsrevival.item;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.bananacheese.darkartsrevival.item.custom.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class DAItems {

    public static final Item SOUL_SYRINGE = registerItem("soul_syringe",
            settings -> new SoulSyringe(settings.maxCount(1)));

    public static final Item ALTER_ANALYZER = registerItem("alter_analyzer",
            settings -> new AlterAnalyzer(settings.maxCount(1)));

    public static final Item GEAR_FORGE_HAMMER = registerItem("gear_forge_hammer",
            settings -> new GearForgeHammer(settings.maxCount(1).maxDamage(250))); // Durability like IE hammer

    public static final Item DARK_CRYSTAL = registerItem("dark_crystal", Item::new);

    public static final Item HELMET_CORE = registerItem("helmet_core",
            settings -> new ArmorCoreItem(settings.maxCount(1), ArmorCoreItem.ArmorCoreType.HELMET));

    public static final Item CHESTPLATE_CORE = registerItem("chestplate_core",
            settings -> new ArmorCoreItem(settings.maxCount(1), ArmorCoreItem.ArmorCoreType.CHESTPLATE));

    public static final Item LEGGINGS_CORE = registerItem("leggings_core",
            settings -> new ArmorCoreItem(settings.maxCount(1), ArmorCoreItem.ArmorCoreType.LEGGINGS));

    public static final Item BOOTS_CORE = registerItem("boots_core",
            settings -> new ArmorCoreItem(settings.maxCount(1), ArmorCoreItem.ArmorCoreType.BOOTS));

    // Armor Components - Examples
    public static final Item IRON_PAULDRONS = registerItem("iron_pauldrons",
            settings ->  new ArmorComponentItem(settings.maxCount(1),
                    ArmorComponentItem.ComponentType.PAULDRONS, 2, 50));

    public static final Item IRON_GORGET = registerItem("iron_gorget",
            settings ->  new ArmorComponentItem(settings.maxCount(1),
                    ArmorComponentItem.ComponentType.GORGET, 1, 30));

    public static final Item IRON_FAULD = registerItem("iron_fauld",
            settings -> new ArmorComponentItem(settings.maxCount(1),
                    ArmorComponentItem.ComponentType.FAULD, 2, 40));

    public static final Item STEEL_REINFORCEMENT = registerItem("steel_reinforcement",
            settings ->  new ArmorComponentItem(settings.maxCount(1),
                    ArmorComponentItem.ComponentType.REINFORCEMENT, 1, 100));

    private static Item registerItem(String name, Function<Item.Settings, Item> function) {
        return Registry.register(Registries.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, name),
                function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, name)))));
    }
    public static void registerModItems() {
        DarkArtsRevival.LOGGER.info("Registering Mod Items for " + DarkArtsRevival.MOD_ID);
    }
}
