package dev.nitron.wayfinder.mixin;

import dev.nitron.wayfinder.Wayfinder;
import dev.nitron.wayfinder.content.item.SignalscopeItem;
import dev.nitron.wayfinder.init.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow public abstract ItemStack getSelectedStack();

    @Shadow public abstract ItemStack getStack(int slot);

    @Shadow @Final private DefaultedList<ItemStack> main;

    @Shadow @Final public PlayerEntity player;

    @Inject(method = "setSelectedSlot", at = @At("HEAD"))
    private void playsound(int slot, CallbackInfo ci){
        boolean old = this.getSelectedStack().getItem() instanceof SignalscopeItem;
        boolean newIsSignalscope = this.main.get(slot).getItem() instanceof SignalscopeItem;
        Wayfinder.grantAdvancement(player, Identifier.of(Wayfinder.MOD_ID, "a_plus_hearing"), "incode");
        if (!old && newIsSignalscope){
            this.player.playSound(ModSounds.SIGNALSCOPE, 1.0F, 0.9F + (this.player.getRandom().nextFloat() * 0.2F));
            this.player.playSound(SoundEvents.ITEM_SPYGLASS_USE, 1.0F, 1.0F);
        }
    }
}
