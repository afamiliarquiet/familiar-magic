package io.github.afamiliarquiet.familiar_magic.network;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import io.github.afamiliarquiet.familiar_magic.data.HatWearer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

public record HattedPayload(ItemStack hatStack, int entityId) implements CustomPacketPayload {
    public static final Type<HattedPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "hatted_payload"));

    public static final StreamCodec<ByteBuf, HattedPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(ItemStack.OPTIONAL_CODEC), // idk about this. but it seems like minecraft is ok with this
            HattedPayload::hatStack,
            ByteBufCodecs.VAR_INT,
            HattedPayload::entityId,
            HattedPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void hattedOhILoveHatted(final HattedPayload hattedPayload, final IPayloadContext context) {
        Entity hattedEntity = context.player().level().getEntity(hattedPayload.entityId);
        if (hattedEntity instanceof HatWearer) {
            hattedEntity.getData(FamiliarAttachments.HAT).setStackInSlot(0, hattedPayload.hatStack);
        }
    }
}
