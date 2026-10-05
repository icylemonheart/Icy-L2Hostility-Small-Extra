package com.icyl2extra.pandora;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import dev.xkmc.curseofpandora.content.pandora.AttributeItem;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class CharmLevelData {

    public static final String NBT_KEY = "CharmLevel";

    public static int getLevel(ItemStack stack) {
        if (stack == null) {
            return 0;
        }
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? 0 : Math.max(0, data.copyTag().getInt(NBT_KEY));
    }

    public static void setLevel(ItemStack stack, int level) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt(NBT_KEY, level));
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

    public static Multimap<Holder<Attribute>, AttributeModifier> buildScaled(
            AttributeItem.AttributeEntry[] entries, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> ans = LinkedHashMultimap.create();
        double multiplier = getMultiplier(getLevel(stack));
        for (AttributeItem.AttributeEntry entry : entries) {
            double scaledValue = entry.val().getAsDouble() * multiplier;
            ans.put(entry.attr(),
                    new AttributeModifier(id.withSuffix("_" + entry.name()), scaledValue, entry.op()));
        }
        return ans;
    }

    private CharmLevelData() {
    }
}
