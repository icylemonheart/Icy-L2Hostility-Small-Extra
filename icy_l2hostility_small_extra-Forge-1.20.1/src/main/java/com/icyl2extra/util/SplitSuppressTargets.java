package com.icyl2extra.util;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;

public final class SplitSuppressTargets {

    private static final Class<?> TACZ_IGUN;

    static {
        Class<?> c = null;
        try {
            c = Class.forName("com.tacz.guns.api.item.IGun");
        } catch (ClassNotFoundException e) {
            c = null;
        }
        TACZ_IGUN = c;
    }

    private SplitSuppressTargets() {
    }

    public static boolean isWeapon(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof BlockItem) return false;
        if (item instanceof TieredItem) return true;
        if (item instanceof BowItem || item instanceof CrossbowItem || item instanceof TridentItem) {
            return true;
        }
        if (TACZ_IGUN != null && TACZ_IGUN.isInstance(item)) return true;
        var modifiers = item.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND)
                .get(Attributes.ATTACK_DAMAGE);
        return modifiers != null && !modifiers.isEmpty();
    }
}
