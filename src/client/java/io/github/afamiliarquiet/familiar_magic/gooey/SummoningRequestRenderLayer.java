package io.github.afamiliarquiet.familiar_magic.gooey;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.afamiliarquiet.familiar_magic.FamiliarKeybinds;
import io.github.afamiliarquiet.familiar_magic.FamiliarMagic;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import io.github.afamiliarquiet.familiar_magic.data.SummoningRequestData;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class SummoningRequestRenderLayer implements HudRenderCallback {
    private static final Identifier BACKGROUND = FamiliarMagic.id("textures/gui/summoning_request.png");
    private final int imageHeight = 128;
    private final int imageWidth = 128;
    private final int textureHeight = 128;
    private final int textureWidth = 128;
    private final int spacing = 13;

    private float requestReadiness = 1;

    // i want you to know. i hate transparency and i hate opengl and i hate rendering. but it mostly works..
    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        ClientPlayerEntity player = minecraft.player;
        SummoningRequestData requestData = FamiliarAttachments.getRequest(player);

        if (player == null || requestData == null || minecraft.options.hudHidden) {
            return;
        }

        context.getMatrices().push();

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1);

        context.getMatrices().translate(0, 0, 3100);

        int top = context.getScaledWindowHeight() - (this.imageHeight + this.spacing);
        int left = /*context.getScaledWindowWidth() - (this.imageWidth + */this.spacing/*)*/;

        context.drawTexture(BACKGROUND, left, top, 0, 0, imageWidth, imageHeight, textureWidth, textureHeight);

        Text titleComponent = Text.translatable(
                "gui.familiar_magic.summoning_request.name"
        );
        Text blurb = Text.translatable(
                "gui.familiar_magic.summoning_request.blurb",
                FamiliarKeybinds.FOCUS_HOLD.getBoundKeyLocalizedText(),
                minecraft.options.jumpKey.getBoundKeyLocalizedText(),
                minecraft.options.sneakKey.getBoundKeyLocalizedText()
        );

        // i have a hunch this may not exist but we shall see! maybe neoforge is nice to me today
        // ya didnt exist. so i make it my own and it's fine enough, compat with other mod dimensions should be easy enough
        Text levelComponent = Text.translatable("familiar_magic.world_key." + requestData.tableWorldKey().getValue().toTranslationKey());
        BlockPos destinationPos = requestData.tablePos();
        Text positionComponent = Text.translatable(
                "gui.familiar_magic.summoning_request.position",
                destinationPos.getX(),
                destinationPos.getY(),
                destinationPos.getZ()
        );
        List<ItemStack> offerings = requestData.offerings().orElse(List.of()); // *should* always be present but w/e

        if (requestReadiness < 1) {
            // lerp is mathematically poorly suited to this job i know it doesn't matter
            requestReadiness = MathHelper.lerp(
                    0.075f * tickCounter.getLastFrameDuration(),
                    this.requestReadiness,
                    1f);
            if (requestReadiness > 0.99) {
                requestReadiness = 1;
            } else {
                // enable the transparency nonsense
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                context.setShaderColor(1.0F, 1.0F, 1.0F, requestReadiness);
            }
        }

        this.drawCenteredStringAtHeight(context, minecraft.textRenderer, top, left, titleComponent, 0);
        context.drawTextWrapped(minecraft.textRenderer, blurb, left + this.spacing, top + 26, this.imageWidth - 2 * this.spacing, 0x382414);
        this.drawCenteredStringAtHeight(context, minecraft.textRenderer, top, left, levelComponent, 62);
        this.drawCenteredStringAtHeight(context, minecraft.textRenderer, top, left, positionComponent, 72);

        this.drawItems(context, minecraft.textRenderer, top, left, offerings);

        if (requestReadiness < 1) {
            // disable the transparency nonsense
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        context.getMatrices().pop();
    }
    
    private void drawCenteredStringAtHeight(DrawContext context, TextRenderer font, int top, int left, Text text, int height) {
        context.drawText(
                font,
                text,
                left + this.imageWidth / 2 - font.getWidth(text) / 2,
                top + this.spacing + height,
                0x492f5b,
                false
        );
    }
    
    private void drawItems(DrawContext context, TextRenderer font, int top, int left, List<ItemStack> offerings) {
        if (offerings.size() != 4) {
            // just to be extra safe
            return;
        }

        for (int i = 0; i < 4; i++) {
            int itemTop = top + 98;
            int itemLeft = i * 18 + left + 29;

            context.drawItem(
                    offerings.get(i),
                    itemLeft,
                    itemTop
            );
            context.drawItemInSlot(
                    font,
                    offerings.get(i),
                    itemLeft,
                    itemTop
            );
        }
    }

    public void reset() {
        this.requestReadiness = 0;
    }

    public boolean isReady() {
        return this.requestReadiness > 0.62;
    }
}
