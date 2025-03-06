package io.github.afamiliarquiet.familiar_magic.gooey;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class FamiliarClientScreenery {
    // im up to no good
    public static final FocusRenderLayer FOCUS_RENDER_LAYER = regify(new FocusRenderLayer());
    public static final SummoningRequestRenderLayer SUMMONING_REQUEST_RENDER_LAYER = regify(new SummoningRequestRenderLayer());

    public static void initialize() {
        HandledScreens.register(FamiliarScreenery.SUMMONING_TABLE_HANDLER_TYPE, SummoningTableScreen::new);
    }

    private static <T extends HudRenderCallback> T regify(T hudRenderCallback) {
        HudRenderCallback.EVENT.register(hudRenderCallback);
        return hudRenderCallback;
    }
}
