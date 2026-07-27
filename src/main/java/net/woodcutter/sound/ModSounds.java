package net.woodcutter.sound;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.woodcutter.Woodcutter;

public class ModSounds {

    public static final Identifier WOODCUTTER_TAKE_RESULT_ID = Identifier.fromNamespaceAndPath(Woodcutter.MOD_ID, "ui.woodcutter.take_result");

    public static final SoundEvent WOODCUTTER_TAKE_RESULT = Registry.register(
            BuiltInRegistries.SOUND_EVENT,
            WOODCUTTER_TAKE_RESULT_ID,
            SoundEvent.createVariableRangeEvent(WOODCUTTER_TAKE_RESULT_ID)
    );

    public static void registerSounds() {
        Woodcutter.LOGGER.info("Регистрация звуков для " + Woodcutter.MOD_ID);
    }
}