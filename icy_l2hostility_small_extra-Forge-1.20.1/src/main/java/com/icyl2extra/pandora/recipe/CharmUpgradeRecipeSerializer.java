package com.icyl2extra.pandora.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.List;

public class CharmUpgradeRecipeSerializer implements RecipeSerializer<CharmUpgradeRecipe> {

    @Override
    public CharmUpgradeRecipe fromJson(ResourceLocation id, JsonObject json) {
        int level = GsonHelper.getAsInt(json, "level", 0);
        Ingredient charm = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "charm"));
        List<CharmUpgradeRecipe.MaterialEntry> materials = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "materials")) {
            JsonObject obj = element.getAsJsonObject();
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(obj, "ingredient"));
            int count = GsonHelper.getAsInt(obj, "count", 1);
            materials.add(new CharmUpgradeRecipe.MaterialEntry(ingredient, count));
        }
        return new CharmUpgradeRecipe(id, level, charm, materials);
    }

    @Override
    public CharmUpgradeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        int level = buffer.readVarInt();
        Ingredient charm = Ingredient.fromNetwork(buffer);
        int size = buffer.readVarInt();
        List<CharmUpgradeRecipe.MaterialEntry> materials = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            int count = buffer.readVarInt();
            materials.add(new CharmUpgradeRecipe.MaterialEntry(ingredient, count));
        }
        return new CharmUpgradeRecipe(id, level, charm, materials);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, CharmUpgradeRecipe recipe) {
        buffer.writeVarInt(recipe.level);
        recipe.charm.toNetwork(buffer);
        buffer.writeVarInt(recipe.materials.size());
        for (CharmUpgradeRecipe.MaterialEntry material : recipe.materials) {
            material.ingredient().toNetwork(buffer);
            buffer.writeVarInt(material.count());
        }
    }
}
