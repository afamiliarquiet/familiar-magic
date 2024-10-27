package io.github.afamiliarquiet.familiar_magic.network;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

public record FocusPayload(boolean iAmHereToAnnounceThatThePlayerHasBegunToBelieve) implements CustomPacketPayload {
    public static final Type<FocusPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "focus_payload"));

    public static final StreamCodec<ByteBuf, FocusPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            FocusPayload::iAmHereToAnnounceThatThePlayerHasBegunToBelieve,
            FocusPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void focusEater(final FocusPayload focusPayload, final IPayloadContext context) {
        // do Somethin. prolly. heck, i may not even need this packet stuff, but it's nice to have it set up if i do.
        // like actually i kinda just want the key press stuff on client.. ah well. whatever.
        // that day has come!
        context.player().setData(FamiliarAttachments.FOCUSED, focusPayload.iAmHereToAnnounceThatThePlayerHasBegunToBelieve);
    }
}
