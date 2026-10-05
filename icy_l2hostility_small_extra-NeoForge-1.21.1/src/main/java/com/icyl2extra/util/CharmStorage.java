package com.icyl2extra.util;

import dev.xkmc.l2hostility.content.item.curio.misc.LootingCharm;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CharmStorage {

    public static final String KEY_STORED = "Stored";
    public static final String KEY_SELECT = "Select";

    public static ItemStack findEquippedCharm(LivingEntity entity) {
        List<ItemStack> all = findEquippedCharms(entity);
        return all.isEmpty() ? ItemStack.EMPTY : all.get(0);
    }

    public static List<ItemStack> findEquippedCharms(LivingEntity entity) {
        List<ItemStack> result = new ArrayList<>();
        if (entity.level().isClientSide()) return result;
        Optional<? extends top.theillusivec4.curios.api.type.capability.ICuriosItemHandler> opt =
                CuriosApi.getCuriosInventory(entity);
        opt.ifPresent(handler -> {
            IItemHandlerModifiable slots = handler.getEquippedCurios();
            for (int i = 0; i < slots.getSlots(); i++) {
                ItemStack stack = slots.getStackInSlot(i);
                if (stack.getItem() instanceof LootingCharm) result.add(stack);
            }
        });
        return result;
    }

    public static void commitEquipped(LivingEntity entity) {
        CuriosApi.getCuriosInventory(entity).ifPresent(handler -> {
            IItemHandlerModifiable slots = handler.getEquippedCurios();
            for (int i = 0; i < slots.getSlots(); i++) {
                if (slots.getStackInSlot(i).getItem() instanceof LootingCharm) {
                    slots.setStackInSlot(i, slots.getStackInSlot(i));
                }
            }
        });
    }

    public static void routeLoot(List<ItemStack> charms, ItemStack loot, HolderLookup.Provider registries) {
        if (charms.isEmpty() || loot.isEmpty()) return;
        ItemStack mergeTarget = null;
        ItemStack fallback = charms.get(0);
        int minEntries = Integer.MAX_VALUE;
        for (ItemStack charm : charms) {
            int entries = getEntryCount(charm);
            if (entries < minEntries) {
                minEntries = entries;
                fallback = charm;
            }
            if (mergeTarget == null && containsSame(charm, loot, registries)) {
                mergeTarget = charm;
            }
        }
        addLoot(mergeTarget != null ? mergeTarget : fallback, loot, registries);
    }

    private static boolean containsSame(ItemStack charm, ItemStack loot, HolderLookup.Provider registries) {
        ListTag list = getStoredList(charm);
        for (int i = 0; i < list.size(); i++) {
            if (ItemStack.isSameItemSameComponents(readEntry(list.getCompound(i), registries), loot)) return true;
        }
        return false;
    }

    public static void addLoot(ItemStack charm, ItemStack loot, HolderLookup.Provider registries) {
        if (loot.isEmpty()) return;
        ListTag list = getStoredList(charm);

        for (int i = 0; i < list.size(); i++) {
            ItemStack stored = readEntry(list.getCompound(i), registries);
            if (ItemStack.isSameItemSameComponents(stored, loot)) {
                stored.grow(loot.getCount());
                list.set(i, writeEntry(stored, registries));
                putStored(charm, list);
                clampSelect(charm);
                return;
            }
        }
        list.add(writeEntry(loot.copy(), registries));
        putStored(charm, list);
        clampSelect(charm);
    }

    public static ItemStack takeSelected(ItemStack charm, int amount, HolderLookup.Provider registries) {
        ListTag list = getStoredList(charm);
        int idx = getSelectedIndex(charm);
        if (list.isEmpty() || amount <= 0) return ItemStack.EMPTY;
        idx = Math.min(idx, list.size() - 1);
        ItemStack stored = readEntry(list.getCompound(idx), registries);
        int take = Math.min(amount, stored.getCount());
        ItemStack out = stored.copy();
        out.setCount(take);
        stored.shrink(take);
        if (stored.isEmpty()) {
            list.remove(idx);
        } else {
            list.set(idx, writeEntry(stored, registries));
        }
        putStored(charm, list);
        clampSelect(charm);
        return out;
    }

    public static int cycleSelect(ItemStack charm, int delta) {
        int size = getStoredList(charm).size();
        if (size == 0) {
            putSelect(charm, 0);
            return -1;
        }
        int idx = getSelectedIndex(charm);
        idx = Math.floorMod(idx + delta, size);
        putSelect(charm, idx);
        return idx;
    }

    public static ItemStack getSelected(ItemStack charm, HolderLookup.Provider registries) {
        ListTag list = getStoredList(charm);
        if (list.isEmpty()) return ItemStack.EMPTY;
        int idx = Math.min(getSelectedIndex(charm), list.size() - 1);
        return readEntry(list.getCompound(idx), registries);
    }

    public static int getSelectedIndex(ItemStack charm) {
        CompoundTag root = rootTag(charm);
        return root == null ? 0 : root.getInt(KEY_SELECT);
    }

    public static int getEntryCount(ItemStack charm) {
        return getStoredList(charm).size();
    }

    public static int getTotalCount(ItemStack charm, HolderLookup.Provider registries) {
        int total = 0;
        ListTag list = getStoredList(charm);
        for (int i = 0; i < list.size(); i++) {
            total += readEntry(list.getCompound(i), registries).getCount();
        }
        return total;
    }

    public static ListTag getStoredList(ItemStack charm) {
        CompoundTag root = rootTag(charm);
        return root == null ? new ListTag() : root.getList(KEY_STORED, Tag.TAG_COMPOUND);
    }

    public static ItemStack readEntry(CompoundTag c, HolderLookup.Provider registries) {
        return ItemStack.parse(registries, c).orElse(ItemStack.EMPTY);
    }

    private static CompoundTag writeEntry(ItemStack stack, HolderLookup.Provider registries) {
        return (CompoundTag) stack.save(registries);
    }

    private static CompoundTag rootTag(ItemStack charm) {
        CustomData data = charm.get(DataComponents.CUSTOM_DATA);
        return data == null ? null : data.copyTag();
    }

    private static void putStored(ItemStack charm, ListTag list) {
        CustomData.update(DataComponents.CUSTOM_DATA, charm, root -> root.put(KEY_STORED, list.copy()));
    }

    private static void putSelect(ItemStack charm, int idx) {
        CustomData.update(DataComponents.CUSTOM_DATA, charm, root -> root.putInt(KEY_SELECT, idx));
    }

    private static void clampSelect(ItemStack charm) {
        int size = getStoredList(charm).size();
        int idx = getSelectedIndex(charm);
        if (size == 0) idx = 0;
        else idx = Math.min(idx, size - 1);
        putSelect(charm, idx);
    }
}
