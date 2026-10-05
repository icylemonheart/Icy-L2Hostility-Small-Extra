package com.icyl2extra.network;

import com.icyl2extra.util.CharmInteractions;
import com.icyl2extra.util.CharmStorage;
import dev.xkmc.l2hostility.content.item.curio.misc.LootingCharm;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ScrollSelectPacket {

    private final int delta;

    public ScrollSelectPacket(int delta) {
        this.delta = delta;
    }

    public ScrollSelectPacket(FriendlyByteBuf buf) {
        this.delta = buf.readVarInt();
    }

    public static ScrollSelectPacket decode(FriendlyByteBuf buf) {
        return new ScrollSelectPacket(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(delta);
    }

    public static void handle(ScrollSelectPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            ItemStack held = CharmInteractions.getHeldCharm(player);
            if (held.isEmpty() || CharmStorage.getEntryCount(held) == 0) return;
            CharmStorage.cycleSelect(held, msg.delta);
        });
        context.setPacketHandled(true);
    }
}
