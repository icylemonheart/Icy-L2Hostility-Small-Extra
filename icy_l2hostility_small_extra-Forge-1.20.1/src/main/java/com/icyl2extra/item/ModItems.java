package com.icyl2extra.item;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import com.icyl2extra.pandora.FusionCharmItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, IcyL2HostilitySmallExtra.MODID);

    public static final RegistryObject<BeyondPhysicsItem> BEYOND_PHYSICS =
            ITEMS.register("beyond_physics", () -> new BeyondPhysicsItem(
                    new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));

    public static final RegistryObject<SealedMagicItem> SEALED_MAGIC =
            ITEMS.register("sealed_magic", () -> new SealedMagicItem(
                    new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));

    public static final RegistryObject<SpaceLockItem> SPACE_LOCK =
            ITEMS.register("space_lock", () -> new SpaceLockItem(
                    new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));

    public static final RegistryObject<NoMoreImmortalItem> NO_MORE_IMMORTAL =
            ITEMS.register("no_more_immortal", () -> new NoMoreImmortalItem(
                    new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));

    public static final RegistryObject<FusionCharmItem> FUSION_CHARM =
            ITEMS.register("fusion_charm", () -> new FusionCharmItem(
                    new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));
}
