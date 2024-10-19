package io.github.afamiliarquiet.familiar_magic.mixin;

import io.github.afamiliarquiet.familiar_magic.FamiliarMagicClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class FocusOverlayMixin {
    @Unique
    private float familiar_magic$focusIntensity = 0.0f;

    @Shadow
    protected abstract void renderTextureOverlay(GuiGraphics guiGraphics, ResourceLocation shaderLocation, float alpha);

    @Inject(at = @At("TAIL"), method = "renderCameraOverlays")
    private void renderFocusOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (FamiliarMagicClient.FOCUSED_LAST_TICK.get()) {
            familiar_magic$focusIntensity = Mth.lerp(
                    0.5F * deltaTracker.getGameTimeDeltaTicks(),
                    this.familiar_magic$focusIntensity,
                    1f);
            renderTextureOverlay(guiGraphics, FamiliarMagicClient.FOCUS_OVERLAY, familiar_magic$focusIntensity);
        } else if (familiar_magic$focusIntensity > 0.0f) {
            familiar_magic$focusIntensity = Mth.lerp(
                    0.5F * deltaTracker.getGameTimeDeltaTicks(),
                    this.familiar_magic$focusIntensity,
                    0f);
            renderTextureOverlay(guiGraphics, FamiliarMagicClient.FOCUS_OVERLAY, familiar_magic$focusIntensity);
            if (familiar_magic$focusIntensity <= 0.01f) {
                familiar_magic$focusIntensity = 0.0f;
            }
        }
    }
}
