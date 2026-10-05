package com.icyl2extra.event;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import com.icyl2extra.util.CharmInteractions;
import dev.xkmc.l2hostility.content.item.curio.misc.LootingCharm;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = IcyL2HostilitySmallExtra.MODID)
public class CharmEventHandler {

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        handleInteract(event);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        handleInteract(event);
    }

    private static <T extends PlayerInteractEvent & ICancellableEvent> void handleInteract(T event) {
        if (!(event.getItemStack().getItem() instanceof LootingCharm)) return;
        event.setCanceled(true);
        if (!event.getLevel().isClientSide()) {
            CharmInteractions.perform(event.getEntity(), event.getItemStack(),
                    event.getEntity().isShiftKeyDown());
        }
    }
}
