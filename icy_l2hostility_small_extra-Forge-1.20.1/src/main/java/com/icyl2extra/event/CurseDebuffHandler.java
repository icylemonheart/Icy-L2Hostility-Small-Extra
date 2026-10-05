package com.icyl2extra.event;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = IcyL2HostilitySmallExtra.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CurseDebuffHandler {

    private static final int CURSE_SECONDS = 10;

    private static final ResourceLocation CURSE_ID =
            new ResourceLocation("l2complements", "curse");

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        var source = event.getSource();
        if (!(source.getEntity() instanceof LivingEntity attacker)) return;
        if (!BypassAccessoryHandler.wearsNoMoreImmortal(attacker)) return;
        LivingEntity target = event.getEntity();
        MobEffect curse = ForgeRegistries.MOB_EFFECTS.getValue(CURSE_ID);
        if (curse != null) {
            target.addEffect(new MobEffectInstance(curse, CURSE_SECONDS * 20));
        }
    }
}
