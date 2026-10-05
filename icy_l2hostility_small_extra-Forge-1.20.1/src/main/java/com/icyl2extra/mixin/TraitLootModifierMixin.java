package com.icyl2extra.mixin;

import com.icyl2extra.util.LootCollector;
import dev.xkmc.l2hostility.init.loot.TraitLootModifier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TraitLootModifier.class, remap = false)
public abstract class TraitLootModifierMixin {

    @Inject(method = "doApply", at = @At("HEAD"))
    private void icy$snapshot(ObjectArrayList<ItemStack> generatedLoot, LootContext context,
                             CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        LootCollector.snapshot(generatedLoot);
    }

    @Inject(method = "doApply", at = @At("RETURN"))
    private void icy$collect(ObjectArrayList<ItemStack> generatedLoot, LootContext context,
                             CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        LootCollector.collect(context, cir.getReturnValue());
    }
}
