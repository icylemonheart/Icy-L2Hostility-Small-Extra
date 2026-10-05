package com.icyl2extra.pandora;

import net.neoforged.neoforge.common.ModConfigSpec;

public class PandoraCharmConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue TIER1_MULTIPLIER;

    public static final ModConfigSpec.DoubleValue TIER2_MULTIPLIER;

    public static final ModConfigSpec.IntValue MAX_LEVEL;

    public static final ModConfigSpec.DoubleValue DAMAGE_REDUCTION_CAP;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("pandora_charm");

        TIER1_MULTIPLIER = builder
                .comment("II 级宝珠的属性倍率，默认 1.5。")
                .defineInRange("tier1Multiplier", 1.5d, 1.0d, 1000.0d);

        TIER2_MULTIPLIER = builder
                .comment("III 级宝珠的属性倍率，默认 2.0。")
                .defineInRange("tier2Multiplier", 2.0d, 1.0d, 1000.0d);

        MAX_LEVEL = builder
                .comment("宝珠最高可强化到的 NBT 等级（2 = III 级封顶），默认 2。")
                .defineInRange("maxLevel", 2, 1, 100);

        builder.pop();

        builder.push("fusion_charm");

        DAMAGE_REDUCTION_CAP = builder
                .comment("减伤软上限（0~1），默认 0.99，=1 时允许 100% 免伤。")
                .defineInRange("damageReductionCap", 0.99d, 0.0d, 1.0d);

        builder.pop();
        SPEC = builder.build();
    }

    private PandoraCharmConfig() {
    }
}
