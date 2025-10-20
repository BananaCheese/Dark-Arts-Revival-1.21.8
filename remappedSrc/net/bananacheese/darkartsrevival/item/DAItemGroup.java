package net.bananacheese.darkartsrevival.item;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.bananacheese.darkartsrevival.block.DABlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DAItemGroup {
    public static final ItemGroup DA_ITEM_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(DarkArtsRevival.MOD_ID, "da_item_group"),
            FabricItemGroup.builder().icon(() -> new ItemStack(DABlocks.ALTER))
                    .displayName(Text.translatable("itemgroup.darkartsrevival.da_item_group"))
                    .entries((displayContext, entries) -> {

                        entries.add(DABlocks.ALTER);
                        entries.add(DABlocks.PEDESTAL);
                        entries.add(DAItems.ALTER_ANALYZER);
                        entries.add(DAItems.SOUL_SYRINGE);

                    }).build());

    public static void registerItemGroups() {
        DarkArtsRevival.LOGGER.info("Registering Item Groups for " + DarkArtsRevival.MOD_ID);
    }
}
