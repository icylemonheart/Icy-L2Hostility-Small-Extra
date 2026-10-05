package com.icyl2extra.event;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import com.icyl2extra.util.CharmInteractions;
import dev.xkmc.l2hostility.content.item.curio.misc.LootingCharm;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IcyL2HostilitySmallExtra.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CharmEventHandler {

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        handleInteract(event);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        handleInteract(event);
    }

    private static void handleInteract(PlayerInteractEvent event) {
        if (!(event.getItemStack().getItem() instanceof LootingCharm)) return;
        event.setCanceled(true);
        if (!event.getLevel().isClientSide()) {
            CharmInteractions.perform(event.getEntity(), event.getItemStack(), event.getEntity().isShiftKeyDown());
        }
    }
}

