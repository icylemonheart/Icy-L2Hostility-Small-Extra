package com.icyl2extra.pandora.recipe;

import com.icyl2extra.item.ModItems;
import com.icyl2extra.pandora.FusionCharmItem;
import com.icyl2extra.pandora.ModRecipes;
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

import java.util.ArrayList;
import java.util.List;

public class FusionCharmRecipe extends ShapelessRecipe {

    final Ingredient charm;
    final int charmCount;
    final List<CharmUpgradeRecipe.MaterialEntry> materials;

    public FusionCharmRecipe(ResourceLocation id, Ingredient charm, int charmCount,
                             List<CharmUpgradeRecipe.MaterialEntry> materials) {
        super(id, "", CraftingBookCategory.MISC,
                new ItemStack(ModItems.FUSION_CHARM.get()), buildInputs(charm, charmCount, materials));
        this.charm = charm;
        this.charmCount = charmCount;
        this.materials = materials;
    }

    private static NonNullList<Ingredient> buildInputs(Ingredient charm, int charmCount,
                                                       List<CharmUpgradeRecipe.MaterialEntry> materials) {
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
    public boolean matches(CraftingContainer container, Level level) {
        int charms = 0;
        int[] counts = new int[materials.size()];
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
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
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        List<ItemStack> charms = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize() && charms.size() < charmCount; i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && charm.test(stack)) {
                charms.add(stack.copy());
            }
        }
        return FusionCharmItem.create(charms);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= charmCount + materials.stream()
                .mapToInt(CharmUpgradeRecipe.MaterialEntry::count).sum();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.FUSION_CHARM_SERIALIZER.get();
    }
}
