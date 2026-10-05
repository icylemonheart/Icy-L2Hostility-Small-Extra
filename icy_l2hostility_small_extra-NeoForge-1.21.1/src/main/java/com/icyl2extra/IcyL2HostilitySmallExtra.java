package com.icyl2extra;

import com.icyl2extra.config.HostilityInteractionConfig;
import com.icyl2extra.item.ModCreativeTabs;
import com.icyl2extra.item.ModItems;
import com.icyl2extra.network.NetworkHandler;
import com.icyl2extra.pandora.ModRecipes;
import com.icyl2extra.pandora.PandoraCharmConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(IcyL2HostilitySmallExtra.MODID)
public class IcyL2HostilitySmallExtra {

    public static final String MODID = "icy_l2hostility_small_extra";

    public IcyL2HostilitySmallExtra(IEventBus bus, ModContainer modContainer) {
        ModItems.ITEMS.register(bus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(bus);
        ModRecipes.RECIPE_SERIALIZERS.register(bus);

        bus.addListener(NetworkHandler::register);

        modContainer.registerConfig(ModConfig.Type.COMMON,
                HostilityInteractionConfig.SPEC,
                "icy/icy_l2hostility_small_extra-common.toml");

        modContainer.registerConfig(ModConfig.Type.COMMON,
                PandoraCharmConfig.SPEC,
                "icy/icy_pandora_charm-common.toml");
    }
}
