package dev.nitron.wayfinder.networking.c2s;

import dev.nitron.wayfinder.Wayfinder;
import dev.nitron.wayfinder.content.data_component.SignalTweakerComponent;
import dev.nitron.wayfinder.content.data_component.SignalscopeComponent;
import dev.nitron.wayfinder.content.item.SignalTweakerItem;
import dev.nitron.wayfinder.content.item.SignalscopeItem;
import dev.nitron.wayfinder.init.ModDataComponents;
import dev.nitron.wayfinder.init.ModSounds;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public record SignalTweakerSlotC2SPayload(
        int slot, int direction, boolean isHoldingAlt, boolean isHoldingControl
) implements CustomPayload {

    public static final Identifier SIGNALSCOPE_CHANGE =
            Identifier.of(Wayfinder.MOD_ID, "signal_tweaker_slot_payload");
    public static final Id<SignalTweakerSlotC2SPayload> ID = new Id<>(SIGNALSCOPE_CHANGE);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static final PacketCodec<PacketByteBuf, SignalTweakerSlotC2SPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, SignalTweakerSlotC2SPayload::slot,
                    PacketCodecs.INTEGER, SignalTweakerSlotC2SPayload::direction,
                    PacketCodecs.BOOLEAN, SignalTweakerSlotC2SPayload::isHoldingAlt,
                    PacketCodecs.BOOLEAN, SignalTweakerSlotC2SPayload::isHoldingControl,
                    SignalTweakerSlotC2SPayload::new
            );

    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<SignalTweakerSlotC2SPayload> {
        @Override public void receive(SignalTweakerSlotC2SPayload payload, ServerPlayNetworking.Context context) {
            context.server().execute(() -> {
                PlayerEntity player = context.player();
                if (player == null) return;
                ScreenHandler handler = player.currentScreenHandler;
                int dir = payload.direction;

                if (payload.slot < 0 || payload.slot >= handler.slots.size()) return;

                Slot slot = handler.getSlot(payload.slot);
                ItemStack held = slot.getStack();
                if (!(held.getItem() instanceof SignalTweakerItem)) return;
                SignalTweakerComponent component = held.get(ModDataComponents.SIGNAL_TWEAKER_COMPONENT_COMPONENT_TYPE);

                if (payload.direction != 0){
                    context.player().playSoundToPlayer(ModSounds.SIGNALSCOPE_INCREASE, SoundCategory.PLAYERS, 0.5F, 0.95F + context.player().getRandom().nextFloat() * 0.1F);
                    if (payload.isHoldingAlt){
                        context.player().playSoundToPlayer(SoundEvents.BLOCK_VAULT_ACTIVATE, SoundCategory.PLAYERS, 0.5F, 0.95F + context.player().getRandom().nextFloat() * 0.1F);
                    }
                }

                if (payload.isHoldingAlt){
                    int current = component.selected();
                    if (dir > 0){
                        current--;
                        if (current < 0){
                            current = 5;
                        }
                    }
                    if (dir < 0){
                        current++;
                        if (current > 5){
                            current = 0;
                        }
                    }
                    held.set(ModDataComponents.SIGNAL_TWEAKER_COMPONENT_COMPONENT_TYPE,
                            new SignalTweakerComponent(component.color(), component.name(), component.privateNetwork(), component.freq(), component.type(), current));
                } else {
                    dir *= -1;
                    if (component.selected() == 0){
                        int current = component.color().getX();
                        if (dir > 0){
                            current -= payload.isHoldingControl ? 1 : 5;
                            if (current < 0){
                                current = 255;
                            }
                        }
                        if (dir < 0){
                            current += payload.isHoldingControl ? 1 : 5;
                            if (current > 255){
                                current = 0;
                            }
                        }

                        context.player().playSoundToPlayer(SoundEvents.ITEM_DYE_USE, SoundCategory.PLAYERS, 0.1F, 0.95F + context.player().getRandom().nextFloat() * 0.1F);

                        held.set(ModDataComponents.SIGNAL_TWEAKER_COMPONENT_COMPONENT_TYPE,
                                new SignalTweakerComponent(new Vec3i(current, component.color().getY(), component.color().getZ()), component.name(), component.privateNetwork(), component.freq(), component.type(), 0));
                    }
                    if (component.selected() == 1){
                        int current = component.color().getY();
                        if (dir > 0){
                            current -= payload.isHoldingControl ? 1 : 5;
                            if (current < 0){
                                current = 255;
                            }
                        }
                        if (dir < 0){
                            current += payload.isHoldingControl ? 1 : 5;
                            if (current > 255){
                                current = 0;
                            }
                        }

                        context.player().playSoundToPlayer(SoundEvents.ITEM_DYE_USE, SoundCategory.PLAYERS, 0.1F, 0.95F + context.player().getRandom().nextFloat() * 0.1F);

                        held.set(ModDataComponents.SIGNAL_TWEAKER_COMPONENT_COMPONENT_TYPE,
                                new SignalTweakerComponent(new Vec3i(component.color().getX(), current, component.color().getZ()), component.name(), component.privateNetwork(), component.freq(), component.type(),1));
                    }
                    if (component.selected() == 2){
                        int current = component.color().getZ();
                        if (dir > 0){
                            current -= payload.isHoldingControl ? 1 : 5;
                            if (current < 0){
                                current = 255;
                            }
                        }
                        if (dir < 0){
                            current += payload.isHoldingControl ? 1 : 5;
                            if (current > 255){
                                current = 0;
                            }
                        }

                        context.player().playSoundToPlayer(SoundEvents.ITEM_DYE_USE, SoundCategory.PLAYERS, 0.1F, 0.95F + context.player().getRandom().nextFloat() * 0.1F);

                        held.set(ModDataComponents.SIGNAL_TWEAKER_COMPONENT_COMPONENT_TYPE,
                                new SignalTweakerComponent(new Vec3i(component.color().getX(), component.color().getY(), current), component.name(), component.privateNetwork(), component.freq(), component.type(),2));
                    }
                    if (component.selected() == 3){
                        boolean current = component.privateNetwork();
                        if (dir > 0){
                            current = !current;
                        }
                        if (dir < 0){
                            current = !current;
                        }

                        context.player().playSoundToPlayer(current ? SoundEvents.BLOCK_VAULT_CLOSE_SHUTTER : SoundEvents.BLOCK_VAULT_OPEN_SHUTTER, SoundCategory.PLAYERS, 0.5F, 0.95F + context.player().getRandom().nextFloat() * 0.1F);

                        held.set(ModDataComponents.SIGNAL_TWEAKER_COMPONENT_COMPONENT_TYPE,
                                new SignalTweakerComponent(component.color(), component.name(), current, component.freq(), component.type(),3));
                    }
                    if (component.selected() == 4){
                        int current = component.freq();
                        if (dir > 0){
                            current += 3;
                            if (current > 12){
                                current = 3;
                            }
                        }
                        if (dir < 0){
                            current -= 3;
                            if (current < 3){
                                current = 12;
                            }
                        }

                        context.player().playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), SoundCategory.PLAYERS, 0.3F, 0.95F + context.player().getRandom().nextFloat() * 0.1F);

                        held.set(ModDataComponents.SIGNAL_TWEAKER_COMPONENT_COMPONENT_TYPE,
                                new SignalTweakerComponent(component.color(), component.name(), component.privateNetwork(), current, component.type(),4));
                    }
                    if (component.selected() == 5){
                        int current = component.type();
                        if (dir > 0){
                            current += 1;
                            if (current > 2){
                                current = 0;
                            }
                        }
                        if (dir < 0){
                            current -= 1;
                            if (current < 0){
                                current = 2;
                            }
                        }

                        context.player().playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), SoundCategory.PLAYERS, 0.3F, 0.95F + context.player().getRandom().nextFloat() * 0.1F);

                        held.set(ModDataComponents.SIGNAL_TWEAKER_COMPONENT_COMPONENT_TYPE,
                                new SignalTweakerComponent(component.color(), component.name(), component.privateNetwork(), component.freq(), current,5));
                    }
                }
            });
        }
    }
}

