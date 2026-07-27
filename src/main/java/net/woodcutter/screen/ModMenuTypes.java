package net.woodcutter.screen;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.woodcutter.Woodcutter;

public class ModMenuTypes {

    public static final ResourceKey<MenuType<?>> WOODCUTTER_MENU_KEY = ResourceKey.create(
            Registries.MENU,
            Identifier.fromNamespaceAndPath(Woodcutter.MOD_ID, "woodcutter")
    );

    public static final MenuType<WoodcutterMenu> WOODCUTTER_MENU = Registry.register(
            BuiltInRegistries.MENU,
            WOODCUTTER_MENU_KEY,
            new MenuType<>(WoodcutterMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static void registerMenuTypes() {
        Woodcutter.LOGGER.info("Регистрация типов меню для " + Woodcutter.MOD_ID);
    }
}