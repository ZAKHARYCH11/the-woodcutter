package net.woodcutter.block;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.woodcutter.Woodcutter;

public class ModBlocks {

    public static final ResourceKey<Block> WOODCUTTER_KEY = ResourceKey.create(
            Registries.BLOCK,
            Identifier.fromNamespaceAndPath(Woodcutter.MOD_ID, "woodcutter")
    );

    public static final ResourceKey<Item> WOODCUTTER_ITEM_KEY = ResourceKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(Woodcutter.MOD_ID, "woodcutter")
    );

    public static final Block WOODCUTTER = registerBlock();

    private static Block registerBlock() {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                .setId(WOODCUTTER_KEY)
                .strength(2.0f)
                .sound(SoundType.WOOD);

        WoodcutterBlock block = new WoodcutterBlock(properties);

        Registry.register(BuiltInRegistries.BLOCK, WOODCUTTER_KEY, block);

        Item.Properties itemProperties = new Item.Properties().setId(WOODCUTTER_ITEM_KEY);
        Registry.register(BuiltInRegistries.ITEM, WOODCUTTER_ITEM_KEY, new BlockItem(block, itemProperties));

        return block;
    }

    public static void registerModBlocks() {
        Woodcutter.LOGGER.info("Регистрация блоков для " + Woodcutter.MOD_ID);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(output -> {
            output.accept(WOODCUTTER);
        });
    }
}