package com.icyl2extra.item;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IcyL2HostilitySmallExtra.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB =
            CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.icy_l2hostility_small_extra.main"))
                    .icon(() -> new ItemStack(ModItems.BEYOND_PHYSICS.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.BEYOND_PHYSICS.get());
                        output.accept(ModItems.SEALED_MAGIC.get());
                        output.accept(ModItems.SPACE_LOCK.get());
                        output.accept(ModItems.NO_MORE_IMMORTAL.get());
                        output.accept(ModItems.FUSION_CHARM.get());
                    })
                    .build());
}
