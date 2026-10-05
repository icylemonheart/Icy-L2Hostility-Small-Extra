package com.icyl2extra.util;

import com.icyl2extra.config.HostilityInteractionConfig;
import dev.xkmc.l2hostility.compat.curios.CurioCompat;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.init.data.LHConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class LootCollector {

    private LootCollector() {
    }

    private static final ThreadLocal<List<ItemStack>> SNAPSHOT = new ThreadLocal<>();

    private static Item nidhoggur;

    public static void snapshot(ObjectArrayList<ItemStack> generatedLoot) {
        SNAPSHOT.set(new ArrayList<>(generatedLoot));
    }

    public static void collect(LootContext context, ObjectArrayList<ItemStack> after) {
        List<ItemStack> before = SNAPSHOT.get();
        SNAPSHOT.remove();
        if (before == null) return;
        doCollect(context, before, after);
    }

    private static void doCollect(LootContext context, List<ItemStack> before, ObjectArrayList<ItemStack> after) {
        if (!HostilityInteractionConfig.COLLECT_LOOT_INTO_CHARM.get()) return;

        List<ItemStack> added = new ArrayList<>(after);
        for (ItemStack original : before) {
            Iterator<ItemStack> it = added.iterator();
            while (it.hasNext()) {
                if (ItemStack.isSameItemSameTags(original, it.next())) {
                    it.remove();
                    break;
                }
            }
        }
        if (added.isEmpty()) return;

        Object killer = context.getParamOrNull(LootContextParams.KILLER_ENTITY);
        if (!(killer instanceof Player player)) return;
        List<ItemStack> charms = CharmStorage.findEquippedCharms(player);
        if (charms.isEmpty()) return;

        int multiplier = nidhoggurMultiplier(context, player);
        for (ItemStack loot0 : added) {
            ItemStack loot = loot0.copy();
            if (multiplier > 1) {
                int n = loot.getCount() * multiplier;
                if (LHConfig.COMMON.nidhoggurCapAtItemMaxStack.get()) {
                    n = Math.min(n, loot.getMaxStackSize());
                }
                loot.setCount(n);
            }
            CharmStorage.routeLoot(charms, loot);
        }
        after.removeAll(added);
    }

    private static int nidhoggurMultiplier(LootContext context, Player player) {
        Item item = getNidhoggur();
        if (item == Items.AIR || !CurioCompat.hasItemInCurio(player, item)) return 1;
        Object victim = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(victim instanceof LivingEntity living)) return 1;
        if (!MobTraitCap.HOLDER.isProper(living)) return 1;
        MobTraitCap cap = MobTraitCap.HOLDER.get(living);
        double extra = LHConfig.COMMON.nidhoggurDropFactor.get() * cap.getLevel();
        int mult = (int) Math.floor(extra);
        if (context.getRandom().nextDouble() < extra - mult) mult++;
        return mult + 1;
    }

    private static Item getNidhoggur() {
        if (nidhoggur == null) {
            nidhoggur = ForgeRegistries.ITEMS.getValue(
                    new ResourceLocation("l2hostility", "greed_of_nidhoggur"));
        }
        return nidhoggur;
    }
}
