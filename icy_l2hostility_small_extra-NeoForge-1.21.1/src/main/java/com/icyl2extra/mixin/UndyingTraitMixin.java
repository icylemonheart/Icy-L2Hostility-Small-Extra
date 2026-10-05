package com.icyl2extra.mixin;

import com.icyl2extra.event.BypassAccessoryHandler;
import dev.xkmc.l2hostility.content.traits.legendary.UndyingTrait;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = UndyingTrait.class, remap = false)
public abstract class UndyingTraitMixin {

    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void icy$cancelUndying(int level, LivingEntity entity, LivingDeathEvent event, CallbackInfo ci) {
        if (entity.level().isClientSide()) return;
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof LivingEntity attacker
                && BypassAccessoryHandler.wearsNoMoreImmortal(attacker)) {
            ci.cancel();
        }
    }
}
