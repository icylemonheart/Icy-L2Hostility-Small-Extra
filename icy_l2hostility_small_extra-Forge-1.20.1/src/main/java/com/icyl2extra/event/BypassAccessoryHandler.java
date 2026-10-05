package com.icyl2extra.event;

import com.icylib.damage.DamageTypeHelper;
import com.icyl2extra.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

public class BypassAccessoryHandler {

    private static final TagKey<DamageType> FORGE_IS_MAGIC =
            TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("forge", "is_magic"));

    public static boolean wearsBeyondPhysics(DamageSource source) {
        if (!(source.getEntity() instanceof LivingEntity attacker)) return false;
        if (!DamageTypeHelper.isPhysicalDamage(source)) return false;
        return hasCurioEquipped(attacker, ModItems.BEYOND_PHYSICS.get());
    }

    public static boolean wearsSealedMagic(DamageSource source) {
        if (!(source.getEntity() instanceof LivingEntity attacker)) return false;
        if (!(DamageTypeHelper.isMagicDamage(source) || DamageTypeHelper.isElementalDamage(source)
                || source.is(FORGE_IS_MAGIC))) return false;
        return hasCurioEquipped(attacker, ModItems.SEALED_MAGIC.get());
    }

    public static boolean wearsSpaceLock(LivingEntity entity) {
        return hasCurioEquipped(entity, ModItems.SPACE_LOCK.get());
    }

    public static boolean wearsNoMoreImmortal(LivingEntity attacker) {
        return hasCurioEquipped(attacker, ModItems.NO_MORE_IMMORTAL.get());
    }

    private static boolean hasCurioEquipped(LivingEntity entity, Item item) {
        if (entity.level().isClientSide()) return false;
        var curioOpt = CuriosApi.getCuriosHelper().getEquippedCurios(entity);
        if (curioOpt.isPresent()) {
            var slots = curioOpt.resolve().orElse(null);
            if (slots == null) return false;
            for (int i = 0; i < slots.getSlots(); i++) {
                ItemStack stack = slots.getStackInSlot(i);
                if (stack.is(item)) return true;
            }
        }
        return false;
    }
}
