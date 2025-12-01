package dev.nitron.wayfinder.mixin;

import dev.nitron.wayfinder.Wayfinder;
import dev.nitron.wayfinder.content.data_component.SignalscopeComponent;
import dev.nitron.wayfinder.content.item.SignalTweakerItem;
import dev.nitron.wayfinder.content.item.SignalscopeItem;
import dev.nitron.wayfinder.init.ModDataComponents;
import dev.nitron.wayfinder.networking.c2s.SignalTweakerSlotC2SPayload;
import dev.nitron.wayfinder.networking.c2s.SignalscopeChangeC2SPayload;
import dev.nitron.wayfinder.networking.c2s.SignalscopeSlotC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScrollUnified(long window, double horizontal, double vertical, CallbackInfo ci) {
        boolean altDown = InputUtil.isKeyPressed(this.client.getWindow(), GLFW.GLFW_KEY_LEFT_ALT)
                || InputUtil.isKeyPressed(this.client.getWindow(), GLFW.GLFW_KEY_RIGHT_ALT);

        boolean ctrlDown = InputUtil.isKeyPressed(this.client.getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL)
                || InputUtil.isKeyPressed(this.client.getWindow(), GLFW.GLFW_KEY_RIGHT_CONTROL);

        if (this.client.currentScreen instanceof HandledScreen<?> screen && window == this.client.getWindow().getHandle()) {
            Slot hoveredSlot = screen.focusedSlot;
            if (hoveredSlot == null) {
                return;
            }

            ItemStack hoveredStack = hoveredSlot.getStack();
            if (hoveredStack.getItem() instanceof SignalscopeItem) {
                SignalscopeComponent comp = hoveredStack.get(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE);
                if (comp == null) return;

                int selectedIndex = comp.selected();
                if (vertical < 0) {
                    selectedIndex++;
                    if (selectedIndex > 2) selectedIndex = 1;
                } else if (vertical > 0) {
                    selectedIndex--;
                    if (selectedIndex < 1) selectedIndex = 2;
                }

                SignalscopeSlotC2SPayload payload = new SignalscopeSlotC2SPayload(
                        hoveredSlot.id,
                        selectedIndex
                );
                ClientPlayNetworking.send(payload);
                ci.cancel();
                return;
            } else if (hoveredStack.getItem() instanceof SignalTweakerItem){
                int direction = 0;
                if (vertical < 0){
                    direction = -1;
                }
                if (vertical > 0){
                    direction = 1;
                }
                SignalTweakerSlotC2SPayload payload = new SignalTweakerSlotC2SPayload(
                        hoveredSlot.id,
                        direction,
                        altDown,
                        ctrlDown
                );
                ClientPlayNetworking.send(payload);
                ci.cancel();
                return;
            } else {
                return;
            }
        }

        if (this.client.player == null || this.client.currentScreen != null) return;

        if (!altDown) return;

        ItemStack held = this.client.player.getMainHandStack();
        if (!(held.getItem() instanceof SignalscopeItem)) return;

        ci.cancel();

        SignalscopeComponent comp = held.get(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE);
        if (comp == null) return;

        SignalscopeChangeC2SPayload payload = getSignalscopeChangeC2SPayload(vertical, comp);
        ClientPlayNetworking.send(payload);
    }


    @Unique
    private @NotNull SignalscopeChangeC2SPayload getSignalscopeChangeC2SPayload(double vertical, SignalscopeComponent comp) {
        int oldFrequency = comp.frequency();
        double oldGain = comp.gain();
        int newFrequency = oldFrequency;
        double newGain = oldGain;

        boolean crouching = this.client.player.isSneaking();

        if (!crouching) {
            if (vertical > 0) newFrequency += 3;
            else if (vertical < 0) newFrequency -= 3;
            newFrequency = Math.max(3, Math.min(12, newFrequency));
        } else {
            if (vertical > 0) newGain += 1;
            else if (vertical < 0) newGain -= 1;
            newGain = Math.max(30.0, Math.min(40.0, newGain));
        }

        return new SignalscopeChangeC2SPayload(oldFrequency, oldGain, newFrequency, newGain);
    }
}
