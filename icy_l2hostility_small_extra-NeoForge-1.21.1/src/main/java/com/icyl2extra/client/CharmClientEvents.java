package com.icyl2extra.client;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import com.icyl2extra.network.ScrollSelectPayload;
import com.icyl2extra.util.CharmInteractions;
import com.icyl2extra.util.CharmStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.LinkedHashMap;
import java.util.Map;

@EventBusSubscriber(modid = IcyL2HostilitySmallExtra.MODID, value = Dist.CLIENT)
public class CharmClientEvents {

    private static final int DISPLAY_TICKS = 60;
    private static long showUntilTick = -1;

    private static void pulseDisplay() {
        if (Minecraft.getInstance().level != null) {
            showUntilTick = Minecraft.getInstance().level.getGameTime() + DISPLAY_TICKS;
        }
    }

    @SubscribeEvent
    public static void onScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        if (!mc.player.isShiftKeyDown()) return;
        ItemStack charm = CharmInteractions.getHeldCharm(mc.player);
        if (charm.isEmpty() || CharmStorage.getEntryCount(charm) == 0) return;
        int delta = event.getScrollDeltaY() > 0 ? 1 : -1;
        CharmStorage.cycleSelect(charm, delta);
        PacketDistributor.sendToServer(new ScrollSelectPayload(delta));
        pulseDisplay();
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        predictShiftSwitch(event.getEntity());
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        predictShiftSwitch(event.getEntity());
    }

    private static void predictShiftSwitch(net.minecraft.world.entity.player.Player player) {
        Minecraft mc = Minecraft.getInstance();
        if (player != mc.player || mc.screen != null || !player.isShiftKeyDown()) return;
        ItemStack charm = CharmInteractions.getHeldCharm(player);
        if (charm.isEmpty() || CharmStorage.getEntryCount(charm) == 0) return;
        CharmStorage.cycleSelect(charm, 1);
        pulseDisplay();
    }

    @SubscribeEvent
    public static void onRenderHud(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) return;
        boolean active = mc.player.isShiftKeyDown() || mc.level.getGameTime() < showUntilTick;
        if (!active) return;
        ItemStack charm = CharmInteractions.getHeldCharm(mc.player);
        if (charm.isEmpty()) return;

        int size = CharmStorage.getEntryCount(charm);
        if (size == 0) return;
        ListTag stored = CharmStorage.getStoredList(charm);
        int sel = CharmStorage.getSelectedIndex(charm);
        HolderLookup.Provider registries = mc.level.registryAccess();

        var font = mc.font;
        GuiGraphics gg = event.getGuiGraphics();
        int centerX = mc.getWindow().getGuiScaledWidth() / 2;
        int centerY = mc.getWindow().getGuiScaledHeight() / 2 - 24;
        int rowH = 17;
        int radius = Math.min(2, size - 1);
        int iconX = centerX - 100;
        int textX = iconX + 20;

        for (int off = -radius; off <= radius; off++) {
            int idx = Math.floorMod(sel + off, size);
            ItemStack entry = CharmStorage.readEntry(stored.getCompound(idx), registries);
            int rowY = centerY + off * rowH;
            boolean current = off == 0;
            int distance = Math.abs(off);
            int nameColor = current ? 0xFFFFFFFF : distance == 1 ? 0xFFB5B5B5 : 0xFF777777;
            int indexColor = current ? 0xFFFFFF55 : nameColor;

            if (current) {
                gg.fill(iconX - 6, rowY - 3, centerX + 112, rowY + 15, 0x90000000);
            }

            gg.renderItem(entry, iconX, rowY - 2);
            if (current) {
                gg.renderItemDecorations(font, entry, iconX, rowY - 2);
            }

            String idxStr = (idx + 1) + "/" + size + "  ";
            gg.drawString(font, idxStr, textX, rowY + 3, indexColor);
            MutableComponent text = Component.literal("")
                    .append(entry.getHoverName()).append(" ×" + entry.getCount());
            gg.drawString(font, text, textX + font.width(idxStr), rowY + 3, nameColor);
        }
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof dev.xkmc.l2hostility.content.item.curio.misc.LootingCharm)) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        HolderLookup.Provider registries = mc.level.registryAccess();
        var list = event.getToolTip();
        int entries = CharmStorage.getEntryCount(stack);
        int total = CharmStorage.getTotalCount(stack, registries);

        if (entries == 0) {
            list.add(Component.translatable(
                    "tooltip.icy_l2hostility_small_extra.charm.empty").withStyle(ChatFormatting.GRAY));
        } else {
            ListTag stored = CharmStorage.getStoredList(stack);
            int selected = CharmStorage.getSelectedIndex(stack);
            Map<String, Group> groups = new LinkedHashMap<>();
            for (int i = 0; i < stored.size(); i++) {
                ItemStack entry = CharmStorage.readEntry(stored.getCompound(i), registries);
                String key = entry.getHoverName().getString();
                Group g = groups.get(key);
                if (g == null) {
                    g = new Group(entry.getHoverName());
                    groups.put(key, g);
                }
                g.count += entry.getCount();
                g.variants++;
                if (i == selected) g.selected = true;
            }

            list.add(Component.translatable(
                    "tooltip.icy_l2hostility_small_extra.charm.count",
                    groups.size(), total).withStyle(ChatFormatting.GOLD));
            if (Screen.hasShiftDown()) {
                for (Group g : groups.values()) {
                    MutableComponent name = Component.literal("")
                            .append(g.hoverName).append(" ×" + g.count);
                    if (g.variants > 1) {
                        name.append(Component.translatable(
                                "tooltip.icy_l2hostility_small_extra.charm.kinds", g.variants)
                                .withStyle(ChatFormatting.DARK_GRAY));
                    }
                    if (g.selected) {
                        list.add(Component.translatable(
                                "tooltip.icy_l2hostility_small_extra.charm.entry_selected",
                                name).withStyle(ChatFormatting.YELLOW));
                    } else {
                        list.add(Component.translatable(
                                "tooltip.icy_l2hostility_small_extra.charm.entry",
                                name).withStyle(ChatFormatting.GRAY));
                    }
                }
            } else {
                list.add(Component.translatable(
                        "tooltip.icy_l2hostility_small_extra.charm.hint_shift").withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        list.add(Component.translatable(
                "tooltip.icy_l2hostility_small_extra.charm.usage")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }

    private static final class Group {
        final Component hoverName;
        int count;
        int variants;
        boolean selected;

        Group(Component hoverName) {
            this.hoverName = hoverName;
        }
    }
}
