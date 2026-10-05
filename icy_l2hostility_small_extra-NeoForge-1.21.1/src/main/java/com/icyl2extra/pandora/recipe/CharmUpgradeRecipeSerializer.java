package com.icyl2extra.pandora.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CharmUpgradeRecipeSerializer implements RecipeSerializer<CharmUpgradeRecipe> {

    public static final CharmUpgradeRecipeSerializer INSTANCE = new CharmUpgradeRecipeSerializer();

    public static final MapCodec<CharmUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.INT.optionalFieldOf("level", 0)
                    .forGetter(CharmUpgradeRecipe::level),
            Ingredient.CODEC_NONEMPTY.fieldOf("charm").forGetter(CharmUpgradeRecipe::charm),
            CharmUpgradeRecipe.MaterialEntry.CODEC.listOf().fieldOf("materials")
                    .forGetter(CharmUpgradeRecipe::materials)
    ).apply(inst, CharmUpgradeRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CharmUpgradeRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, CharmUpgradeRecipe::level,
                    Ingredient.CONTENTS_STREAM_CODEC, CharmUpgradeRecipe::charm,
                    CharmUpgradeRecipe.MaterialEntry.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    CharmUpgradeRecipe::materials,
                    CharmUpgradeRecipe::new);

    @Override
    public MapCodec<CharmUpgradeRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CharmUpgradeRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
