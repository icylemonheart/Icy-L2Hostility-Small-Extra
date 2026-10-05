package com.icyl2extra.pandora.recipe;

import com.icyl2extra.pandora.CharmLevelData;
import com.icyl2extra.pandora.ModRecipes;
import com.icyl2extra.pandora.PandoraCharmConfig;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;

import java.util.List;

public class CharmUpgradeRecipe extends ShapelessRecipe {

    public record MaterialEntry(Ingredient ingredient, int count) {
    }

    final int level;
    final Ingredient charm;
    final List<MaterialEntry> materials;

    public CharmUpgradeRecipe(ResourceLocation id, int level, Ingredient charm, List<MaterialEntry> materials) {
        super(id, "", CraftingBookCategory.MISC, buildResult(charm, level), buildInputs(charm, materials));
        this.level = level;
        this.charm = charm;
        this.materials = materials;
    }

    private static ItemStack buildResult(Ingredient charm, int level) {
        ItemStack[] items = charm.getItems();
        if (items.length == 0) {
            return ItemStack.EMPTY;
        }
        ItemStack representative = items[0].copy();
        representative.setCount(1);
        representative.getOrCreateTag().putInt(CharmLevelData.NBT_KEY, level + 1);
        return representative;
    }

    private static NonNullList<Ingredient> buildInputs(Ingredient charm, List<MaterialEntry> materials) {
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
    public boolean matches(CraftingContainer container, Level level) {
        if (this.level >= PandoraCharmConfig.MAX_LEVEL.get()) {
            return false;
        }
        int charmCount = 0;
        ItemStack theCharm = ItemStack.EMPTY;
        int[] found = new int[this.materials.size()];

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (this.charm.test(stack)) {
                if (CharmLevelData.getLevel(stack) != this.level) {
                    return false;
                }
                theCharm = stack;
                charmCount++;
                continue;
            }
            int index = -1;
            for (int j = 0; j < this.materials.size(); j++) {
                if (this.materials.get(j).ingredient().test(stack)) {
                    index = j;
                    break;
                }
            }
            if (index < 0) {
                return false;
            }
            found[index] += stack.getCount();
        }

        if (charmCount != 1 || theCharm.isEmpty()) {
            return false;
        }
        for (int j = 0; j < this.materials.size(); j++) {
            if (found[j] != this.materials.get(j).count()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && this.charm.test(stack)) {
                ItemStack result = stack.copy();
                result.setCount(1);
                int nextLevel = Math.min(
                        PandoraCharmConfig.MAX_LEVEL.get(),
                        CharmLevelData.getLevel(stack) + 1);
                result.getOrCreateTag().putInt(CharmLevelData.NBT_KEY, nextLevel);
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return buildResult(this.charm, this.level);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CHARM_UPGRADE_SERIALIZER.get();
    }
}
