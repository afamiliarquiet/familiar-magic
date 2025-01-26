package io.github.afamiliarquiet.familiar_magic.gooey;

import io.github.afamiliarquiet.familiar_magic.FamiliarMagic;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class FamiliarScreenery {
    // ah, what a beautiful view..
    public static final ScreenHandlerType<SummoningTableScreenHandler> SUMMONING_TABLE_HANDLER_TYPE = register(
            "summoning_table_handler", SummoningTableScreenHandler::new
    );

    public static void initialize() {

    }

    public static <T extends ScreenHandler> ScreenHandlerType<T> register(String thing, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, FamiliarMagic.id(thing), new ScreenHandlerType<>(factory, FeatureSet.empty()));
    }
}
