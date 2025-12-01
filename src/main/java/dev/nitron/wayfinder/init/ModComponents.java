package dev.nitron.wayfinder.init;

import dev.nitron.wayfinder.Wayfinder;
import dev.nitron.wayfinder.content.cca.SignalPlacementsComponent;
import dev.nitron.wayfinder.content.cca.SignalSoundsComponent;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

public class ModComponents implements WorldComponentInitializer, EntityComponentInitializer {
    public static final ComponentKey<SignalPlacementsComponent> SIGNAL_PLACEMENTS = org.ladysnake.cca.api.v3.component.ComponentRegistry.getOrCreate(Identifier.of(Wayfinder.MOD_ID, "signal_placements"),
            SignalPlacementsComponent.class);

    public static final ComponentKey<SignalSoundsComponent> SOUNDS = org.ladysnake.cca.api.v3.component.ComponentRegistry.getOrCreate(Identifier.of(Wayfinder.MOD_ID, "sounds"),
            SignalSoundsComponent.class);

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry worldComponentFactoryRegistry) {
        worldComponentFactoryRegistry.register(SIGNAL_PLACEMENTS, SignalPlacementsComponent::new);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {
        entityComponentFactoryRegistry.registerForPlayers(SOUNDS, SignalSoundsComponent::new, RespawnCopyStrategy.NEVER_COPY);
    }
}
