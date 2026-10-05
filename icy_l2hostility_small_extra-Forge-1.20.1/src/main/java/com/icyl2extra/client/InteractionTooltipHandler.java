package com.icyl2extra.client;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import com.icyl2extra.config.HostilityInteractionConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = IcyL2HostilitySmallExtra.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class InteractionTooltipHandler {

    private static final ResourceLocation BOTTLE_OF_SANITY =
            new ResourceLocation("l2hostility", "bottle_of_sanity");
    private static final ResourceLocation HOSTILITY_ESSENCE =
            new ResourceLocation("l2hostility", "hostility_essence");

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
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
