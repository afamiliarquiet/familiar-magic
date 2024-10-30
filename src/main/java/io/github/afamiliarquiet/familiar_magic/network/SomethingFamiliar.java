package io.github.afamiliarquiet.familiar_magic.network;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

public record SomethingFamiliar(BlockPos summoningPos, List<ItemStack> offerings) implements CustomPacketPayload {
    public static final Type<SomethingFamiliar> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "summoning_start_payload"));

    public static final StreamCodec<ByteBuf, SomethingFamiliar> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(BlockPos.CODEC),
            SomethingFamiliar::summoningPos,
            ByteBufCodecs.collection( // goidness ggrayshesous
                    NonNullList::createWithCapacity,
                    ByteBufCodecs.fromCodec(ItemStack.OPTIONAL_CODEC),
                    4
            ),
            SomethingFamiliar::offerings,
            SomethingFamiliar::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void hmHmYouWantToSummonMe(final SomethingFamiliar somethingFamiliar, final IPayloadContext context) {
        // is this something to be encouraged?
        // i feel like i should store it on local playerentity data attachment or something instead. that feels better.
        LocalPlayer hehehe = Minecraft.getInstance().player;
        if (hehehe != null) {
            // also set like, request time? to 30? and fade screen in and out at the ends
            hehehe.setData(FamiliarAttachments.FAMILIAR_SUMMONING_DESTINATION, somethingFamiliar.summoningPos);
            hehehe.setData(FamiliarAttachments.FAMILIAR_SUMMONING_OFFERINGS, somethingFamiliar.offerings);
        }
    }
}
