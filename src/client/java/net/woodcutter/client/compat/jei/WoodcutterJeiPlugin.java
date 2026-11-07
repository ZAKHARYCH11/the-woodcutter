package net.woodcutter.client.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.woodcutter.Woodcutter;
import net.woodcutter.block.ModBlocks;
import net.woodcutter.client.ClientTasks;
import net.woodcutter.screen.ModMenuTypes;
import net.woodcutter.screen.WoodcutterMenu;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class WoodcutterJeiPlugin implements IModPlugin {

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(Woodcutter.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new WoodcutterRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft client = Minecraft.getInstance();
        if (client.level != null) {
            List<RecipeHolder<StonecutterRecipe>> woodRecipes = new ArrayList<>();

            for (RecipeHolder<StonecutterRecipe> holder : client.level.recipeAccess().getSynchronizedRecipes().getAllOfType(RecipeType.STONECUTTING)) {
                if (holder.id().identifier().getNamespace().equals(Woodcutter.MOD_ID)) {
                    woodRecipes.add(holder);
                }
            }

            registration.addRecipes(WoodcutterRecipeCategory.RECIPE_TYPE, woodRecipes);
        }
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        var recipeManager = jeiRuntime.getRecipeManager();
        var stonecutterRecipes = recipeManager.createRecipeLookup(RecipeTypes.STONECUTTING).get();

        List<RecipeHolder<StonecutterRecipe>> toHide = stonecutterRecipes
                .filter(holder -> holder.id().identifier().getNamespace().equals(Woodcutter.MOD_ID))
                .toList();

        if (!toHide.isEmpty()) {
            recipeManager.hideRecipes(RecipeTypes.STONECUTTING, toHide);
        }
    }

    @SuppressWarnings("removal")
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.WOODCUTTER), WoodcutterRecipeCategory.RECIPE_TYPE);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        IRecipeTransferHandlerHelper helper = registration.getTransferHelper();

        var transferInfo = helper.createBasicRecipeTransferInfo(
                WoodcutterMenu.class,
                ModMenuTypes.WOODCUTTER_MENU,
                WoodcutterRecipeCategory.RECIPE_TYPE,
                0,
                1,
                2,
                36
        );

        var basicHandler = helper.createUnregisteredRecipeTransferHandler(transferInfo);

        registration.addRecipeTransferHandler(new IRecipeTransferHandler<WoodcutterMenu, RecipeHolder<StonecutterRecipe>>() {
            @Override
            public Class<WoodcutterMenu> getContainerClass() {
                return WoodcutterMenu.class;
            }

            @Override
            public java.util.Optional<net.minecraft.world.inventory.MenuType<WoodcutterMenu>> getMenuType() {
                return java.util.Optional.of(ModMenuTypes.WOODCUTTER_MENU);
            }

            @Override
            public mezz.jei.api.recipe.types.IRecipeType<RecipeHolder<StonecutterRecipe>> getRecipeType() {
                return WoodcutterRecipeCategory.RECIPE_TYPE;
            }

            @SuppressWarnings("removal")
            @Override
            public @Nullable IRecipeTransferError transferRecipe(
                    WoodcutterMenu container,
                    RecipeHolder<StonecutterRecipe> recipe,
                    IRecipeSlotsView recipeSlotsView,
                    Player player,
                    boolean maxTransfer,
                    boolean doTransfer
            ) {
                IRecipeTransferError error = basicHandler.transferRecipe(container, recipe, recipeSlotsView, player, maxTransfer, doTransfer);

                if (error == null && doTransfer) {
                    Minecraft mc = Minecraft.getInstance();
                    ItemStack targetOutput = recipe.value().assemble(new SingleRecipeInput(ItemStack.EMPTY));
                    int[] ticksLeft = {20};

                    ClientTasks.add(() -> {
                        if (mc.player == null || mc.gameMode == null || mc.level == null
                                || mc.player.containerMenu != container) {
                            return true;
                        }

                        ItemStack currentInput = container.slots.get(0).getItem();

                        if (!recipe.value().input().test(currentInput)) {
                            return --ticksLeft[0] <= 0;
                        }

                        if (container.hasInputItem()) {
                            var entries = container.getVisibleRecipes().entries();
                            ContextMap ctx = SlotDisplayContext.fromLevel(mc.level);

                            for (int i = 0; i < entries.size(); i++) {
                                ItemStack result = entries.get(i).recipe().optionDisplay().resolveForFirstStack(ctx);
                                if (ItemStack.isSameItemSameComponents(result, targetOutput)) {
                                    mc.gameMode.handleInventoryButtonClick(container.containerId, i);
                                    return true;
                                }
                            }
                        }
                        return --ticksLeft[0] <= 0;
                    });
                }

                return error;
            }
        }, WoodcutterRecipeCategory.RECIPE_TYPE);
    }
}