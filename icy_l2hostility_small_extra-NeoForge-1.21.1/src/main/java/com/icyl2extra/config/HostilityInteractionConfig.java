package com.icyl2extra.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class HostilityInteractionConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue BOTTLE_OF_SANITY_ENABLED;

    public static final ModConfigSpec.BooleanValue HOSTILITY_ESSENCE_ENABLED;

    public static final ModConfigSpec.IntValue ESSENCE_LEVEL_BONUS;

    public static final ModConfigSpec.BooleanValue COLLECT_LOOT_INTO_CHARM;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment(
                "净化瓶 / 恶意精华 对生物的右键交互功能。",
                "Right-click entity interactions for Bottle of Sanity / Hostility Essence.",
                "净化瓶 Bottle of Sanity：等级置 0 并重新随机特性 / reset level to 0 and reroll traits.",
                "恶意精华 Hostility Essence：按 essenceLevelBonus 增加恶意等级 / add hostility levels.");
        builder.push("hostility_interaction");

        BOTTLE_OF_SANITY_ENABLED = builder
                .comment(
                        "是否启用净化瓶对生物的右键交互。",
                        "Enable right-click mob interaction for Bottle of Sanity.",
                        "false = 不干预，物品保持 L2Hostility 原生存放行为。",
                        "false = do not intercept; the item keeps L2Hostility vanilla behavior.")
                .define("bottleOfSanityEnabled", true);

        HOSTILITY_ESSENCE_ENABLED = builder
                .comment(
                        "是否启用恶意精华对生物的右键交互。",
                        "Enable right-click mob interaction for Hostility Essence.",
                        "false = 不干预，物品保持 L2Hostility 原生存放行为。",
                        "false = do not intercept; the item keeps L2Hostility vanilla behavior.")
                .define("hostilityEssenceEnabled", true);

        ESSENCE_LEVEL_BONUS = builder
                .comment(
                        "每个恶意精华对生物右键时提升的恶意等级数。",
                        "Hostility levels added when right-clicking a mob with Hostility Essence.",
                        "默认 100；设为 0 则使用后只消耗、不提升。",
                        "Default 100; 0 = consume with no level gain.")
                .defineInRange("essenceLevelBonus", 100, 0, 1000000);

        builder.pop();

        builder.comment(
                "抢夺宝珠掉落物收集功能。",
                "Looting Charm loot collection.");
        builder.push("charm_loot");

        COLLECT_LOOT_INTO_CHARM = builder
                .comment(
                        "开启后，词条奖励的额外掉落不再掉落到地面，",
                        "而是记录进击杀者 Curios 栏佩戴的抢夺宝珠（含尼德霍格数量倍率）。",
                        "When enabled, extra drops from trait rewards are recorded into",
                        "equipped Looting Charms instead of spawning on the ground",
                        "(Nidhoggur quantity multiplier included).",
                        "嫉妒/暴食诅咒的掉落不受此开关影响，保持 L2Hostility 原始行为。",
                        "Envy/Gluttony curse drops are unaffected and keep vanilla behavior.",
                        "默认 false：保持 L2Hostility 原始掉落行为。",
                        "Default false: keep vanilla L2Hostility drop behavior.")
                .define("collectLootIntoCharm", false);

        builder.pop();
        SPEC = builder.build();
    }

    private HostilityInteractionConfig() {
    }
}
