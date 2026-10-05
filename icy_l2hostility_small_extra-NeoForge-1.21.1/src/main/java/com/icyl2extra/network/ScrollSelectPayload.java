package com.icyl2extra.network;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import com.icyl2extra.util.CharmInteractions;
import com.icyl2extra.util.CharmStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ScrollSelectPayload(int delta) implements CustomPacketPayload {

    public static final Type<ScrollSelectPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(IcyL2HostilitySmallExtra.MODID, "scroll_select"));

    public static final StreamCodec<FriendlyByteBuf, ScrollSelectPayload> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> buf.writeVarInt(packet.delta),
            buf -> new ScrollSelectPayload(buf.readVarInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ScrollSelectPayload packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                ItemStack held = CharmInteractions.getHeldCharm(player);
                if (held.isEmpty() || CharmStorage.getEntryCount(held) == 0) return;
                CharmStorage.cycleSelect(held, packet.delta);
            }
        });
    }
}
