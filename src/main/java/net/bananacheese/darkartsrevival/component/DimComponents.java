package net.bananacheese.darkartsrevival.component;

import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public class DimComponents implements EntityComponentInitializer {

    public static final ComponentKey<ReturnPositionComponent> RETURN_POS =
            ComponentRegistry.getOrCreate(
                    Identifier.of("darkartsrevival", "return_pos"),
                    ReturnPositionComponent.class
            );

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(
                RETURN_POS,
                player -> new ReturnPositionComponentImpl(),
                RespawnCopyStrategy.ALWAYS_COPY
        );
    }
}