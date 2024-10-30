package io.github.afamiliarquiet.familiar_magic.network;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

public record SummoningCancelledPayload() implements CustomPacketPayload {
    public static final Type<SummoningCancelledPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "summoning_cancelled_payload"));

    // is this sensible? this feels sensible enough.
    public static final StreamCodec<ByteBuf, SummoningCancelledPayload> STREAM_CODEC = StreamCodec.of(
            (buffer, value) -> {},
            (buffer -> new SummoningCancelledPayload())
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sorry(final SummoningCancelledPayload payload, final IPayloadContext context) {
        // is this something to be encouraged?
        // i feel like i should store it on local playerentity data attachment or something instead. that feels better.
        LocalPlayer hehehe = Minecraft.getInstance().player;
        if (hehehe != null) {
            hehehe.removeData(FamiliarAttachments.FAMILIAR_SUMMONING_DESTINATION);
            hehehe.removeData(FamiliarAttachments.FAMILIAR_SUMMONING_OFFERINGS);
        }
    }
}
