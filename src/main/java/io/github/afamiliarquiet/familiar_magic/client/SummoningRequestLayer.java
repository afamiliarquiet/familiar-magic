package io.github.afamiliarquiet.familiar_magic.client;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SummoningRequestLayer implements LayeredDraw.Layer {
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

        BlockPos destinationPos = player.getData(FamiliarAttachments.FAMILIAR_SUMMONING_DESTINATION);
        List<ItemStack> offerings = player.getData(FamiliarAttachments.FAMILIAR_SUMMONING_OFFERINGS);

        guiGraphics.drawStringWithBackdrop(
                minecraft.font,
                Component.literal(String.format(
                        "You are being summoned to %d / %d / %d",
                        destinationPos.getX(),
                        destinationPos.getY(),
                        destinationPos.getZ()
                )),
                50,
                50,
                0,
                0x895b76
        );

        for (int i = 0; i < offerings.size(); i++) {
            guiGraphics.renderItem(
                    offerings.get(i),
                    i * 25 + 50,
                    75
            );
            guiGraphics.renderItemDecorations(
                    minecraft.font,
                    offerings.get(i),
                    i * 25 + 50,
                    75
            );
        }
    }
}
