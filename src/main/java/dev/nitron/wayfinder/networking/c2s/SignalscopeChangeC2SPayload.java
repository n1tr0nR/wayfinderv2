package dev.nitron.wayfinder.networking.c2s;

import dev.nitron.wayfinder.Wayfinder;
import dev.nitron.wayfinder.content.data_component.SignalscopeComponent;
import dev.nitron.wayfinder.content.item.SignalscopeItem;
import dev.nitron.wayfinder.init.ModDataComponents;
import dev.nitron.wayfinder.init.ModSounds;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public record SignalscopeChangeC2SPayload(
        int oldFrequency, double oldGain,
        int newFrequency, double newGain
) implements CustomPayload {

    public static final Identifier SIGNALSCOPE_CHANGE =
            Identifier.of(Wayfinder.MOD_ID, "signalscope_change_payload");
    public static final Id<SignalscopeChangeC2SPayload> ID = new Id<>(SIGNALSCOPE_CHANGE);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static final PacketCodec<PacketByteBuf, SignalscopeChangeC2SPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, SignalscopeChangeC2SPayload::oldFrequency,
                    PacketCodecs.DOUBLE, SignalscopeChangeC2SPayload::oldGain,
                    PacketCodecs.INTEGER, SignalscopeChangeC2SPayload::newFrequency,
                    PacketCodecs.DOUBLE, SignalscopeChangeC2SPayload::newGain,
                    SignalscopeChangeC2SPayload::new
            );

    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<SignalscopeChangeC2SPayload> {
        @Override
        public void receive(SignalscopeChangeC2SPayload payload, ServerPlayNetworking.Context context) {
            context.server().execute(() -> {
                ItemStack held = context.player().getMainHandStack();
                if (!(held.getItem() instanceof SignalscopeItem)) return;

                SignalscopeComponent def = held.get(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE);

                held.set(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE,
                        new SignalscopeComponent(payload.newFrequency(), payload.newGain(), def.lensItemstack(), def.upgradeItemstack(), def.selected()));

                Random random = context.player().getRandom();
                if (payload.oldFrequency > payload.newFrequency || payload.oldGain > payload.newGain){
                    //Down
                    context.player().playSoundToPlayer(ModSounds.SIGNALSCOPE_INCREASE, SoundCategory.PLAYERS, 0.5F, 0.8F + random.nextFloat() * 0.1F);
                } else if (payload.oldFrequency < payload.newFrequency || payload.oldGain < payload.newGain){
                    //Up
                    context.player().playSoundToPlayer(ModSounds.SIGNALSCOPE_INCREASE, SoundCategory.PLAYERS, 0.5F, 0.95F + random.nextFloat() * 0.1F);
                }
            });
        }
    }
}

