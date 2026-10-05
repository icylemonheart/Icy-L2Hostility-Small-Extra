package com.icyl2extra.item;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import com.icyl2extra.pandora.FusionCharmItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, IcyL2HostilitySmallExtra.MODID);

    public static final DeferredHolder<Item, BeyondPhysicsItem> BEYOND_PHYSICS =
            ITEMS.register("beyond_physics", () -> new BeyondPhysicsItem(
                    new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));

    public static final DeferredHolder<Item, SealedMagicItem> SEALED_MAGIC =
            ITEMS.register("sealed_magic", () -> new SealedMagicItem(
                    new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));

    public static final DeferredHolder<Item, SpaceLockItem> SPACE_LOCK =
            ITEMS.register("space_lock", () -> new SpaceLockItem(
                    new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));

    public static final DeferredHolder<Item, NoMoreImmortalItem> NO_MORE_IMMORTAL =
            ITEMS.register("no_more_immortal", () -> new NoMoreImmortalItem(
                    new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));

    public static final DeferredHolder<Item, FusionCharmItem> FUSION_CHARM =
            ITEMS.register("fusion_charm", () -> new FusionCharmItem(
                    new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));
}
