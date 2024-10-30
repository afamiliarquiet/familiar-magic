package io.github.afamiliarquiet.familiar_magic.client.gooey;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

// oh my god cover it with cheese the ooey gooey cheesy oh yeah oh my god does it have like a bunch of fucking nacho cheese on it oh based
public class FamiliarGUIStuffs {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MOD_ID);

    public static final Supplier<MenuType<SummoningTableMenu>> SUMMONING_TABLE_MENU = MENUS.register(
            "summoning_table_menu",
            () -> new MenuType<>(SummoningTableMenu::new, FeatureFlags.DEFAULT_FLAGS)
    );

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
        eventBus.addListener(FamiliarGUIStuffs::mrwRegisterMenuScreensEvent);
    }

    private static void mrwRegisterMenuScreensEvent(RegisterMenuScreensEvent event) {
        event.register(SUMMONING_TABLE_MENU.get(), SummoningTableScreen::new);
    }
}
