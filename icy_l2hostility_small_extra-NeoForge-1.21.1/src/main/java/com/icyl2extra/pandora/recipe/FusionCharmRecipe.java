package com.icyl2extra.pandora.recipe;

import com.google.common.collect.ImmutableList;
import com.icyl2extra.item.ModItems;
import com.icyl2extra.pandora.FusionCharmItem;
import com.icyl2extra.pandora.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class FusionCharmRecipe implements CraftingRecipe {

    private final Ingredient charm;
    private final int charmCount;
    private final List<CharmUpgradeRecipe.MaterialEntry> materials;

    public FusionCharmRecipe(Ingredient charm, int charmCount,
                             List<CharmUpgradeRecipe.MaterialEntry> materials) {
        this.charm = charm;
        this.charmCount = charmCount;
        this.materials = ImmutableList.copyOf(materials);
    }

    public Ingredient charm() {
        return charm;
    }

    public int charmCount() {
        return charmCount;
    }

    public List<CharmUpgradeRecipe.MaterialEntry> materials() {
        return materials;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int charms = 0;
        int[] counts = new int[materials.size()];
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (charm.test(stack)) {
                charms++;
                continue;
            }
            boolean matched = false;
            for (int m = 0; m < materials.size(); m++) {
                if (materials.get(m).ingredient().test(stack)) {
                    counts[m]++;
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }
        if (charms != charmCount) {
            return false;
        }
        for (int m = 0; m < materials.size(); m++) {
            if (counts[m] != materials.get(m).count()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        List<ItemStack> charms = new ArrayList<>();
        for (int i = 0; i < input.size() && charms.size() < charmCount; i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && charm.test(stack)) {
                charms.add(stack.copy());
            }
        }
        return FusionCharmItem.create(charms, registries);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(ModItems.FUSION_CHARM.get());
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> inputs = NonNullList.create();
        for (int i = 0; i < charmCount; i++) {
            inputs.add(charm);
        }
        for (CharmUpgradeRecipe.MaterialEntry material : materials) {
            for (int i = 0; i < material.count(); i++) {
                inputs.add(material.ingredient());
            }
        }
        return inputs;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= charmCount + materials.stream()
                .mapToInt(CharmUpgradeRecipe.MaterialEntry::count).sum();
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.FUSION_CHARM_SERIALIZER.get();
    }
}
