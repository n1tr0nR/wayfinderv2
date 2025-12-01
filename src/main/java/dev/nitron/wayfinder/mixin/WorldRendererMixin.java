package dev.nitron.wayfinder.mixin;

import dev.nitron.wayfinder.client.sound.Signal1;
import dev.nitron.wayfinder.client.sound.Signal2;
import dev.nitron.wayfinder.client.sound.Signal3;
import dev.nitron.wayfinder.client.sound.SignalscopeAmbienceInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.sound.SoundInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow @Final private MinecraftClient client;
    @Unique
    SoundInstance ambience;

    @Unique
    SoundInstance s1;
    @Unique
    SoundInstance s2;
    @Unique
    SoundInstance s3;

    @Inject(method = "tick", at = @At("HEAD"))
    private void wayfinder$tick(Camera camera, CallbackInfo ci){
        ClientPlayerEntity player = this.client.player;
        if (player != null && (this.ambience == null || !this.client.getSoundManager().isPlaying(this.ambience))) {
            this.ambience = new SignalscopeAmbienceInstance(player);
            this.client.getSoundManager().play(this.ambience);
        }

        if (player != null && (this.s1 == null || !this.client.getSoundManager().isPlaying(this.s1))) {
            this.s1 = new Signal1(player);
            this.client.getSoundManager().play(this.s1);
        }
        if (player != null && (this.s2 == null || !this.client.getSoundManager().isPlaying(this.s2))) {
            this.s2 = new Signal2(player);
            this.client.getSoundManager().play(this.s2);
        }
        if (player != null && (this.s3 == null || !this.client.getSoundManager().isPlaying(this.s3))) {
            this.s3 = new Signal3(player);
            this.client.getSoundManager().play(this.s3);
        }
    }
}
