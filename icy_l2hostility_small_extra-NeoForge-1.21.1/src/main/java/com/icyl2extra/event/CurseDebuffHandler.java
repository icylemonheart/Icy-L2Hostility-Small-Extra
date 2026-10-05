package com.icyl2extra.event;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import dev.xkmc.l2complements.init.registrate.LCEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = IcyL2HostilitySmallExtra.MODID)
public class CurseDebuffHandler {

    private static final int CURSE_SECONDS = 10;

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (event.getEntity().level().isClientSide()) return;
        var source = event.getSource();
        if (!(source.getEntity() instanceof LivingEntity attacker)) return;
        if (!BypassAccessoryHandler.wearsNoMoreImmortal(attacker)) return;
        LivingEntity target = event.getEntity();
        target.addEffect(new MobEffectInstance(LCEffects.CURSE, CURSE_SECONDS * 20));
    }
}
