package net.woodcutter.client.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.woodcutter.Woodcutter;
import net.woodcutter.block.ModBlocks;

public class WoodcutterRecipeCategory implements IRecipeCategory<RecipeHolder<StonecutterRecipe>> {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final IRecipeType<RecipeHolder<StonecutterRecipe>> RECIPE_TYPE =
            IRecipeType.create(Woodcutter.MOD_ID, "woodcutting", (Class) RecipeHolder.class);

    private final IDrawable icon;
    private final IDrawable slotBackground;
    private final IDrawable outputSlotBackground;
    private final IDrawableStatic arrow;
    private final Component title;

    public WoodcutterRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.WOODCUTTER));
        this.slotBackground = guiHelper.getSlotDrawable();
        this.outputSlotBackground = guiHelper.getOutputSlot();
        this.arrow = guiHelper.getRecipeArrow();
        this.title = Component.translatable("gui.the_woodcutter.category.woodcutting");
    }

    @Override
    public IRecipeType<RecipeHolder<StonecutterRecipe>> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public int getWidth() {
        return 82;
    }

    @Override
    public int getHeight() {
        return 34;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(RecipeHolder<StonecutterRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.arrow.draw(guiGraphics, 26, 9);
    }

    @SuppressWarnings("removal")
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<StonecutterRecipe> recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 9)
                .setBackground(this.slotBackground, -1, -1)
                .addIngredients(recipe.value().input());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 60, 9)
                .setBackground(this.outputSlotBackground, -5, -5)
                .addItemStack(recipe.value().assemble(new SingleRecipeInput(ItemStack.EMPTY)));
    }
}
