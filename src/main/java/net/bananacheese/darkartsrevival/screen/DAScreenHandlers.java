package net.bananacheese.darkartsrevival.screen;

import net.bananacheese.darkartsrevival.DarkArtsRevival;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class DAScreenHandlers {

    public static final ScreenHandlerType<GearForgeScreenHandler> GEAR_FORGE_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(DarkArtsRevival.MOD_ID, "gear_forge"),
                    new ScreenHandlerType<>(GearForgeScreenHandler::new, null)
            );

    public static void registerScreenHandlers() {
        DarkArtsRevival.LOGGER.info("Registering Screen Handlers for " + DarkArtsRevival.MOD_ID);
    }
}