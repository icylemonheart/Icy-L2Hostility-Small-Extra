package com.icyl2extra.util;

import dev.xkmc.l2hostility.content.item.curio.misc.LootingCharm;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;

public class CharmStorage {

    public static final String KEY_STORED = "Stored";
    public static final String KEY_SELECT = "Select";
    public static final String KEY_NUM = "Num";

    public static ItemStack findEquippedCharm(LivingEntity entity) {
        List<ItemStack> all = findEquippedCharms(entity);
        return all.isEmpty() ? ItemStack.EMPTY : all.get(0);
    }

    public static List<ItemStack> findEquippedCharms(LivingEntity entity) {
        List<ItemStack> result = new ArrayList<>();
        if (entity.level().isClientSide()) return result;
        var curioOpt = CuriosApi.getCuriosHelper().getEquippedCurios(entity);
        if (curioOpt.isPresent()) {
            var slots = curioOpt.resolve().orElse(null);
            if (slots != null) {
                for (int i = 0; i < slots.getSlots(); i++) {
                    ItemStack stack = slots.getStackInSlot(i);
                    if (stack.getItem() instanceof LootingCharm) result.add(stack);
                }
            }
        }
        return result;
    }

    public static void routeLoot(List<ItemStack> charms, ItemStack loot) {
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
            if (mergeTarget == null && containsSame(charm, loot)) {
                mergeTarget = charm;
            }
        }
        addLoot(mergeTarget != null ? mergeTarget : fallback, loot);
    }

    private static boolean containsSame(ItemStack charm, ItemStack loot) {
        ListTag list = getStoredList(charm);
        for (int i = 0; i < list.size(); i++) {
            if (ItemStack.isSameItemSameTags(readEntry(list.getCompound(i)), loot)) return true;
        }
        return false;
    }

    public static void addLoot(ItemStack charm, ItemStack loot) {
        if (loot.isEmpty()) return;
        CompoundTag root = charm.getOrCreateTag();
        ListTag list = getStoredList(charm);

        for (int i = 0; i < list.size(); i++) {
            ItemStack stored = readEntry(list.getCompound(i));
            if (ItemStack.isSameItemSameTags(stored, loot)) {
                stored.grow(loot.getCount());
                list.set(i, writeEntry(stored));
                root.put(KEY_STORED, list);
                clampSelect(charm);
                return;
            }
        }
        list.add(writeEntry(loot.copy()));
        root.put(KEY_STORED, list);
        clampSelect(charm);
    }

    public static ItemStack takeSelected(ItemStack charm, int amount) {
        ListTag list = getStoredList(charm);
        int idx = getSelectedIndex(charm);
        if (list.isEmpty() || amount <= 0) return ItemStack.EMPTY;
        idx = Math.min(idx, list.size() - 1);
        ItemStack stored = readEntry(list.getCompound(idx));
        int take = Math.min(amount, stored.getCount());
        ItemStack out = stored.copy();
        out.setCount(take);
        stored.shrink(take);
        if (stored.isEmpty()) {
            list.remove(idx);
        } else {
            list.set(idx, writeEntry(stored));
        }
        charm.getOrCreateTag().put(KEY_STORED, list);
        clampSelect(charm);
        return out;
    }

    public static int cycleSelect(ItemStack charm, int delta) {
        int size = getStoredList(charm).size();
        if (size == 0) {
            charm.getOrCreateTag().putInt(KEY_SELECT, 0);
            return -1;
        }
        int idx = getSelectedIndex(charm);
        idx = Math.floorMod(idx + delta, size);
        charm.getOrCreateTag().putInt(KEY_SELECT, idx);
        return idx;
    }

    public static ItemStack getSelected(ItemStack charm) {
        ListTag list = getStoredList(charm);
        if (list.isEmpty()) return ItemStack.EMPTY;
        int idx = Math.min(getSelectedIndex(charm), list.size() - 1);
        return readEntry(list.getCompound(idx));
    }

    public static int getSelectedIndex(ItemStack charm) {
        CompoundTag tag = charm.getTag();
        return tag == null ? 0 : tag.getInt(KEY_SELECT);
    }

    public static int getEntryCount(ItemStack charm) {
        return getStoredList(charm).size();
    }

    public static int getTotalCount(ItemStack charm) {
        int total = 0;
        ListTag list = getStoredList(charm);
        for (int i = 0; i < list.size(); i++) {
            total += readEntry(list.getCompound(i)).getCount();
        }
        return total;
    }

    public static ListTag getStoredList(ItemStack charm) {
        CompoundTag tag = charm.getTag();
        if (tag == null) return new ListTag();
        ListTag list = tag.getList(KEY_STORED, Tag.TAG_COMPOUND);

        boolean dirty = false;
        for (int i = list.size() - 1; i >= 0; i--) {
            CompoundTag c = list.getCompound(i);
            if (c.contains(KEY_NUM)) continue;
            ItemStack old = ItemStack.of(c);
            if (old.isEmpty() || old.getCount() <= 0) {
                list.remove(i);
            } else {
                list.set(i, writeEntry(old));
            }
            dirty = true;
        }
        if (dirty) tag.put(KEY_STORED, list);
        return list;
    }

    public static ItemStack readEntry(CompoundTag c) {
        if (c.contains(KEY_NUM)) {
            Item item = BuiltInRegistries.ITEM.get(
                    ResourceLocation.tryParse(c.getString("id")));
            ItemStack stack = new ItemStack(item, Math.max(0, c.getInt(KEY_NUM)));
            if (c.contains("tag", Tag.TAG_COMPOUND)) {
                stack.setTag(c.getCompound("tag").copy());
            }
            return stack;
        }
        return ItemStack.of(c);
    }

    private static CompoundTag writeEntry(ItemStack stack) {
        CompoundTag c = new CompoundTag();
        c.putString("id", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        c.putInt(KEY_NUM, stack.getCount());
        if (stack.getTag() != null) {
            c.put("tag", stack.getTag().copy());
        }
        return c;
    }

    private static void clampSelect(ItemStack charm) {
        int size = getStoredList(charm).size();
        int idx = getSelectedIndex(charm);
        if (size == 0) idx = 0;
        else idx = Math.min(idx, size - 1);
        charm.getOrCreateTag().putInt(KEY_SELECT, idx);
    }
}
