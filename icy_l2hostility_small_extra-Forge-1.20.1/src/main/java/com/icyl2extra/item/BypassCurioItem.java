package com.icyl2extra.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;

public abstract class BypassCurioItem extends Item {

    public BypassCurioItem(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return CuriosApi.createCurioProvider(new ICurio() {
            @Override
            public ItemStack getStack() {
                return stack;
            }

            @Override
            public boolean canEquip(SlotContext slotContext) {
                return true;
            }

            @Override
            public boolean canEquipFromUse(SlotContext slotContext) {
                return true;
            }

            @Override
            public boolean canUnequip(SlotContext slotContext) {
                return true;
            }
        });
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
