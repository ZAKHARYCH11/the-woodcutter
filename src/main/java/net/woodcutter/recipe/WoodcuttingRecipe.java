package net.woodcutter.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.StonecutterRecipeDisplay;
import net.woodcutter.block.ModBlocks;

import java.util.List;

public class WoodcuttingRecipe extends SingleItemRecipe {

    public static final MapCodec<WoodcuttingRecipe> MAP_CODEC = simpleMapCodec(WoodcuttingRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, WoodcuttingRecipe> STREAM_CODEC = simpleStreamCodec(WoodcuttingRecipe::new);
    public static final RecipeSerializer<WoodcuttingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public WoodcuttingRecipe(final Recipe.CommonInfo commonInfo, final Ingredient ingredient, final ItemStackTemplate result) {
        super(commonInfo, ingredient, result);
    }

    @Override
    public RecipeType<WoodcuttingRecipe> getType() {
        return ModRecipes.WOODCUTTING_TYPE;
    }

    @Override
    public RecipeSerializer<WoodcuttingRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.STONECUTTER;
    }

    public SlotDisplay resultDisplay() {
        return new SlotDisplay.ItemStackSlotDisplay(this.result());
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new StonecutterRecipeDisplay(
                this.input().display(),
                this.resultDisplay(),
                new SlotDisplay.ItemSlotDisplay(ModBlocks.WOODCUTTER.asItem())
        ));
    }
}