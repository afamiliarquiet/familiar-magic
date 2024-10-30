package io.github.afamiliarquiet.familiar_magic.client.gooey;

import io.github.afamiliarquiet.familiar_magic.FamiliarMagicClient;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

@ParametersAreNonnullByDefault
public class SummoningRequestLayer implements LayeredDraw.Layer {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/summoning_request.png");
    private final int imageHeight = 128;
    private final int imageWidth = 128;
    private final int textureHeight = 128;
    private final int textureWidth = 128;
    private final int spacing = 13;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if ( // wowowow intellij makes u really float all the way out here huh
                player == null
                        || !player.hasData(FamiliarAttachments.FAMILIAR_SUMMONING_DESTINATION)
                        || !player.hasData(FamiliarAttachments.FAMILIAR_SUMMONING_OFFERINGS)
        ) {
            return;
        }

        int top = guiGraphics.guiHeight() - (this.imageHeight + this.spacing);
        int left = guiGraphics.guiWidth() - (this.imageWidth + this.spacing);

        guiGraphics.blit(BACKGROUND, left, top, 0, 0, imageWidth, imageHeight, textureWidth, textureHeight);

        Component titleComponent = Component.translatable(
                "gui.familiar_magic.summoning_request.name"
        );
        Component blurb = Component.translatable(
                "gui.familiar_magic.summoning_request.blurb",
                FamiliarMagicClient.FOCUS_MAPPING.get().getKey().getDisplayName(),
                minecraft.options.keyJump.getKey().getDisplayName(),
                minecraft.options.keyShift.getKey().getDisplayName()
        );
        BlockPos destinationPos = player.getData(FamiliarAttachments.FAMILIAR_SUMMONING_DESTINATION);
        Component positionComponent = Component.translatable(
                "gui.familiar_magic.summoning_request.position",
                destinationPos.getX(),
                destinationPos.getY(),
                destinationPos.getZ()
        );
        List<ItemStack> offerings = player.getData(FamiliarAttachments.FAMILIAR_SUMMONING_OFFERINGS);

        this.drawCenteredStringAtHeight(guiGraphics, minecraft.font, top, left, titleComponent, 0);
        guiGraphics.drawWordWrap(minecraft.font, blurb, left + this.spacing, top + 26, this.imageWidth - 2 * this.spacing, 0x492f5b);
        this.drawCenteredStringAtHeight(guiGraphics, minecraft.font, top, left, positionComponent, 72);

        this.drawItems(guiGraphics, minecraft.font, top, left, offerings);
    }

    private void drawCenteredStringAtHeight(GuiGraphics guiGraphics, Font font, int top, int left, Component component, int height) {
        guiGraphics.drawString(
                font,
                component,
                left + this.imageWidth / 2 - font.width(component) / 2,
                top + this.spacing + height,
                0x492f5b,
                false
        );
    }

    private void drawItems(GuiGraphics guiGraphics, Font font, int top, int left, List<ItemStack> offerings) {
        for (int i = 0; i < 4; i++) {
            int itemTop = top + 98;
            int itemLeft = i * 18 + left + 29;

            guiGraphics.renderItem(
                    offerings.get(i),
                    itemLeft,
                    itemTop
            );
            guiGraphics.renderItemDecorations(
                    font,
                    offerings.get(i),
                    itemLeft,
                    itemTop
            );
        }
    }
}
