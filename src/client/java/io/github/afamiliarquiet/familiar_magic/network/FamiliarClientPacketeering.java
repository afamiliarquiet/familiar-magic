package io.github.afamiliarquiet.familiar_magic.network;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import io.github.afamiliarquiet.familiar_magic.data.SummoningRequestData;
import io.github.afamiliarquiet.familiar_magic.gooey.FamiliarClientScreenery;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class FamiliarClientPacketeering {
    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(SillySummoningRequestLuggage.ID, ((lugged, context) -> context.client().execute(() -> {
            if (lugged.acceptable()) {
                SummoningRequestData request = FamiliarAttachments.getRequest(context.player());
                if (request != null) {
                    ClientPlayNetworking.send(new SillySummoningRequestLuggage(request, false));
                }
                FamiliarClientScreenery.SUMMONING_REQUEST_RENDER_LAYER.reset();
            }
            SillySummoningRequestLuggage.doomOfAllClients(lugged, context.player());
        })));
    }
}
