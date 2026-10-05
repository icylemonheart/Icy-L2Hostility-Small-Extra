package com.icyl2extra.pandora;

import com.icyl2extra.IcyL2HostilitySmallExtra;
import com.icyl2extra.pandora.recipe.CharmUpgradeRecipe;
import com.icyl2extra.pandora.recipe.CharmUpgradeRecipeSerializer;
import com.icyl2extra.pandora.recipe.FusionCharmRecipe;
import com.icyl2extra.pandora.recipe.FusionCharmRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, IcyL2HostilitySmallExtra.MODID);

    public static final RegistryObject<RecipeSerializer<CharmUpgradeRecipe>> CHARM_UPGRADE_SERIALIZER =
            RECIPE_SERIALIZERS.register("charm_upgrade", CharmUpgradeRecipeSerializer::new);

    public static final RegistryObject<RecipeSerializer<FusionCharmRecipe>> FUSION_CHARM_SERIALIZER =
            RECIPE_SERIALIZERS.register("fusion_charm", FusionCharmRecipeSerializer::new);

    private ModRecipes() {
    }
}
