package com.icyl2extra.client;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import com.icyl2extra.config.HostilityInteractionConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = IcyL2HostilitySmallExtra.MODID, value = Dist.CLIENT)
public class InteractionTooltipHandler {

    private static final ResourceLocation BOTTLE_OF_SANITY =
            ResourceLocation.fromNamespaceAndPath("l2hostility", "bottle_of_sanity");
    private static final ResourceLocation HOSTILITY_ESSENCE =
            ResourceLocation.fromNamespaceAndPath("l2hostility", "hostility_essence");

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem());
        if (id == null) return;

        if (id.equals(BOTTLE_OF_SANITY) && HostilityInteractionConfig.BOTTLE_OF_SANITY_ENABLED.get()) {
            event.getToolTip().add(Component.translatable(
                    "tooltip.icy_l2hostility_small_extra.bottle_of_sanity")
                    .withStyle(ChatFormatting.YELLOW));
        } else if (id.equals(HOSTILITY_ESSENCE) && HostilityInteractionConfig.HOSTILITY_ESSENCE_ENABLED.get()) {
            event.getToolTip().add(Component.translatable(
                    "tooltip.icy_l2hostility_small_extra.hostility_essence",
                    HostilityInteractionConfig.ESSENCE_LEVEL_BONUS.get())
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }
    }
}
