package io.github.afamiliarquiet.familiar_magic.network;

import io.github.afamiliarquiet.familiar_magic.FamiliarMagic;
import io.github.afamiliarquiet.familiar_magic.FamiliarTricks;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record C2SFocusLuggage(boolean focused) implements CustomPayload {
    public static final CustomPayload.Id<C2SFocusLuggage> ID = new Id<>(FamiliarMagic.id("focus_luggage"));

    public static final PacketCodec<ByteBuf, C2SFocusLuggage> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL,
            C2SFocusLuggage::focused,
            C2SFocusLuggage::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void focusEater(C2SFocusLuggage lugged, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            // is this desirable? i'm not entirely sure. i'm more sure that i don't want to auto close the server though
            FamiliarAttachments.setFocused(context.player(), lugged.focused);
        });
    }
}
