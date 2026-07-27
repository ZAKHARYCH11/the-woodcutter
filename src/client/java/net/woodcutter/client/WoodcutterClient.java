package net.woodcutter.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.woodcutter.screen.ModMenuTypes;
import net.woodcutter.client.screen.WoodcutterScreen;

public class WoodcutterClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModMenuTypes.WOODCUTTER_MENU, WoodcutterScreen::new);
	}
}