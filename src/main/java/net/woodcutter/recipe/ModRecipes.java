package net.woodcutter.recipe;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.woodcutter.Woodcutter;

public class ModRecipes {

    public static final RecipeType<WoodcuttingRecipe> WOODCUTTING_TYPE = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            Identifier.fromNamespaceAndPath(Woodcutter.MOD_ID, "woodcutting"),
            new RecipeType<WoodcuttingRecipe>() {
                @Override
                public String toString() {
                    return "the_woodcutter:woodcutting";
                }
            }
    );

    public static final RecipeSerializer<WoodcuttingRecipe> WOODCUTTING_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(Woodcutter.MOD_ID, "woodcutting"),
            WoodcuttingRecipe.SERIALIZER
    );

    public static void registerRecipes() {
        Woodcutter.LOGGER.info("Регистрация типов рецептов для " + Woodcutter.MOD_ID);
    }
}