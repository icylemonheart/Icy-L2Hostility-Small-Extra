package com.icyl2extra.mixin;

import com.icyl2extra.event.BypassAccessoryHandler;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public abstract class DamageSourceBypassTagsMixin {

    @Inject(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("HEAD"), cancellable = true)
    private void icy$accessoryBypassDamageTags(TagKey<DamageType> tag, CallbackInfoReturnable<Boolean> cir) {
        DamageSource self = (DamageSource) (Object) this;
        if (tag == DamageTypeTags.BYPASSES_ARMOR) {
            if (BypassAccessoryHandler.wearsBeyondPhysics(self)) {
                cir.setReturnValue(true);
            }
        } else if (tag == DamageTypeTags.BYPASSES_EFFECTS
                || tag == DamageTypeTags.BYPASSES_RESISTANCE
                || tag == DamageTypeTags.BYPASSES_ENCHANTMENTS) {
            if (BypassAccessoryHandler.wearsSealedMagic(self)
                    || BypassAccessoryHandler.wearsBeyondPhysics(self)) {
                cir.setReturnValue(true);
            }
        }
    }
}
