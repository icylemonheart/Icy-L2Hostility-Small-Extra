package com.icyl2extra.pandora;

import net.minecraftforge.common.ForgeConfigSpec;

public class PandoraCharmConfig {

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue TIER1_MULTIPLIER;

    public static final ForgeConfigSpec.DoubleValue TIER2_MULTIPLIER;

    public static final ForgeConfigSpec.IntValue MAX_LEVEL;

    public static final ForgeConfigSpec.DoubleValue DAMAGE_REDUCTION_CAP;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment(
                "潘多拉宝珠逐级强化（CurseOfPandora 联动）：每级属性 = 基础值 × 对应倍率。",
                "Pandora charm tier upgrades (CurseOfPandora compat): attribute = base x multiplier.");
        builder.push("pandora_charm");

        TIER1_MULTIPLIER = builder
                .comment(
                        "II 级宝珠的属性倍率，默认 1.5。",
                        "Multiplier for tier II charms. Default 1.5.")
                .defineInRange("tier1Multiplier", 1.5d, 1.0d, 1000.0d);

        TIER2_MULTIPLIER = builder
                .comment(
                        "III 级宝珠的属性倍率，默认 2.0。",
                        "Multiplier for tier III charms. Default 2.0.")
                .defineInRange("tier2Multiplier", 2.0d, 1.0d, 1000.0d);

        MAX_LEVEL = builder
                .comment(
                        "宝珠最高可强化到的 NBT 等级（2 = III 级封顶），默认 2。",
                        "Maximum charm NBT level (2 = capped at tier III). Default 2.")
                .defineInRange("maxLevel", 2, 1, 100);

        builder.pop();

        builder.comment(
                "融合宝珠减伤软上限：受伤总系数最低钳制到 1 - 此值，全局生效（含怪物）；0.99=最多减伤99%，1=允许免伤。",
                "Fusion charm damage-reduction soft cap: damage-taken multiplier clamped to >= (1 - value), globally incl. mobs; 0.99=99% max, 1=full immunity.")
                .push("fusion_charm");

        DAMAGE_REDUCTION_CAP = builder
                .comment(
                        "减伤软上限（0~1），默认 0.99。",
                        "Damage-reduction cap (0~1). Default 0.99.")
                .defineInRange("damageReductionCap", 0.99d, 0.0d, 1.0d);

        builder.pop();
        SPEC = builder.build();
    }

    private PandoraCharmConfig() {
    }
}
