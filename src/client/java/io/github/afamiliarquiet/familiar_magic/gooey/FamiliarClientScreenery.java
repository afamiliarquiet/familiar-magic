package io.github.afamiliarquiet.familiar_magic.gooey;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class FamiliarClientScreenery {
    public static void initialize() {
        HandledScreens.register(FamiliarScreenery.SUMMONING_TABLE_HANDLER_TYPE, SummoningTableScreen::new);

        HudRenderCallback.EVENT.register(new FocusRenderLayer());
        HudRenderCallback.EVENT.register(new SummoningRequestRenderLayer());
    }
}
