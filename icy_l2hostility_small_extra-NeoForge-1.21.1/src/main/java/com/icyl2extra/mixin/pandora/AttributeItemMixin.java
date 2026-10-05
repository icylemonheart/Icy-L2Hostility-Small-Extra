package com.icyl2extra.mixin.pandora;

import com.google.common.collect.Multimap;
import com.icyl2extra.pandora.CharmLevelData;
import dev.xkmc.curseofpandora.content.pandora.AttributeItem;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotContext;

@Mixin(value = AttributeItem.class, remap = false)
public abstract class AttributeItemMixin {

    @Shadow
    private AttributeItem.AttributeEntry[] entries;

    @Inject(
            method = "getAttributeModifiers(Ltop/theillusivec4/curios/api/SlotContext;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/item/ItemStack;)Lcom/google/common/collect/Multimap;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void icy$scaleModifiers(SlotContext context, ResourceLocation id, ItemStack stack,
                                    CallbackInfoReturnable<Multimap<Holder<Attribute>, AttributeModifier>> cir) {
        if (CharmLevelData.getLevel(stack) > 0) {
            cir.setReturnValue(CharmLevelData.buildScaled(this.entries, id, stack));
        }
    }
}
