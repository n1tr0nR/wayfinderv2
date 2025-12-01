package dev.nitron.wayfinder.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.nitron.wayfinder.content.item.SignalscopeItem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow private ItemStack currentStack;

    @WrapOperation(method = "renderHeldItemTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithBackground(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIII)V"))
    private void wayfinder$renderHeldItemTooltip(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int width, int color, Operation<Void> original){
        if (this.currentStack.getItem() instanceof SignalscopeItem){
            original.call(instance, textRenderer, text, x, y - 13, width, color);
            return;
        }
        original.call(instance, textRenderer, text, x, y, width, color);
    }
}
