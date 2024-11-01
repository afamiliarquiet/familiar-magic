package io.github.afamiliarquiet.familiar_magic.client.gooey;

import io.github.afamiliarquiet.familiar_magic.FamiliarTricks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.ParametersAreNonnullByDefault;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SummoningTableScreen extends AbstractContainerScreen<SummoningTableMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/container/summoning_table.png");
    private static final ResourceLocation BACKGROUND_MOODY = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/container/summoning_table_blocked.png");

    private static final ResourceLocation NO_CANDLE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "container/no_candle");
    private static final ResourceLocation UNMATCHED_CANDLE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "container/unmatched_candle");
    private static final ResourceLocation BAD_HEIGHT = ResourceLocation.fromNamespaceAndPath(MOD_ID, "container/bad_height");
    private static final ResourceLocation BAD_QUANTITY = ResourceLocation.fromNamespaceAndPath(MOD_ID, "container/bad_quantity");
    private static final ResourceLocation UNLIT_CANDLE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "container/unlit_candle");
    private static final ResourceLocation MATCHED_CANDLE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "container/matched_candle");

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
        this.inventoryLabelY += 14;
        this.imageHeight += 14;
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
        this.renderCandleGuide(graphics);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    protected void renderCandleGuide(GuiGraphics graphics) {
        int centerX = this.leftPos + this.menu.trueNameSlot.x + 8;
        int centerY = this.topPos + this.menu.trueNameSlot.y + 8;

        byte[] itemNybbles = FamiliarTricks.trueNameToNybbles(this.menu.trueNameSlot.getItem().getHoverName().getString());
        byte[] worldNybbles = this.menu.tableData.getNybbles();

        boolean badItem = itemNybbles == null || itemNybbles.length != 32;
        if (badItem) {
            itemNybbles = worldNybbles;
        }

        for (int i = 0; i < 32; i++) {
            renderCandleTile(
                    graphics,
                    centerX + CANDLE_CENTER_SCREEN_OFFSETS[i * 2],
                    centerY + CANDLE_CENTER_SCREEN_OFFSETS[i * 2 + 1],
                    getCandleSprite(itemNybbles[i], worldNybbles[i], badItem)
            );
        }
    }

    private void renderCandleTile(GuiGraphics graphics, int centerX, int centerY, ResourceLocation candleSprite) {
        graphics.blitSprite(candleSprite, centerX - 4, centerY - 4, 8, 8);
    }

    private ResourceLocation getCandleSprite(byte item, byte world, boolean badItem) {
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

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

//        guiGraphics.drawString(
//                this.font,
//                Component.literal(FamiliarTricks.uuidToTrueName(UUIDUtil.uuidFromIntArray(new int[]{
//                        menu.tableData.get(0),
//                        menu.tableData.get(1),
//                        menu.tableData.get(2),
//                        menu.tableData.get(3)
//                }))),
//                52,
//                31,
//                0x404040,
//                false
//        );
    }
}
