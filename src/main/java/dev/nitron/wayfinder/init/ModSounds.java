package dev.nitron.wayfinder.init;

import dev.nitron.wayfinder.Wayfinder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static void init(){}

    public static final SoundEvent SIGNALSCOPE_INCREASE = registerSound("signalscope_increase");
    public static final SoundEvent SIGNALSCOPE_DECREASE = registerSound("signalscope_decrease");
    public static final SoundEvent SIGNALSCOPE = registerSound("signalscope");
    public static final SoundEvent SIGNALSCOPE_AMBIENT = registerSound("signalscope_ambient");
    public static final SoundEvent TAKE_LENS = registerSound("take_lens");
    public static final SoundEvent PLACE_LENS = registerSound("place_lens");


    public static final SoundEvent SIGNAL_1 = registerSound("signal_1");
    public static final SoundEvent SIGNAL_2 = registerSound("signal_2");
    public static final SoundEvent SIGNAL_3 = registerSound("signal_3");

    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.of(Wayfinder.MOD_ID, id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }
}
