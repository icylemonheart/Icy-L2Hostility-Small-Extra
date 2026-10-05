package com.icyl2extra.event;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import com.icyl2extra.config.HostilityInteractionConfig;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = IcyL2HostilitySmallExtra.MODID)
public class HostilityItemInteractionHandler {

    private static final ResourceLocation BOTTLE_OF_SANITY =
            ResourceLocation.fromNamespaceAndPath("l2hostility", "bottle_of_sanity");
    private static final ResourceLocation HOSTILITY_ESSENCE =
            ResourceLocation.fromNamespaceAndPath("l2hostility", "hostility_essence");

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getTarget() instanceof LivingEntity target)) return;
        if (target instanceof Player) return;

        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId == null) return;

        boolean isBottle = itemId.equals(BOTTLE_OF_SANITY)
                && HostilityInteractionConfig.BOTTLE_OF_SANITY_ENABLED.get();
        boolean isEssence = itemId.equals(HOSTILITY_ESSENCE)
                && HostilityInteractionConfig.HOSTILITY_ESSENCE_ENABLED.get();
        if (!isBottle && !isEssence) return;
        if (!LHMiscs.MOB.type().isProper(target)) return;

        MobTraitCap cap = LHMiscs.MOB.type().getOrCreate(target);
        boolean success;
        if (isBottle) {
            success = cap.reinit(target, 0, false);
        } else {
            int bonus = HostilityInteractionConfig.ESSENCE_LEVEL_BONUS.get();
            cap.setLevel(target, cap.getLevel() + bonus);
            cap.syncToClient(target);
            success = true;
        }
        if (!success) return;

        Player player = event.getEntity();
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        player.swing(event.getHand());
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
