package net.bananacheese.darkartsrevival.component;

import com.mojang.serialization.Codec;
import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class DAComponents {

    public static final ComponentType<Integer> SYRINGE_FILL_LEVEL = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(DarkArtsRevival.MOD_ID, "syringe_fill_level"),
            ComponentType.<Integer>builder()
                    .codec(Codec.INT)
                    .packetCodec(PacketCodecs.VAR_INT)
                    .build()
    );

    public static void registerComponents() {
        DarkArtsRevival.LOGGER.info("Registering Data Components for " + DarkArtsRevival.MOD_ID);
    }
}
