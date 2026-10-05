package com.icyl2extra;

import com.icyl2extra.config.HostilityInteractionConfig;
import com.icyl2extra.event.BypassAccessoryHandler;
import com.icyl2extra.item.ModCreativeTabs;
import com.icyl2extra.item.ModItems;
import com.icyl2extra.network.NetworkHandler;
import com.icyl2extra.pandora.ModRecipes;
import com.icyl2extra.pandora.PandoraCharmConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(IcyL2HostilitySmallExtra.MODID)
public class IcyL2HostilitySmallExtra {

    public static final String MODID = "icy_l2hostility_small_extra";

    public IcyL2HostilitySmallExtra() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(bus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(bus);

        ModRecipes.RECIPE_SERIALIZERS.register(bus);

        NetworkHandler.register();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,
                HostilityInteractionConfig.SPEC,
                "icy/icy_l2hostility_small_extra-common.toml");

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,
                PandoraCharmConfig.SPEC,
                "icy/icy_pandora_charm-common.toml");

        MinecraftForge.EVENT_BUS.register(new BypassAccessoryHandler());
    }
}
