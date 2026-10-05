package com.icyl2extra.event;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IcyL2HostilitySmallExtra.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TeleportCancelHandler {

    private static final double RANGE = 48.0;

    @SubscribeEvent
    public static void onEnderTeleport(EntityTeleportEvent.EnderEntity event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof Mob mob)) return;
        for (Player p : mob.level().players()) {
            if (p.distanceToSqr(mob) <= RANGE * RANGE
                    && BypassAccessoryHandler.wearsSpaceLock(p)) {
                event.setCanceled(true);
                return;
            }
        }
    }
}
