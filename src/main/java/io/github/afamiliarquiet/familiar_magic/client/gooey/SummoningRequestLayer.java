package io.github.afamiliarquiet.familiar_magic.client.gooey;

import io.github.afamiliarquiet.familiar_magic.FamiliarMagicClient;
import io.github.afamiliarquiet.familiar_magic.data.SummoningRequestData;
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
import static io.github.afamiliarquiet.familiar_magic.FamiliarTricks.getRequest;
import static io.github.afamiliarquiet.familiar_magic.FamiliarTricks.hasRequest;

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

        if (player == null || !hasRequest(player)) {
            return;
        }

        SummoningRequestData requestData = getRequest(player);

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

        // i have a hunch this may not exist but we shall see! maybe neoforge is nice to me today
        // ya didnt exist. so i make it my own and it's fine enough, compat with other mod dimensions should be easy enough
        Component levelComponent = Component.translatable("familiar_magic.level_key." + requestData.tableLevelKey().location().toLanguageKey());
        BlockPos destinationPos = requestData.tablePos();
        Component positionComponent = Component.translatable(
                "gui.familiar_magic.summoning_request.position",
                destinationPos.getX(),
                destinationPos.getY(),
                destinationPos.getZ()
        );
        List<ItemStack> offerings = requestData.offerings().orElse(List.of()); // *should* always be present but w/e

        this.drawCenteredStringAtHeight(guiGraphics, minecraft.font, top, left, titleComponent, 0);
        guiGraphics.drawWordWrap(minecraft.font, blurb, left + this.spacing, top + 26, this.imageWidth - 2 * this.spacing, 0x382414);
        this.drawCenteredStringAtHeight(guiGraphics, minecraft.font, top, left, levelComponent, 62);
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
        if (offerings.size() != 4) {
            // just to be extra safe
            return;
        }

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
