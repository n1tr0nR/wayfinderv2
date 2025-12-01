package dev.nitron.wayfinder.mixin;

import dev.nitron.wayfinder.client.AdvancedTooltipBackgroundRenderer;
import dev.nitron.wayfinder.content.item.SignalTweakerItem;
import dev.nitron.wayfinder.content.item.SignalscopeItem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(HandledScreen.class)
public abstract class DrawContextMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
    @Shadow @Nullable protected Slot focusedSlot;

    @Shadow @Final protected T handler;

    @Shadow protected abstract boolean isItemTooltipSticky(ItemStack item);

    @Shadow protected abstract List<Text> getTooltipFromItem(ItemStack stack);

    protected DrawContextMixin(Text title) {
        super(title);
    }

    @Inject(method = "drawMouseoverTooltip", at = @At(value = "HEAD"), cancellable = true)
    private void brushstroke$changeToAdvanced(DrawContext context, int x, int y, CallbackInfo ci){
        if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            ItemStack itemStack = this.focusedSlot.getStack();
            if (this.handler.getCursorStack().isEmpty() || this.isItemTooltipSticky(itemStack)) {
                if (itemStack.getItem() instanceof SignalscopeItem){
                    AdvancedTooltipBackgroundRenderer renderer = new AdvancedTooltipBackgroundRenderer(context);
                    renderer.drawTooltip(this.textRenderer, this.getTooltipFromItem(itemStack), itemStack.getTooltipData(), x, y, (Identifier)itemStack.get(DataComponentTypes.TOOLTIP_STYLE), itemStack);
                    ci.cancel();
                }
                if (itemStack.getItem() instanceof SignalTweakerItem){
                    AdvancedTooltipBackgroundRenderer renderer = new AdvancedTooltipBackgroundRenderer(context);
                    renderer.drawTooltip(this.textRenderer, this.getTooltipFromItem(itemStack), itemStack.getTooltipData(), x, y, (Identifier)itemStack.get(DataComponentTypes.TOOLTIP_STYLE), itemStack);
                    ci.cancel();
                }
            }
        }
    }
}
