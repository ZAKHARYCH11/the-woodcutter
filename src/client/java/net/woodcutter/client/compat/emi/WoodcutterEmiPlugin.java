package net.woodcutter.client.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.woodcutter.Woodcutter;
import net.woodcutter.block.ModBlocks;
import net.woodcutter.client.ClientTasks;
import net.woodcutter.screen.ModMenuTypes;
import net.woodcutter.screen.WoodcutterMenu;

import java.util.List;

public class WoodcutterEmiPlugin implements EmiPlugin {

    public static final Identifier WOODCUTTING_ID = Identifier.fromNamespaceAndPath(Woodcutter.MOD_ID, "woodcutting");

    public static final EmiRecipeCategory WOODCUTTING_CATEGORY = new EmiRecipeCategory(
            WOODCUTTING_ID,
            EmiStack.of(new ItemStack(ModBlocks.WOODCUTTER)),
            new EmiTexture(Identifier.fromNamespaceAndPath("emi", "textures/gui/widgets.png"), 224, 144, 16, 16)
    );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(WOODCUTTING_CATEGORY);
        registry.addWorkstation(WOODCUTTING_CATEGORY, EmiStack.of(new ItemStack(ModBlocks.WOODCUTTER)));

        registry.addRecipeHandler(ModMenuTypes.WOODCUTTER_MENU, new StandardRecipeHandler<WoodcutterMenu>() {
            @Override
            public List<Slot> getInputSources(WoodcutterMenu handler) {
                return handler.slots.subList(2, 38);
            }

            @Override
            public List<Slot> getCraftingSlots(WoodcutterMenu handler) {
                return List.of(handler.slots.get(0));
            }

            @Override
            public boolean supportsRecipe(EmiRecipe recipe) {
                return recipe.getCategory() == WOODCUTTING_CATEGORY;
            }

            private static void selectRecipe(WoodcutterMenu menu, Ingredient expectedIngredient, ItemStack target) {
                Minecraft mc = Minecraft.getInstance();
                int[] ticksLeft = {20};

                ClientTasks.add(() -> {
                    if (mc.player == null || mc.gameMode == null || mc.level == null
                            || mc.player.containerMenu != menu) {
                        return true;
                    }

                    if (!expectedIngredient.test(menu.slots.get(0).getItem()) || !menu.hasInputItem()) {
                        return --ticksLeft[0] <= 0;
                    }

                    var entries = menu.getVisibleRecipes().entries();
                    ContextMap ctx = SlotDisplayContext.fromLevel(mc.level);

                    int targetIndex = -1;
                    for (int i = 0; i < entries.size(); i++) {
                        ItemStack result = entries.get(i).recipe().optionDisplay().resolveForFirstStack(ctx);
                        if (ItemStack.isSameItemSameComponents(result, target)) {
                            targetIndex = i;
                            break;
                        }
                    }
                    if (targetIndex < 0) {
                        return --ticksLeft[0] <= 0;
                    }

                    if (entries.size() > 1) {
                        int other = targetIndex == 0 ? 1 : 0;
                        mc.gameMode.handleInventoryButtonClick(menu.containerId, other);
                    }
                    mc.gameMode.handleInventoryButtonClick(menu.containerId, targetIndex);
                    return true;
                });
            }

            @Override
            public boolean craft(EmiRecipe recipe, EmiCraftContext<WoodcutterMenu> context) {
                System.out.println("amount=" + context.getAmount() + " dest=" + context.getDestination() + " type=" + context.getType());
                WoodcutterMenu menu = context.getScreenHandler();
                boolean success = StandardRecipeHandler.super.craft(recipe, context);

                if (success && menu != null && recipe instanceof WoodcutterEmiRecipe r) {
                    selectRecipe(menu, r.getRawIngredient(), r.getOutputs().get(0).getItemStack());
                }
                return success;
            }
        });

        registry.removeRecipes(recipe -> {
            return recipe.getCategory() == VanillaEmiRecipeCategories.STONECUTTING &&
                    recipe.getId() != null &&
                    recipe.getId().getNamespace().equals(Woodcutter.MOD_ID);
        });

        for (RecipeHolder<StonecutterRecipe> holder : registry.getRecipeMap().byType(RecipeType.STONECUTTING)) {
            Identifier recipeId = holder.id().identifier();

            if (recipeId.getNamespace().equals(Woodcutter.MOD_ID)) {
                StonecutterRecipe recipe = holder.value();
                registry.addRecipe(new WoodcutterEmiRecipe(recipeId, recipe));
            }
        }
    }

    public static class WoodcutterEmiRecipe implements EmiRecipe {
        private final Identifier id;
        private final Ingredient rawIngredient;
        private final EmiIngredient input;
        private final EmiStack output;

        public WoodcutterEmiRecipe(Identifier id, StonecutterRecipe recipe) {
            this.id = id;
            this.rawIngredient = recipe.input();
            this.input = EmiIngredient.of(recipe.input());
            this.output = EmiStack.of(recipe.assemble(new SingleRecipeInput(ItemStack.EMPTY)));
        }

        public Ingredient getRawIngredient() {
            return rawIngredient;
        }

        @Override
        public EmiRecipeCategory getCategory() {
            return WOODCUTTING_CATEGORY;
        }

        @Override
        public Identifier getId() {
            return id;
        }

        @Override
        public List<EmiIngredient> getInputs() {
            return List.of(input);
        }

        @Override
        public List<EmiStack> getOutputs() {
            return List.of(output);
        }

        @Override
        public int getDisplayWidth() {
            return 76;
        }

        @Override
        public int getDisplayHeight() {
            return 18;
        }

        @Override
        public void addWidgets(WidgetHolder widgets) {
            widgets.addSlot(input, 0, 0);
            widgets.addTexture(EmiTexture.EMPTY_ARROW, 26, 1);
            widgets.addSlot(output, 58, 0).recipeContext(this);
        }
    }
}