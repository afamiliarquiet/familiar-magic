package io.github.afamiliarquiet.familiar_magic.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class FamiliarPacketeering {
    public static void mrwRegisterPayloadHandlersEvent(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1"); // 1, i guess

        registrar.playToServer(
                FocusPayload.TYPE,
                FocusPayload.STREAM_CODEC,
                FocusPayload::focusEater
        );

        registrar.playToClient(
                HattedPayload.TYPE,
                HattedPayload.STREAM_CODEC,
                HattedPayload::hattedOhILoveHatted
        );
    }
}
