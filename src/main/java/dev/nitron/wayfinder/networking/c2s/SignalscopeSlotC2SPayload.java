package dev.nitron.wayfinder.networking.c2s;

import dev.nitron.wayfinder.Wayfinder;
import dev.nitron.wayfinder.content.data_component.SignalscopeComponent;
import dev.nitron.wayfinder.content.item.SignalscopeItem;
import dev.nitron.wayfinder.init.ModDataComponents;
import dev.nitron.wayfinder.init.ModSounds;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public record SignalscopeSlotC2SPayload(
        int slot, int selected
) implements CustomPayload {

    public static final Identifier SIGNALSCOPE_CHANGE =
            Identifier.of(Wayfinder.MOD_ID, "signalscope_slot_payload");
    public static final Id<SignalscopeSlotC2SPayload> ID = new Id<>(SIGNALSCOPE_CHANGE);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static final PacketCodec<PacketByteBuf, SignalscopeSlotC2SPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, SignalscopeSlotC2SPayload::slot,
                    PacketCodecs.INTEGER, SignalscopeSlotC2SPayload::selected,
                    SignalscopeSlotC2SPayload::new
            );

    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<SignalscopeSlotC2SPayload> {
        @Override public void receive(SignalscopeSlotC2SPayload payload, ServerPlayNetworking.Context context) {
            context.server().execute(() -> {
                PlayerEntity player = context.player();
                if (player == null) return;
                Wayfinder.LOGGER.info("Slot " + payload.slot);
                ScreenHandler handler = player.currentScreenHandler;

                if (payload.slot < 0 || payload.slot >= handler.slots.size()) return;

                Slot slot = handler.getSlot(payload.slot);
                ItemStack held = slot.getStack();

                if (!(held.getItem() instanceof SignalscopeItem)) return;
                SignalscopeComponent def = held.get(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE);

                held.set(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE, new SignalscopeComponent(def.frequency(), def.gain(), def.lensItemstack(), def.upgradeItemstack(), payload.selected()));

                Random random = player.getRandom();
                if (def.selected() > payload.selected){
                    //Down
                    context.player().playSoundToPlayer(ModSounds.SIGNALSCOPE_INCREASE, SoundCategory.PLAYERS, 0.5F, 0.8F + random.nextFloat() * 0.1F);
                } else if (def.selected() < payload.selected){
                    //Up
                    context.player().playSoundToPlayer(ModSounds.SIGNALSCOPE_INCREASE, SoundCategory.PLAYERS, 0.5F, 0.95F + random.nextFloat() * 0.1F);
                }

                ItemStack selected = payload.selected == 2 ? def.upgradeItemstack() : payload.selected == 1 ? def.lensItemstack() : ItemStack.EMPTY;
                if (!selected.isEmpty()){
                    if (def.selected() > payload.selected){
                        //Down
                        context.player().playSoundToPlayer(SoundEvents.BLOCK_VAULT_ACTIVATE, SoundCategory.PLAYERS, 0.5F, 0.8F + random.nextFloat() * 0.1F);
                    } else if (def.selected() < payload.selected){
                        //Up
                        context.player().playSoundToPlayer(SoundEvents.BLOCK_VAULT_ACTIVATE, SoundCategory.PLAYERS, 0.5F, 0.95F + random.nextFloat() * 0.1F);
                    }
                }
            });
        }
    }
}

