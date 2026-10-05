package com.icyl2extra.event;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = IcyL2HostilitySmallExtra.MODID)
public class SplitSuppressionHandler {

    private static final ResourceLocation SPLIT_SUPPRESSOR_ID =
            ResourceLocation.fromNamespaceAndPath("l2hostility", "split_suppressor");
    private static final ResourceLocation SPLIT_TRAIT_ID =
            ResourceLocation.fromNamespaceAndPath("l2hostility", "split");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide()) return;
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
        if (attacker == target) return;
        var capOpt = LHMiscs.MOB.type().getExisting(target);
        if (capOpt.isEmpty()) return;

        var registry = target.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var key = ResourceKey.create(Registries.ENCHANTMENT, SPLIT_SUPPRESSOR_ID);
        Holder<Enchantment> enchantment = registry.get(key).orElse(null);
        if (enchantment == null) return;
        if (!heldHasEnchantment(enchantment, attacker.getMainHandItem())
                && !heldHasEnchantment(enchantment, attacker.getOffhandItem())) {
            return;
        }

        MobTraitCap cap = capOpt.get();
        MobTrait splitTrait = null;
        for (MobTrait trait : cap.traits.keySet()) {
            if (SPLIT_TRAIT_ID.equals(trait.getRegistryName())) {
                splitTrait = trait;
                break;
            }
        }
        if (splitTrait == null) return;
        cap.removeTrait(splitTrait);
        cap.syncToClient(target);
        if (target instanceof Slime slime) {
            slime.addTag("SuppressSplit");
        }
    }

    private static boolean heldHasEnchantment(Holder<Enchantment> enchantment, ItemStack stack) {
        if (stack.isEmpty()) return false;
        ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
        return enchantments != null && enchantments.getLevel(enchantment) > 0;
    }
}
