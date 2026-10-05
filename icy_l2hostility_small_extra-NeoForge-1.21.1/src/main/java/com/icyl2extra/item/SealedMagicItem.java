package com.icyl2extra.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SealedMagicItem extends BypassCurioItem {

    private static final String KEY = "item.icy_l2hostility_small_extra.sealed_magic";

    public SealedMagicItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("『 ").withStyle(ChatFormatting.DARK_PURPLE)
                .append(Component.translatable(KEY + ".title").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD))
                .append(Component.literal(" 』").withStyle(ChatFormatting.DARK_PURPLE)));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable(KEY + ".label").withStyle(ChatFormatting.GOLD)
                .append(Component.translatable(KEY + ".desc").withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable(KEY + ".include_1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(KEY + ".include_2").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(KEY + ".include_3").withStyle(ChatFormatting.GRAY));
    }
}
