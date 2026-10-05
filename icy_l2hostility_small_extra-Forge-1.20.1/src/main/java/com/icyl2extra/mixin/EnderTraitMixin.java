package com.icyl2extra.mixin;

import com.icyl2extra.event.BypassAccessoryHandler;
import dev.xkmc.l2hostility.content.traits.goals.EnderTrait;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EnderTrait.class, remap = false)
public abstract class EnderTraitMixin {

    @Inject(method = "onAttackedByOthers", at = @At("HEAD"), cancellable = true)
    private void icy$cancelEnderDodge(int level, LivingEntity entity, LivingAttackEvent event, CallbackInfo ci) {
        if (entity.level().isClientSide()) return;
        if (event.getSource().getEntity() instanceof LivingEntity attacker
                && BypassAccessoryHandler.wearsSpaceLock(attacker)) {
            ci.cancel();
        }
    }
}
