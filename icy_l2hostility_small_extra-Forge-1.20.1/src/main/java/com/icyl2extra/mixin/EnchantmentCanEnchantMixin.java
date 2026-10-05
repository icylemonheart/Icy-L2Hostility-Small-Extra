package com.icyl2extra.mixin;

import com.icyl2extra.util.SplitSuppressTargets;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentCanEnchantMixin {

    private static final ResourceLocation SPLIT_SUPPRESSOR_ID =
            new ResourceLocation("l2hostility", "split_suppressor");

    @Inject(method = "canEnchant(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void icy$splitSuppressCanEnchantWeapon(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey((Enchantment) (Object) this);
        if (SPLIT_SUPPRESSOR_ID.equals(id) && SplitSuppressTargets.isWeapon(stack)) {
            cir.setReturnValue(true);
        }
    }
}
