package com.icyl2extra.pandora;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import dev.xkmc.curseofpandora.content.pandora.AttributeItem;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public final class CharmLevelData {

    public static final String NBT_KEY = "CharmLevel";

    public static int getLevel(ItemStack stack) {
        if (stack == null || !stack.hasTag()) {
            return 0;
        }
        return Math.max(0, stack.getTag().getInt(NBT_KEY));
    }

    public static double getMultiplier(int level) {
        if (level <= 0) {
            return 1.0d;
        }
        double tier1 = PandoraCharmConfig.TIER1_MULTIPLIER.get();
        double tier2 = PandoraCharmConfig.TIER2_MULTIPLIER.get();
        if (level == 1) {
            return tier1;
        }
        if (level == 2) {
            return tier2;
        }
        return tier2 * Math.pow(tier2 / tier1, level - 2);
    }

    private static final String[] ROMAN = {
            "", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"
    };

    public static String levelSuffix(int level) {
        if (level <= 0) {
            return "";
        }
        if (level < ROMAN.length) {
            return " " + ROMAN[level];
        }
        return " Lv." + level;
    }

    public static Multimap<Attribute, AttributeModifier> buildScaled(
            AttributeItem.AttributeEntry[] entries, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> ans = LinkedHashMultimap.create();
        double multiplier = getMultiplier(getLevel(stack));
        for (AttributeItem.AttributeEntry entry : entries) {
            double scaledValue = entry.val().getAsDouble() * multiplier;
            ans.put(entry.attr().get(),
                    new AttributeModifier(uuid, entry.name(), scaledValue, entry.op()));
        }
        return ans;
    }

    private CharmLevelData() {
    }
}
