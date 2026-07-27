package net.woodcutter.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.woodcutter.Woodcutter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        return new RecipeProvider(provider, recipeOutput) {
            @Override
            public void buildRecipes() {
                List<String> standardWoods = List.of(
                        "oak", "spruce", "birch", "jungle",
                        "acacia", "dark_oak", "mangrove", "cherry", "pale_oak"
                );

                // 1. Стандартные деревья
                for (String wood : standardWoods) {
                    generateWoodRecipes(this.output, wood, false);
                }

                // 2. Незерская древесина
                generateWoodRecipes(this.output, "crimson", true);
                generateWoodRecipes(this.output, "warped", true);

                // 3. Бамбук
                generateBambooRecipes(this.output);
            }

            private void generateWoodRecipes(RecipeOutput output, String wood, boolean isNether) {
                String logId = isNether ? wood + "_stem" : wood + "_log";
                String strippedLogId = isNether ? "stripped_" + wood + "_stem" : "stripped_" + wood + "_log";
                String woodBlockId = isNether ? wood + "_hyphae" : wood + "_wood";
                String strippedWoodBlockId = isNether ? "stripped_" + wood + "_hyphae" : "stripped_" + wood + "_wood";

                String planksId = wood + "_planks";
                String stairsId = wood + "_stairs";
                String slabId = wood + "_slab";
                String fenceId = wood + "_fence";
                String fenceGateId = wood + "_fence_gate";
                String pressurePlateId = wood + "_pressure_plate";
                String buttonId = wood + "_button";
                String trapdoorId = wood + "_trapdoor";
                String doorId = wood + "_door";

                Item log = getItem(logId);
                Item strippedLog = getItem(strippedLogId);
                Item woodBlock = getItem(woodBlockId);
                Item strippedWoodBlock = getItem(strippedWoodBlockId);

                Item planks = getItem(planksId);
                Item stairs = getItem(stairsId);
                Item slab = getItem(slabId);
                Item fence = getItem(fenceId);
                Item fenceGate = getItem(fenceGateId);
                Item pressurePlate = getItem(pressurePlateId);
                Item button = getItem(buttonId);
                Item trapdoor = getItem(trapdoorId);
                Item door = getItem(doorId);

                // --- КРАФТЫ ИЗ ДОСОК (1 доска = 1 мелкий предмет / 2 полублока) ---
                addCut(output, planks, stairs, 1, wood + "_stairs_from_" + planksId);
                addCut(output, planks, slab, 2, wood + "_slab_from_" + planksId);
                addCut(output, planks, fence, 1, wood + "_fence_from_" + planksId);
                addCut(output, planks, fenceGate, 1, wood + "_fence_gate_from_" + planksId);
                addCut(output, planks, pressurePlate, 1, wood + "_pressure_plate_from_" + planksId);
                addCut(output, planks, button, 1, wood + "_button_from_" + planksId);
                // (Двери и Люки из 1 доски убраны для баланса)

                // Отеска бревна и блока дерева
                addCut(output, log, strippedLog, 1, strippedLogId + "_from_" + logId);
                addCut(output, woodBlock, strippedWoodBlock, 1, strippedWoodBlockId + "_from_" + woodBlockId);

                List<Item> fullWoodBlocks = List.of(log, strippedLog, woodBlock, strippedWoodBlock);
                List<String> fullWoodIds = List.of(logId, strippedLogId, woodBlockId, strippedWoodBlockId);

                // --- КРАФТЫ ИЗ БРЕВЕН И ДЕРЕВА (1 бревно = 4 доски = 2 Двери / 2 Люка) ---
                for (int i = 0; i < fullWoodBlocks.size(); i++) {
                    Item source = fullWoodBlocks.get(i);
                    String sourceId = fullWoodIds.get(i);

                    addCut(output, source, planks, 4, wood + "_planks_from_" + sourceId);
                    addCut(output, source, stairs, 4, wood + "_stairs_from_" + sourceId);
                    addCut(output, source, slab, 8, wood + "_slab_from_" + sourceId);
                    addCut(output, source, fence, 4, wood + "_fence_from_" + sourceId);
                    addCut(output, source, fenceGate, 4, wood + "_fence_gate_from_" + sourceId);
                    addCut(output, source, pressurePlate, 4, wood + "_pressure_plate_from_" + sourceId);
                    addCut(output, source, button, 4, wood + "_button_from_" + sourceId);

                    // БАЛАНС: Из бревна получается 2 Двери и 2 Люка
                    addCut(output, source, trapdoor, 2, wood + "_trapdoor_from_" + sourceId);
                    addCut(output, source, door, 2, wood + "_door_from_" + sourceId);
                }
            }

            private void generateBambooRecipes(RecipeOutput output) {
                Item bambooBlock = getItem("bamboo_block");
                Item strippedBambooBlock = getItem("stripped_bamboo_block");
                Item bambooPlanks = getItem("bamboo_planks");

                Item stairs = getItem("bamboo_stairs");
                Item slab = getItem("bamboo_slab");
                Item fence = getItem("bamboo_fence");
                Item fenceGate = getItem("bamboo_fence_gate");
                Item pressurePlate = getItem("bamboo_pressure_plate");
                Item button = getItem("bamboo_button");
                Item trapdoor = getItem("bamboo_trapdoor");
                Item door = getItem("bamboo_door");

                Item mosaic = getItem("bamboo_mosaic");
                Item mosaicStairs = getItem("bamboo_mosaic_stairs");
                Item mosaicSlab = getItem("bamboo_mosaic_slab");

                // Отеска бамбукового блока
                addCut(output, bambooBlock, strippedBambooBlock, 1, "stripped_bamboo_block_from_bamboo_block");

                // Из бамбуковых досок
                addCut(output, bambooPlanks, stairs, 1, "bamboo_stairs_from_bamboo_planks");
                addCut(output, bambooPlanks, slab, 2, "bamboo_slab_from_bamboo_planks");
                addCut(output, bambooPlanks, fence, 1, "bamboo_fence_from_bamboo_planks");
                addCut(output, bambooPlanks, fenceGate, 1, "bamboo_fence_gate_from_bamboo_planks");
                addCut(output, bambooPlanks, pressurePlate, 1, "bamboo_pressure_plate_from_bamboo_planks");
                addCut(output, bambooPlanks, button, 1, "bamboo_button_from_bamboo_planks");
                addCut(output, bambooPlanks, mosaic, 1, "bamboo_mosaic_from_bamboo_planks");
                addCut(output, bambooPlanks, mosaicStairs, 1, "bamboo_mosaic_stairs_from_bamboo_planks");
                addCut(output, bambooPlanks, mosaicSlab, 2, "bamboo_mosaic_slab_from_bamboo_planks");

                // Из бамбуковых блоков (1 блок = 2 доски = 1 Дверь / 1 Люк)
                List<Item> bambooBlocks = List.of(bambooBlock, strippedBambooBlock);
                List<String> bambooBlockIds = List.of("bamboo_block", "stripped_bamboo_block");

                for (int i = 0; i < bambooBlocks.size(); i++) {
                    Item source = bambooBlocks.get(i);
                    String sourceId = bambooBlockIds.get(i);

                    addCut(output, source, bambooPlanks, 2, "bamboo_planks_from_" + sourceId);
                    addCut(output, source, stairs, 2, "bamboo_stairs_from_" + sourceId);
                    addCut(output, source, slab, 4, "bamboo_slab_from_" + sourceId);
                    addCut(output, source, fence, 2, "bamboo_fence_from_" + sourceId);
                    addCut(output, source, fenceGate, 2, "bamboo_fence_gate_from_" + sourceId);
                    addCut(output, source, pressurePlate, 2, "bamboo_pressure_plate_from_" + sourceId);
                    addCut(output, source, button, 2, "bamboo_button_from_" + sourceId);

                    // БАЛАНС ДЛЯ БАМБУКА: Из блока получается 1 Дверь и 1 Люк
                    addCut(output, source, trapdoor, 1, "bamboo_trapdoor_from_" + sourceId);
                    addCut(output, source, door, 1, "bamboo_door_from_" + sourceId);

                    addCut(output, source, mosaic, 2, "bamboo_mosaic_from_" + sourceId);
                    addCut(output, source, mosaicStairs, 2, "bamboo_mosaic_stairs_from_" + sourceId);
                    addCut(output, source, mosaicSlab, 4, "bamboo_mosaic_slab_from_" + sourceId);
                }
            }

            private Item getItem(String id) {
                return BuiltInRegistries.ITEM.get(Identifier.fromNamespaceAndPath("minecraft", id)).orElseThrow().value();
            }

            private void addCut(RecipeOutput output, Item input, Item result, int count, String recipeName) {
                SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), RecipeCategory.BUILDING_BLOCKS, result, count)
                        .unlockedBy("has_" + getItemName(input), InventoryChangeTrigger.TriggerInstance.hasItems(input))
                        .save(output, Identifier.fromNamespaceAndPath(Woodcutter.MOD_ID, recipeName).toString());
            }
        };
    }

    @Override
    public String getName() {
        return "Woodcutter Recipes";
    }
}