package io.github.afamiliarquiet.familiar_magic.network;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;
import static io.github.afamiliarquiet.familiar_magic.FamiliarTricks.canWearHat;

public record HattedPayload(ItemStack hatStack, int entityId) implements CustomPacketPayload {
    public static final Type<HattedPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "hatted_payload"));

    public static final StreamCodec<RegistryFriendlyByteBuf, HattedPayload> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, // no actually minecraft needs the registry version of bytebuf, sorry
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
        if (canWearHat(hattedEntity)) {
            hattedEntity.getData(FamiliarAttachments.HAT).setStackInSlot(0, hattedPayload.hatStack);
        }
    }
}
