package com.icyl2extra.pandora.recipe;

import com.google.common.collect.ImmutableList;
import com.icyl2extra.pandora.CharmLevelData;
import com.icyl2extra.pandora.ModRecipes;
import com.icyl2extra.pandora.PandoraCharmConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;

public class CharmUpgradeRecipe implements CraftingRecipe {

    public record MaterialEntry(Ingredient ingredient, int count) {

        public static final Codec<MaterialEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(MaterialEntry::ingredient),
                Codec.INT.optionalFieldOf("count", 1).forGetter(MaterialEntry::count)
        ).apply(inst, MaterialEntry::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, MaterialEntry> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, MaterialEntry::ingredient,
                        ByteBufCodecs.VAR_INT, MaterialEntry::count,
                        MaterialEntry::new);
    }

    private final int level;
    private final Ingredient charm;
    private final List<MaterialEntry> materials;

    public CharmUpgradeRecipe(int level, Ingredient charm, List<MaterialEntry> materials) {
        this.level = level;
        this.charm = charm;
        this.materials = ImmutableList.copyOf(materials);
    }

    public int level() {
        return level;
    }

    public Ingredient charm() {
        return charm;
    }

    public List<MaterialEntry> materials() {
        return materials;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (this.level >= PandoraCharmConfig.MAX_LEVEL.get()) {
            return false;
        }
        int charmCount = 0;
        int[] found = new int[materials.size()];

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (charm.test(stack)) {
                if (CharmLevelData.getLevel(stack) != this.level) {
                    return false;
                }
                charmCount++;
                continue;
            }
            int index = -1;
            for (int j = 0; j < materials.size(); j++) {
                if (materials.get(j).ingredient().test(stack)) {
                    index = j;
                    break;
                }
            }
            if (index < 0) {
                return false;
            }
            found[index] += stack.getCount();
        }

        if (charmCount != 1) {
            return false;
        }
        for (int j = 0; j < materials.size(); j++) {
            if (found[j] != materials.get(j).count()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && charm.test(stack)) {
                ItemStack result = stack.copy();
                result.setCount(1);
                int nextLevel = Math.min(
                        PandoraCharmConfig.MAX_LEVEL.get(),
                        CharmLevelData.getLevel(stack) + 1);
                CharmLevelData.setLevel(result, nextLevel);
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        ItemStack[] items = charm.getItems();
        if (items.length == 0) {
            return ItemStack.EMPTY;
        }
        ItemStack result = items[0].copy();
        result.setCount(1);
        CharmLevelData.setLevel(result, Math.min(
                PandoraCharmConfig.MAX_LEVEL.get(), level + 1));
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> inputs = NonNullList.create();
        inputs.add(charm);
        for (MaterialEntry material : materials) {
            for (int i = 0; i < material.count(); i++) {
                inputs.add(material.ingredient());
            }
        }
        return inputs;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1 + materials.stream().mapToInt(MaterialEntry::count).sum();
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CHARM_UPGRADE_SERIALIZER.get();
    }
}
