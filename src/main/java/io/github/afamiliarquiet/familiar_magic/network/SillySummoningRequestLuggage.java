package io.github.afamiliarquiet.familiar_magic.network;

import io.github.afamiliarquiet.familiar_magic.FamiliarMagic;
import io.github.afamiliarquiet.familiar_magic.block.entity.SummoningTableBlockEntity;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import io.github.afamiliarquiet.familiar_magic.data.SummoningRequestData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public record SillySummoningRequestLuggage(@NotNull SummoningRequestData request, boolean acceptable) implements CustomPayload {
    // fear me
    public static final CustomPayload.Id<SillySummoningRequestLuggage> ID = new Id<>(FamiliarMagic.id("summoning_luggage"));

    public static final PacketCodec<RegistryByteBuf, SillySummoningRequestLuggage> PACKET_CODEC = PacketCodec.tuple(
            SummoningRequestData.PACKET_CODEC,
            SillySummoningRequestLuggage::request,
            PacketCodecs.BOOL,
            SillySummoningRequestLuggage::acceptable,
            SillySummoningRequestLuggage::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    // used as receiver on server, "C2S" part
    public static void doomOfAllServers(SillySummoningRequestLuggage lugged, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            World targetWorld = context.server().getWorld(lugged.request.tableWorldKey());
            if (targetWorld != null && targetWorld.getBlockEntity(lugged.request.tablePos()) instanceof SummoningTableBlockEntity skubert) {
                if (lugged.acceptable()) {
                    skubert.acceptAndCastSummoning(context.player());
                } else {
                    // todo - maybe something else here that's more indicative of rejection.. later
                    skubert.cancelAll();
                }
            }
        });
    }

    // used as receiver on client, "S2C" part
    // not directly what client wants because of silly silly client/common separation what a silly concept
    public static void doomOfAllClients(SillySummoningRequestLuggage lugged, PlayerEntity player) {
        if (!lugged.acceptable && lugged.request.isSameRequester(FamiliarAttachments.getRequest(player))) {
            // only remove if it's no longer acceptable and the request to cancel matches current request
            FamiliarAttachments.removeRequest(player);
        } else {
            // lotsa improvements to be done here. fade in/out around the edge of requests as warning? use nonvanilla sound event?
            FamiliarAttachments.setRequest(player, lugged.request);
            player.getWorld().playSoundFromEntity(player, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1, 1);
        }
    }
}
