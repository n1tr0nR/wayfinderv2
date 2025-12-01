package dev.nitron.wayfinder.client.sound;

import dev.nitron.wayfinder.content.cca.SignalSoundsComponent;
import dev.nitron.wayfinder.init.ModComponents;
import dev.nitron.wayfinder.init.ModDataComponents;
import dev.nitron.wayfinder.init.ModItems;
import dev.nitron.wayfinder.init.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;

public class Signal1 extends MovingSoundInstance {
    private final ClientPlayerEntity player;
    private float targetVolume = 0.0F;

    public Signal1(ClientPlayerEntity player) {
        super(ModSounds.SIGNAL_1, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.01F;
        this.relative = true;
    }

    @Override
    public void tick() {
        if (player.isRemoved() || player.isDead()) {
            this.setDone();
            return;
        }
        ItemStack stack = player.getMainHandStack();
        boolean holdingScope = stack.isOf(ModItems.SIGNALSCOPE);

        targetVolume = holdingScope ? 0.5F : 0.0F;

        SignalSoundsComponent component = ModComponents.SOUNDS.get(this.player);
        this.volume = MathHelper.lerp(MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false), this.volume, targetVolume) * component.getFactor0();

        if (this.volume <= 0.01 && !holdingScope) {
            this.volume = 0.0F;
        }
    }
}
