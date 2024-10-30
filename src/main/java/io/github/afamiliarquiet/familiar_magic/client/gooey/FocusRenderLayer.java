package io.github.afamiliarquiet.familiar_magic.client.gooey;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.afamiliarquiet.familiar_magic.FamiliarMagicClient;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FocusRenderLayer implements LayeredDraw.Layer {
    private float focusIntensity = 0.0f;
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.getData(FamiliarAttachments.FOCUSED)) {
            focusIntensity = Mth.lerp(
                    0.5F * deltaTracker.getGameTimeDeltaTicks(),
                    this.focusIntensity,
                    1f);
            renderFocusOverlay(guiGraphics, focusIntensity);
            if (focusIntensity >= 0.99f) {
                focusIntensity = 1.0f;
            }
        } else if (focusIntensity > 0.0f) {
            focusIntensity = Mth.lerp(
                    0.5F * deltaTracker.getGameTimeDeltaTicks(),
                    this.focusIntensity,
                    0f);
            renderFocusOverlay(guiGraphics, focusIntensity);
            if (focusIntensity <= 0.01f) {
                focusIntensity = 0.0f;
            }
        }
    }

    // eat my access tf
    private void renderFocusOverlay(GuiGraphics guiGraphics, float alpha) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        guiGraphics.blit(FamiliarMagicClient.FOCUS_OVERLAY, 0, 0, -90, 0.0F, 0.0F, guiGraphics.guiWidth(), guiGraphics.guiHeight(), guiGraphics.guiWidth(), guiGraphics.guiHeight());
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
