package com.icyl2extra.mixin.pandora;

import com.google.common.collect.Multimap;
import com.icyl2extra.pandora.CharmLevelData;
import dev.xkmc.curseofpandora.content.pandora.AttributeItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.UUID;

@Mixin(value = AttributeItem.class, remap = false)
public abstract class AttributeItemMixin {

    @Shadow
    private AttributeItem.AttributeEntry[] entries;

    @Inject(
            method = "getAttributeModifiers(Ltop/theillusivec4/curios/api/SlotContext;Ljava/util/UUID;Lnet/minecraft/world/item/ItemStack;)Lcom/google/common/collect/Multimap;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void icy$scaleWornModifiers(SlotContext context, UUID uuid, ItemStack stack,
                                        CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> cir) {
        if (CharmLevelData.getLevel(stack) > 0) {
            cir.setReturnValue(CharmLevelData.buildScaled(this.entries, uuid, stack));
        }
    }

    @Redirect(
            method = "getAttributesTooltip(Ljava/util/List;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/xkmc/curseofpandora/content/pandora/AttributeItem;getAttributeModifiers(Ljava/util/UUID;)Lcom/google/common/collect/Multimap;"
            )
    )
    private Multimap<Attribute, AttributeModifier> icy$scaleTooltipModifiers(
            AttributeItem self, UUID uuid, List<Component> tooltips, ItemStack stack) {
        return CharmLevelData.buildScaled(this.entries, uuid, stack);
    }
}
