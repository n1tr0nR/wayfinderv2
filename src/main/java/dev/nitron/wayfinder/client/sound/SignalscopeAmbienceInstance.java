package dev.nitron.wayfinder.client.sound;

import dev.nitron.wayfinder.init.ModDataComponents;
import dev.nitron.wayfinder.init.ModItems;
import dev.nitron.wayfinder.init.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class SignalscopeAmbienceInstance extends MovingSoundInstance {
    private final ClientPlayerEntity player;
    private float targetVolume = 0.0F;
    private float targetPitch = 1.0F;

    public SignalscopeAmbienceInstance(ClientPlayerEntity player) {
        super(ModSounds.SIGNALSCOPE_AMBIENT, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.01F;
        this.relative = true;
    }

    @Override
    public void tick() {
        // track the frequency, increase the pitch
        // if not holding signalscope, fade out

        if (player.isRemoved() || player.isDead()) {
            this.setDone();
            return;
        }
        ItemStack stack = player.getMainHandStack();
        boolean holdingScope = stack.isOf(ModItems.SIGNALSCOPE);
        targetVolume = holdingScope ? 0.0F : 0.0F;

        if (holdingScope) {
            int fq = stack.get(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE).frequency();
            int normal = fq / 3;
            float normalized = (normal - 1) / 3.0F;
            targetPitch = 1.0F + normalized;
        } else {
            targetPitch = 1.0F;
        }

        this.volume = MathHelper.lerp(MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false), this.volume, targetVolume);
        this.pitch = MathHelper.lerp(MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false), this.pitch, targetPitch);

        if (this.volume < 0.01F && !holdingScope) {
            this.volume = 0.0F;
        }
    }
}
