package io.github.afamiliarquiet.familiar_magic.network;

import io.github.afamiliarquiet.familiar_magic.data.SummoningRequestData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;
import static io.github.afamiliarquiet.familiar_magic.FamiliarTricks.removeRequest;
import static io.github.afamiliarquiet.familiar_magic.FamiliarTricks.setRequest;

public record SummoningRequestPayload(SummoningRequestData requestData, boolean cancelled) implements CustomPacketPayload {
    public static final Type<SummoningRequestPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "summoning_start_payload"));

    public static final StreamCodec<ByteBuf, SummoningRequestPayload> STREAM_CODEC = StreamCodec.composite(
            SummoningRequestData.STREAM_CODEC,
            SummoningRequestPayload::requestData,
            ByteBufCodecs.BOOL,
            SummoningRequestPayload::cancelled,
            SummoningRequestPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void ayeAyeRequestReceived(final SummoningRequestPayload summoningRequestPayload, final IPayloadContext context) {
        // why did neo get mad about a localplayer here? this should only be handled on client, where we have local players? am i not allowed to import for that?
        // i was literally doin that before!! i dont get it i changed too much without testing
        // is this because i'm adding it a call to this in a listener in FamiliarMagic's common listeners??
        Player hehehe = context.player();
        if (summoningRequestPayload.cancelled) {
            // remove if we have it, and only if it seems to be from the same source as what we have
            removeRequest(hehehe, summoningRequestPayload.requestData);
        } else {
            // also set like, request time? to 30? and fade screen in and out at the ends
            setRequest(hehehe, summoningRequestPayload.requestData);
        }
    }
}
