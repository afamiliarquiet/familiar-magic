package io.github.afamiliarquiet.familiar_magic.network;

import io.github.afamiliarquiet.familiar_magic.block.entity.SummoningTableBlockEntity;
import io.github.afamiliarquiet.familiar_magic.data.SummoningRequestData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

public record SummoningResponsePayload(SummoningRequestData requestData, boolean accepted) implements CustomPacketPayload {
    public static final Type<SummoningResponsePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "summoning_accepted_payload"));

    // is this is this sensible? this feels sensible enough enough.
    public static final StreamCodec<ByteBuf, SummoningResponsePayload> STREAM_CODEC = StreamCodec.composite(
            SummoningRequestData.STREAM_CODEC,
            SummoningResponsePayload::requestData,
            ByteBufCodecs.BOOL,
            SummoningResponsePayload::accepted,
            SummoningResponsePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void iveGivenItSomeThought(final SummoningResponsePayload payload, final IPayloadContext context) {
        SummoningRequestData requestData = payload.requestData;
        eatRequestResponse(requestData, payload.accepted, context.player());
    }

    public static void eatRequestResponse(SummoningRequestData requestData, boolean accepted, Player player) {
        MinecraftServer server = player.getServer();
        if (server != null) {
            Level targetLevel = server.getLevel(requestData.tableLevelKey());
            if (targetLevel != null) {
                BlockEntity acceptedEntity = targetLevel.getBlockEntity(requestData.tablePos());
                if (acceptedEntity instanceof SummoningTableBlockEntity tableEntity) {
                    if (accepted) {
                        tableEntity.acceptSummoning(player);
                    } else {
                        // todo - maybe something else here that's more indicative of rejection.. later
                        tableEntity.cancelSummoning();
                    }
                }
            }
        }
    }
}
