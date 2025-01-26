package io.github.afamiliarquiet.familiar_magic.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class FamiliarPacketeering {
    public static void initialize() {
        PayloadTypeRegistry.playC2S().register(C2SFocusLuggage.ID, C2SFocusLuggage.PACKET_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(C2SFocusLuggage.ID, C2SFocusLuggage::focusEater);

        PayloadTypeRegistry.playS2C().register(SillySummoningRequestLuggage.ID, SillySummoningRequestLuggage.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SillySummoningRequestLuggage.ID, SillySummoningRequestLuggage.PACKET_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SillySummoningRequestLuggage.ID, SillySummoningRequestLuggage::doomOfAllServers);
    }
}
