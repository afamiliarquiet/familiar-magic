package io.github.afamiliarquiet.familiar_magic.client.gooey;

import com.mojang.datafixers.util.Pair;
import io.github.afamiliarquiet.familiar_magic.FamiliarTricks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SummoningTableScreen extends AbstractContainerScreen<SummoningTableMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/container/summoning_table.png");
    private static final ResourceLocation BACKGROUND_MOODY = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/container/summoning_table_blocked.png");

    private static final Pair<String, ResourceLocation> BAD_HEIGHT = Pair.of(
            "gui.familiar_magic.candle_helper.bad_height",
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "container/bad_height")
    );
    private static final Pair<String, ResourceLocation> BAD_QUANTITY = Pair.of(
            "gui.familiar_magic.candle_helper.bad_quantity",
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "container/bad_quantity")
    );
    private static final Pair<String, ResourceLocation> MATCHED_CANDLE = Pair.of(
            "gui.familiar_magic.candle_helper.matched_candle",
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "container/matched_candle")
    );
    private static final Pair<String, ResourceLocation> NO_CANDLE = Pair.of(
            "gui.familiar_magic.candle_helper.no_candle",
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "container/no_candle")
    );
    private static final Pair<String, ResourceLocation> UNLIT_CANDLE = Pair.of(
            "gui.familiar_magic.candle_helper.unlit_candle",
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "container/unlit_candle")
    );
    private static final Pair<String, ResourceLocation> UNMATCHED_CANDLE = Pair.of(
            "gui.familiar_magic.candle_helper.unmatched_candle",
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "container/unmatched_candle")
    );

    private static final int[] CANDLE_CENTER_SCREEN_OFFSETS = {
            -30,-30,  -18,-30,  -6,-30,  6,-30,  18,-30,  30,-30,
            -30,-18,  -18,-18,  -6,-18,  6,-18,  18,-18,  30,-18,
            -30, -6,  -18, -6,                   18, -6,  30, -6,
            -30,  6,  -18,  6,                   18,  6,  30,  6,
            -30, 18,  -18, 18,  -6, 18,  6, 18,  18, 18,  30, 18,
            -30, 30,  -18, 30,  -6, 30,  6, 30,  18, 30,  30, 30
    };

    public SummoningTableScreen(SummoningTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        // i made this 14px taller than normal menu so make it BIGGER
        this.inventoryLabelY += 21;
        this.imageHeight += 21;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // todo - have some indication of orientation, like a chipped corner on the table or something idk
        if (this.menu.tableData.isModifiable()) {
            graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        } else {
            graphics.blit(BACKGROUND_MOODY, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderCandleGuide(graphics, mouseX, mouseY);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    protected void renderCandleGuide(GuiGraphics graphics, int mouseX, int mouseY) {
        int centerX = this.leftPos + this.menu.trueNameSlot.x + 8;
        int centerY = this.topPos + this.menu.trueNameSlot.y + 8;

        byte[] itemNybbles = FamiliarTricks.trueNameToNybbles(this.menu.trueNameSlot.getItem().getHoverName().getString());
        byte[] worldNybbles = this.menu.tableData.getNybbles();

        boolean badItem = itemNybbles == null || itemNybbles.length != 32;
        if (badItem) {
            itemNybbles = worldNybbles;
        }

        for (int i = 0; i < 32; i++) {
            Pair<String, ResourceLocation> candleInfo = getCandleSprite(itemNybbles[i], worldNybbles[i], badItem);
            renderCandleTile(
                    graphics,
                    centerX + CANDLE_CENTER_SCREEN_OFFSETS[i * 2],
                    centerY + CANDLE_CENTER_SCREEN_OFFSETS[i * 2 + 1],
                    mouseX, mouseY,
                    candleInfo.getFirst(), candleInfo.getSecond()
            );
        }
    }

    private void renderCandleTile(GuiGraphics graphics, int centerX, int centerY, int mouseX, int mouseY, String translationKey, ResourceLocation candleSprite) {
        int x = centerX - 4;
        int y = centerY - 4;
        graphics.blitSprite(candleSprite, x, y, 8, 8);
        if (this.isHovering(x - this.leftPos, y - this.topPos, 8, 8, mouseX, mouseY)) {
            graphics.renderTooltip(this.font, List.of(Component.translatable(translationKey)), Optional.empty(), mouseX, mouseY);
        }
    }

    private Pair<String, ResourceLocation> getCandleSprite(byte item, byte world, boolean badItem) {
        if ((FamiliarTricks.NO_CANDLE & world) != 0) { // no candle in world for nybble
            return NO_CANDLE;
        } else if ((item & 0b1100) != (world & 0b1100)) { // wrong height
            return BAD_HEIGHT;
        } else if ((item & 0b11) != (world & 0b11)) { // wrong # of candles
            return BAD_QUANTITY;
        } else if ((FamiliarTricks.UNLIT_CANDLE & world) != 0) { // unlit
            return UNLIT_CANDLE;
        } else if (badItem) {
            return UNMATCHED_CANDLE;
        } else { // correct
            return MATCHED_CANDLE;
        }
    }
}
