package io.github.afamiliarquiet.familiar_magic.network;

import io.github.afamiliarquiet.familiar_magic.block.entity.SummoningTableBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

public record SummoningResponsePayload(BlockPos acceptedPos, boolean accepted) implements CustomPacketPayload {
    public static final Type<SummoningResponsePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "summoning_accepted_payload"));

    // is this is this sensible? this feels sensible enough enough.
    public static final StreamCodec<ByteBuf, SummoningResponsePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(BlockPos.CODEC),
            SummoningResponsePayload::acceptedPos,
            ByteBufCodecs.BOOL,
            SummoningResponsePayload::accepted,
            SummoningResponsePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void atYourService(final SummoningResponsePayload payload, final IPayloadContext context) {
        BlockEntity acceptedEntity = context.player().level().getBlockEntity(payload.acceptedPos);
        if (acceptedEntity instanceof SummoningTableBlockEntity tableEntity) {
            if (payload.accepted) {
                tableEntity.acceptSummoning(context.player());
            } else {
                // todo - maybe something else here that's more indicative of rejection.. later
                tableEntity.cancelSummoning();
            }
        }
    }
}
