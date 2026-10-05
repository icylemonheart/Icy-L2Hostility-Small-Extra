package com.icyl2extra.pandora;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IcyL2HostilitySmallExtra.MODID)
public final class CharmLevelTooltipHandler {

    private static final TagKey<Item> ATTRIBUTE_CHARMS = TagKey.create(
            Registries.ITEM, new ResourceLocation("curseofpandora", "attribute_charms"));

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (event.getToolTip().isEmpty() || !event.getItemStack().is(ATTRIBUTE_CHARMS)) {
            return;
        }

        String suffix = CharmLevelData.levelSuffix(CharmLevelData.getLevel(event.getItemStack()));
        if (suffix.isEmpty()) {
            return;
        }

        Component name = event.getToolTip().get(0);
        MutableComponent updated = name.copy()
                .append(Component.literal(suffix).withStyle(ChatFormatting.GOLD));
        event.getToolTip().set(0, updated);
    }

    private CharmLevelTooltipHandler() {
    }
}
