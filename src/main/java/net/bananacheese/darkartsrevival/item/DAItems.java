package net.bananacheese.darkartsrevival.item;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.bananacheese.darkartsrevival.item.custom.AlterAnalyzer;
import net.bananacheese.darkartsrevival.item.custom.GearForgeHammer;
import net.bananacheese.darkartsrevival.item.custom.SoulSyringe;
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

    private static Item registerItem(String name, Function<Item.Settings, Item> function) {
        return Registry.register(Registries.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, name),
                function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DarkArtsRevival.MOD_ID, name)))));
    }
    public static void registerModItems() {
        DarkArtsRevival.LOGGER.info("Registering Mod Items for " + DarkArtsRevival.MOD_ID);
    }
}
