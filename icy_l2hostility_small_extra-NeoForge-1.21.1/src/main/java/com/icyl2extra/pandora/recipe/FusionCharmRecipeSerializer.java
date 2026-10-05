package com.icyl2extra.pandora.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FusionCharmRecipeSerializer implements RecipeSerializer<FusionCharmRecipe> {

    public static final FusionCharmRecipeSerializer INSTANCE = new FusionCharmRecipeSerializer();

    public static final MapCodec<FusionCharmRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("charm").forGetter(FusionCharmRecipe::charm),
            Codec.INT.optionalFieldOf("charm_count", 1).forGetter(FusionCharmRecipe::charmCount),
            CharmUpgradeRecipe.MaterialEntry.CODEC.listOf().fieldOf("materials")
                    .forGetter(FusionCharmRecipe::materials)
    ).apply(inst, FusionCharmRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FusionCharmRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, FusionCharmRecipe::charm,
                    ByteBufCodecs.VAR_INT, FusionCharmRecipe::charmCount,
                    CharmUpgradeRecipe.MaterialEntry.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    FusionCharmRecipe::materials,
                    FusionCharmRecipe::new);

    @Override
    public MapCodec<FusionCharmRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FusionCharmRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
