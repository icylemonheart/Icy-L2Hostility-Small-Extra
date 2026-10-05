package com.icyl2extra.event;

import com.icylib.damage.DamageTypeHelper;
import com.icyl2extra.item.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;
import top.theillusivec4.curios.api.CuriosApi;

public class BypassAccessoryHandler {

    public static boolean wearsBeyondPhysics(DamageSource source) {
        if (!(source.getEntity() instanceof LivingEntity attacker)) return false;
        if (!DamageTypeHelper.isPhysicalDamage(source)) return false;
        return hasCurioEquipped(attacker, ModItems.BEYOND_PHYSICS.get());
    }

    public static boolean wearsSealedMagic(DamageSource source) {
        if (!(source.getEntity() instanceof LivingEntity attacker)) return false;
        if (!(DamageTypeHelper.isMagicDamage(source) || DamageTypeHelper.isElementalDamage(source)
                || source.is(Tags.DamageTypes.IS_MAGIC))) return false;
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
        var opt = CuriosApi.getCuriosInventory(entity);
        if (opt.isPresent()) {
            return !opt.get().findCurios(stack -> stack.is(item)).isEmpty();
        }
        return false;
    }
}
