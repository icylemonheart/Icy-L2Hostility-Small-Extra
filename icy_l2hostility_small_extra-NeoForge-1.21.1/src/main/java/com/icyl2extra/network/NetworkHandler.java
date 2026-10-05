package com.icyl2extra.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class NetworkHandler {

    private NetworkHandler() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                ScrollSelectPayload.TYPE,
                ScrollSelectPayload.STREAM_CODEC,
                ScrollSelectPayload::handle);
    }
}
