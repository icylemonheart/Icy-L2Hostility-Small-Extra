package com.icyl2extra.mixin;

import com.icyl2extra.pandora.PandoraCharmConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraftforge.registries.ForgeRegistries;

@Mixin(AttributeInstance.class)
public class DamageReductionCapMixin {

    @Shadow
    @Final
    private Attribute attribute;

    @Unique
    private static volatile Attribute icyl2extra$cachedDamageReduction;

    @Inject(method = "getValue", at = @At("RETURN"), cancellable = true)
    private void icyl2extra$capDamageReduction(CallbackInfoReturnable<Double> cir) {
        if (this.attribute != icyl2extra$target()) {
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

    @Unique
    private static Attribute icyl2extra$target() {
        Attribute attr = icyl2extra$cachedDamageReduction;
        if (attr == null) {
            attr = ForgeRegistries.ATTRIBUTES.getValue(
                    new ResourceLocation("l2damagetracker", "damage_reduction"));
            if (attr != null) {
                icyl2extra$cachedDamageReduction = attr;
            }
        }
        return attr;
    }
}
