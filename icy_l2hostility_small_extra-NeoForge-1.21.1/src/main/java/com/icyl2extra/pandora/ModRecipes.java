package com.icyl2extra.pandora;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import com.icyl2extra.pandora.recipe.CharmUpgradeRecipe;
import com.icyl2extra.pandora.recipe.CharmUpgradeRecipeSerializer;
import com.icyl2extra.pandora.recipe.FusionCharmRecipe;
import com.icyl2extra.pandora.recipe.FusionCharmRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, IcyL2HostilitySmallExtra.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, CharmUpgradeRecipeSerializer> CHARM_UPGRADE_SERIALIZER =
            RECIPE_SERIALIZERS.register("charm_upgrade", () -> CharmUpgradeRecipeSerializer.INSTANCE);

    public static final DeferredHolder<RecipeSerializer<?>, FusionCharmRecipeSerializer> FUSION_CHARM_SERIALIZER =
            RECIPE_SERIALIZERS.register("fusion_charm", () -> FusionCharmRecipeSerializer.INSTANCE);

    private ModRecipes() {
    }
}
