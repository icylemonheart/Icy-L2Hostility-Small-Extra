package com.icyl2extra.network;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(IcyL2HostilitySmallExtra.MODID, "charm"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, ScrollSelectPacket.class,
                ScrollSelectPacket::encode, ScrollSelectPacket::decode, ScrollSelectPacket::handle);
    }
}
