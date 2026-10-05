package com.icyl2extra.mixin;

import com.icyl2extra.event.BypassAccessoryHandler;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2hostility.content.traits.goals.EnderTrait;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EnderTrait.class, remap = false)
public abstract class EnderTraitMixin {

    @Inject(method = "onAttackedByOthers", at = @At("HEAD"), cancellable = true)
    private void icy$cancelEnderDodge(int level, LivingEntity entity, DamageData.Attack event,
                                       CallbackInfoReturnable<Boolean> cir) {
        if (entity.level().isClientSide()) return;
        if (event.getSource().getEntity() instanceof LivingEntity attacker
                && BypassAccessoryHandler.wearsSpaceLock(attacker)) {
            cir.setReturnValue(false);
        }
    }
}
