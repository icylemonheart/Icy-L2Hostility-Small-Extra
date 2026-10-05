package com.icyl2extra.mixin;

import com.icyl2extra.pandora.PandoraCharmConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AttributeInstance.class, remap = false)
public class DamageReductionCapMixin {

    @Shadow(remap = false)
    @Final
    private net.minecraft.core.Holder<Attribute> attribute;

    @Inject(method = "getValue", at = @At("RETURN"), cancellable = true, remap = false)
    private void icyl2extra$capDamageReduction(CallbackInfoReturnable<Double> cir) {
        if (!attribute.is(ResourceLocation.fromNamespaceAndPath("l2damagetracker", "damage_reduction"))) {
            return;
        }
        if (!PandoraCharmConfig.SPEC.isLoaded()) {
            return;
        }
        double floor = 1.0D - PandoraCharmConfig.DAMAGE_REDUCTION_CAP.get();
        if (cir.getReturnValue() < floor) {
            cir.setReturnValue(floor);
        }
    }
}
