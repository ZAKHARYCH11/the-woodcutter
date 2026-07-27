package net.woodcutter;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import net.woodcutter.block.ModBlocks;
import net.woodcutter.screen.ModMenuTypes;
import net.woodcutter.recipe.ModRecipes;

import net.woodcutter.sound.ModSounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Woodcutter implements ModInitializer {
	public static final String MOD_ID = "the_woodcutter";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Инициализация мода The Woodcutter!");

		ModBlocks.registerModBlocks();
		ModMenuTypes.registerMenuTypes();
		ModRecipes.registerRecipes();
		ModSounds.registerSounds();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
