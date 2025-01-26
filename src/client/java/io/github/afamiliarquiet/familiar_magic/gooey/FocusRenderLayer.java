package io.github.afamiliarquiet.familiar_magic.gooey;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.afamiliarquiet.familiar_magic.FamiliarMagicClient;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.MathHelper;

public class FocusRenderLayer implements HudRenderCallback {
    private float focusIntensity = 0.0f;
    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.getAttachedOrCreate(FamiliarAttachments.FOCUSED)) {
            focusIntensity = MathHelper.lerp(
                    0.5f * tickCounter.getLastFrameDuration(),
                    this.focusIntensity,
                    1f);
            renderFocusOverlay(context, focusIntensity);
            if (focusIntensity >= 0.99f) {
                focusIntensity = 1.0f;
            }
        } else if (focusIntensity > 0.0f) {
            focusIntensity = MathHelper.lerp(
                    0.5f * tickCounter.getLastFrameDuration(),
                    this.focusIntensity,
                    0f);
            renderFocusOverlay(context, focusIntensity);
            if (focusIntensity <= 0.01f) {
                focusIntensity = 0f;
            }
        }
    }

    // "eat my access tf", it says
    private void renderFocusOverlay(DrawContext context, float alpha) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        context.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        context.drawTexture(FamiliarMagicClient.FOCUS_OVERLAY, 0, 0, -90, 0.0F, 0.0F, context.getScaledWindowWidth(), context.getScaledWindowHeight(), context.getScaledWindowWidth(), context.getScaledWindowHeight());
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
