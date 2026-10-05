package com.icyl2extra.event;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = IcyL2HostilitySmallExtra.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SplitSuppressionHandler {

    private static final ResourceLocation SPLIT_SUPPRESSOR_ID =
            new ResourceLocation("l2hostility", "split_suppressor");
    private static final ResourceLocation SPLIT_TRAIT_ID =
            new ResourceLocation("l2hostility", "split");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide()) return;
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
        if (attacker == target) return;
        if (!MobTraitCap.HOLDER.isProper(target)) return;

        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(SPLIT_SUPPRESSOR_ID);
        if (enchantment == null) return;
        if (!heldHasEnchantment(enchantment, attacker.getMainHandItem())
                && !heldHasEnchantment(enchantment, attacker.getOffhandItem())) {
            return;
        }

        MobTraitCap cap = MobTraitCap.HOLDER.get(target);
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

    private static boolean heldHasEnchantment(Enchantment enchantment, ItemStack stack) {
        return !stack.isEmpty() && stack.getAllEnchantments().containsKey(enchantment);
    }
}
