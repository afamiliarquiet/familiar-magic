package io.github.afamiliarquiet.familiar_magic.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class FamiliarClientPacketeering {
    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(SillySummoningRequestLuggage.ID, ((lugged, context) -> context.client().execute(() -> {
           SillySummoningRequestLuggage.doomOfAllClients(lugged, context.player());
        })));
    }
}
