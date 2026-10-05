package com.icyl2extra.util;

import dev.xkmc.l2hostility.content.item.curio.misc.LootingCharm;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CharmInteractions {

    public static ItemStack getHeldCharm(LivingEntity entity) {
        ItemStack main = entity.getMainHandItem();
        if (main.getItem() instanceof LootingCharm) return main;
        ItemStack off = entity.getOffhandItem();
        if (off.getItem() instanceof LootingCharm) return off;
        return ItemStack.EMPTY;
    }

    public static void perform(Player player, ItemStack charm, boolean shift) {
        if (player.level().isClientSide()) return;
        HolderLookup.Provider registries = player.level().registryAccess();
        if (shift) {
            int idx = CharmStorage.cycleSelect(charm, 1);
            if (idx < 0) {
                actionbar(player, Component.translatable(
                        "tooltip.icy_l2hostility_small_extra.charm.empty_action")
                        .withStyle(ChatFormatting.RED));
                return;
            }
            announceSelect(player, charm, registries);
            clickSound(player, 1.2f);
        } else {
            ItemStack selected = CharmStorage.getSelected(charm, registries);
            if (selected.isEmpty()) {
                actionbar(player, Component.translatable(
                        "tooltip.icy_l2hostility_small_extra.charm.empty_action")
                        .withStyle(ChatFormatting.RED));
                return;
            }
            int want = selected.getMaxStackSize();
            ItemStack out = CharmStorage.takeSelected(charm, want, registries);
            int requested = out.getCount();
            player.getInventory().add(out);
            int remaining = out.getCount();
            if (remaining > 0) {
                CharmStorage.addLoot(charm, out, registries);
            }
            int taken = requested - remaining;
            if (taken > 0) {
                actionbar(player, Component.translatable(
                        "tooltip.icy_l2hostility_small_extra.charm.take_action",
                        selected.getHoverName(), taken).withStyle(ChatFormatting.GREEN));
                clickSound(player, 0.8f);
            } else {
                actionbar(player, Component.translatable(
                        "tooltip.icy_l2hostility_small_extra.charm.inventory_full")
                        .withStyle(ChatFormatting.RED));
            }
        }
    }

    public static void announceSelect(Player player, ItemStack charm, HolderLookup.Provider registries) {
        ItemStack sel = CharmStorage.getSelected(charm, registries);
        if (sel.isEmpty()) return;
        int idx = CharmStorage.getSelectedIndex(charm) + 1;
        int total = CharmStorage.getEntryCount(charm);
        actionbar(player, Component.translatable(
                "tooltip.icy_l2hostility_small_extra.charm.select_action",
                sel.getHoverName(), sel.getCount(), idx, total).withStyle(ChatFormatting.YELLOW));
    }

    private static void actionbar(Player player, Component msg) {
        player.displayClientMessage(msg, true);
    }

    private static void clickSound(Player player, float pitch) {
        player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.6f, pitch);
    }
}
