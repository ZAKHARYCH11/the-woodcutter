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
import net.minecraft.util.context.ContextMap;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.woodcutter.Woodcutter;
import net.woodcutter.block.ModBlocks;
import net.woodcutter.screen.ModMenuTypes;
import net.woodcutter.screen.WoodcutterMenu;
import net.woodcutter.client.ClientTasks;

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

            private static void selectRecipe(WoodcutterMenu menu, ItemStack target) {
                Minecraft mc = Minecraft.getInstance();
                int[] ticksLeft = {20};

                ClientTasks.add(() -> {
                    if (mc.player == null || mc.gameMode == null || mc.level == null
                            || mc.player.containerMenu != menu) {
                        return true;
                    }

                    if (menu.hasInputItem()) {
                        var entries = menu.getVisibleRecipes().entries();
                        ContextMap ctx = SlotDisplayContext.fromLevel(mc.level);

                        for (int i = 0; i < entries.size(); i++) {
                            ItemStack result = entries.get(i).recipe().optionDisplay().resolveForFirstStack(ctx);
                            if (ItemStack.isSameItemSameComponents(result, target)) {
                                menu.clickMenuButton(mc.player, i);
                                mc.gameMode.handleInventoryButtonClick(menu.containerId, i);
                                return true;
                            }
                        }
                    }
                    return --ticksLeft[0] <= 0;
                });
            }

            @Override
            public boolean craft(EmiRecipe recipe, EmiCraftContext<WoodcutterMenu> context) {
                boolean success = StandardRecipeHandler.super.craft(recipe, context);

                if (recipe instanceof WoodcutterEmiRecipe woodcutterRecipe) {
                    WoodcutterMenu menu = context.getScreenHandler();
                    if (menu != null) {
                        selectRecipe(menu, woodcutterRecipe.getOutputs().get(0).getItemStack());
                    }
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
        private final EmiIngredient input;
        private final EmiStack output;

        public WoodcutterEmiRecipe(Identifier id, StonecutterRecipe recipe) {
            this.id = id;
            this.input = EmiIngredient.of(recipe.input());
            this.output = EmiStack.of(recipe.assemble(new SingleRecipeInput(ItemStack.EMPTY)));
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