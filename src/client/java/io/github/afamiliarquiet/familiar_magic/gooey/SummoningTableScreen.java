package io.github.afamiliarquiet.familiar_magic.gooey;

import io.github.afamiliarquiet.familiar_magic.FamiliarMagic;
import io.github.afamiliarquiet.familiar_magic.FamiliarTricks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public class SummoningTableScreen extends HandledScreen<SummoningTableScreenHandler> {
    private static final Identifier BACKGROUND = FamiliarMagic.id("textures/gui/container/summoning_table.png");
    private static final Identifier BACKGROUND_MOODY = FamiliarMagic.id("textures/gui/container/summoning_table_blocked.png");

    private enum ErrorIcon {
        NO_CANDLE,
        BAD_QUANTITY,
        BAD_HEIGHT,
        UNLIT_CANDLE,
        UNMATCHED_CANDLE,
        MATCHED_CANDLE;

        public String translation() {
            return "gui.familiar_magic.candle_helper." + this.toString().toLowerCase();
        }

        public Identifier id() {
            return FamiliarMagic.id("container/" + this.toString().toLowerCase());
        }

        public static ErrorIcon of(byte desired, byte present, boolean badItem) {
            if ((FamiliarTricks.NO_CANDLE & present) != 0) {
                return NO_CANDLE;
            } else if ((desired & 0b1100) != (present & 0b1100)) {
                return BAD_HEIGHT;
            } else if ((desired & 0b11) != (present & 0b11)) {
                return BAD_QUANTITY;
            } else if ((FamiliarTricks.UNLIT_CANDLE & present) != 0) {
                return UNLIT_CANDLE;
            } else if (badItem) {
                return UNMATCHED_CANDLE;
            } else {
                return MATCHED_CANDLE;
            }
        }
    }

    private static final int[] CANDLE_CENTER_SCREEN_OFFSETS = {
            -30,-30,  -18,-30,  -6,-30,  6,-30,  18,-30,  30,-30,
            -30,-18,  -18,-18,  -6,-18,  6,-18,  18,-18,  30,-18,
            -30, -6,  -18, -6,                   18, -6,  30, -6,
            -30,  6,  -18,  6,                   18,  6,  30,  6,
            -30, 18,  -18, 18,  -6, 18,  6, 18,  18, 18,  30, 18,
            -30, 30,  -18, 30,  -6, 30,  6, 30,  18, 30,  30, 30
    };

    public SummoningTableScreen(SummoningTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.playerInventoryTitleY += 21;
        this.backgroundHeight += 21;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(
                this.handler.isModifiable() ? BACKGROUND : BACKGROUND_MOODY,
                this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        //drawBackground(context, delta, mouseX, mouseY);
        super.render(context, mouseX, mouseY, delta);
        drawCandleGuide(context, mouseX, mouseY);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    protected void drawCandleGuide(DrawContext context, int mouseX, int mouseY) {
        int centerX = this.x + this.handler.trueNameSlot.x + 8;
        int centerY = this.y + this.handler.trueNameSlot.y + 8;

        byte[] itemNybbles = FamiliarTricks.trueNameToNybbles(this.handler.trueNameSlot.getStack().getName().getString());
        byte[] worldNybbles = this.handler.getWorldNybbles();

        boolean badItem = itemNybbles == null || itemNybbles.length != 32;
        if (badItem) {
            itemNybbles = worldNybbles;
        }

        for (int i = 0; i < 32; i++) {
            ErrorIcon icon = ErrorIcon.of(itemNybbles[i], worldNybbles[i], badItem);
            drawCandleTile(
                    context,
                    centerX + CANDLE_CENTER_SCREEN_OFFSETS[i * 2],
                    centerY + CANDLE_CENTER_SCREEN_OFFSETS[i * 2 + 1],
                    mouseX, mouseY,
                    icon.translation(), icon.id()
            );
        }
    }

    protected void drawCandleTile(DrawContext context, int centerX, int centerY, int mouseX, int mouseY, String translation, Identifier sprite) {
        int x = centerX - 4;
        int y = centerY - 4;
        context.drawGuiTexture(sprite, x, y, 8, 8);
        if (this.isPointWithinBounds(x - this.x, y - this.y, 8, 8, mouseX, mouseY)) {
            context.drawTooltip(this.textRenderer, List.of(Text.translatable(translation)), Optional.empty(), mouseX, mouseY);
        }
    }
}
