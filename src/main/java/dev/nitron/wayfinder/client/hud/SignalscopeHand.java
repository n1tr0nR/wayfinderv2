package dev.nitron.wayfinder.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.nitron.wayfinder.Wayfinder;
import dev.nitron.wayfinder.content.cca.SignalPlacementsComponent;
import dev.nitron.wayfinder.content.data_component.SignalscopeComponent;
import dev.nitron.wayfinder.content.item.SignalscopeItem;
import dev.nitron.wayfinder.init.ModDataComponents;
import dev.nitron.wayfinder.init.ModItems;
import dev.nitron.wayfinder.util.Math;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class SignalscopeHand implements HudElement {
    @Override
    public void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        Identifier TEXTURE = Identifier.of(Wayfinder.MOD_ID, "textures/gui/sprites/hud/signalscope.png");
        Identifier WAVES = Identifier.of(Wayfinder.MOD_ID, "hud/audio_waves");

        if (MinecraftClient.getInstance().player == null || !(MinecraftClient.getInstance().player.getMainHandStack().getItem() instanceof SignalscopeItem))
            return;

        drawContext.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                drawContext.getScaledWindowWidth() / 2 - 52,
                drawContext.getScaledWindowHeight() - 47 - (MinecraftClient.getInstance().player.isCreative() ? 0 : 20),
                0,
                0,
                104,
                26,
                128,
                64
        );
        SignalscopeComponent component = MinecraftClient.getInstance().player.getMainHandStack().get(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE);
        if (component == null) return;
        drawContext.drawText(
                MinecraftClient.getInstance().textRenderer,
                component.frequency() + "hz",
                drawContext.getScaledWindowWidth() / 2 - 45,
                drawContext.getScaledWindowHeight() - 34  - (MinecraftClient.getInstance().player.isCreative() ? 0 : 20),
                0xFFf4e063,
                true
        );

        drawContext.drawText(
                MinecraftClient.getInstance().textRenderer,
                component.gain() + "mm",
                drawContext.getScaledWindowWidth() / 2 + 5,
                drawContext.getScaledWindowHeight() - 34  - (MinecraftClient.getInstance().player.isCreative() ? 0 : 20),
                0xFFf4e063,
                true
        );

        //Render Audio Waves
        int level = component.frequency() / 3 - 1;
        int width = 64;
        int height = 32;
        float scrollSpeed = 0;
        switch (level){
            case 0 -> scrollSpeed = 0.15F;
            case 1, 2, 3 -> scrollSpeed = 0.1F;
        }

        long time = MinecraftClient.getInstance().world.getTimeOfDay() % 24000;
        float partialTicks = renderTickCounter.getTickProgress(false);
        float totalTime = time + partialTicks;
        float scroll = (totalTime * scrollSpeed * width) % width;
        float nScroll = (totalTime * scrollSpeed * 0.5F * width) % width;

        drawContext.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                WAVES,
                256,
                256,
                (int) scroll,
                level * 48,
                drawContext.getScaledWindowWidth() / 2,
                drawContext.getScaledWindowHeight() - 100,
                width,
                height,
                0x90FFFFFF
        );

        drawContext.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                WAVES,
                256,
                256,
                (int) scroll,
                level * 48,
                drawContext.getScaledWindowWidth() / 2 - width,
                drawContext.getScaledWindowHeight() - 100,
                width,
                height,
                0x90FFFFFF
        );

        drawContext.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                WAVES,
                256,
                256,
                (int) nScroll,
                level * 48,
                drawContext.getScaledWindowWidth() / 2,
                drawContext.getScaledWindowHeight() - 100,
                width,
                height,
                0x50FFFFFF
        );

        drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, WAVES, 256, 256, (int) nScroll, level * 48, drawContext.getScaledWindowWidth() / 2 - width, drawContext.getScaledWindowHeight() - 100, width, height, 0x50FFFFFF);
        drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, WAVES, 256, 256, (int) -nScroll + width, level * 48, drawContext.getScaledWindowWidth() / 2, drawContext.getScaledWindowHeight() - 100, width, height, 0x30FFFFFF);
        drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, WAVES, 256, 256, (int) -nScroll + width, level * 48, drawContext.getScaledWindowWidth() / 2 - width, drawContext.getScaledWindowHeight() - 100, width, height, 0x30FFFFFF);
        drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, WAVES, 256, 256, 0, 192, drawContext.getScaledWindowWidth() / 2 - width - 2, drawContext.getScaledWindowHeight() - 103, 132, 48, 0xFFFFFFFF);

        if (component.lensItemstack().isEmpty()) return;

        //Gain
        int gainValue = (int) ((component.gain() - 30) * 2) / 2;
        Identifier GAIN = Identifier.of(Wayfinder.MOD_ID, "hud/gain/gain" + (gainValue + 1));

        drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, GAIN, 80, 80, 0, 0, drawContext.getScaledWindowWidth() / 2 - 40, drawContext.getScaledWindowHeight() / 2 - 40, 80, 80, 0x50faeca4);
        double maxDis = (component.upgradeItemstack().isOf(ModItems.SIGNAL_EXPANDER) ? 500 : component.upgradeItemstack().isOf(ModItems.SIGNAL_BOOSTER) ? 300 : 200) * (((component.gain() - 30) + 10) / 2);

        drawContext.drawText(
                MinecraftClient.getInstance().textRenderer,
                "Range: " + (int) maxDis + "m",
                drawContext.getScaledWindowWidth() / 2 - (MinecraftClient.getInstance().textRenderer.getWidth("Range: " + (int) maxDis + "m") / 2),
                drawContext.getScaledWindowHeight() / 2 + 50,
                0xFFfaeca4,
                true
        );


        //Actual Signals
        Random random = Random.create();

        BlockPos lookedAtSignal = Math.getLookedAtSignal(MinecraftClient.getInstance().player, SignalPlacementsComponent.get(MinecraftClient.getInstance().world).getSignalPositions(), 15.65F, maxDis, component);
        if ((component.gain() - 30) / 2 >= 1 && lookedAtSignal != null){
            lookedAtSignal = lookedAtSignal.add(new Vec3i(random.nextInt((int) ((component.gain() - 30) / 2)), random.nextInt((int) ((component.gain() - 30) / 2)), random.nextInt((int) ((component.gain() - 30) / 2))));
        }

        BlockPos finalLookedAtSignal = lookedAtSignal;
        SignalPlacementsComponent.SignalData signal = SignalPlacementsComponent.get(MinecraftClient.getInstance().world).getSignalPositions().stream()
                .filter(signalData -> signalData.pos.equals(finalLookedAtSignal))
                .findFirst()
                .orElse(null);

        for (SignalPlacementsComponent.SignalData data : SignalPlacementsComponent.get(MinecraftClient.getInstance().world).getSignalPositions()){
            BlockPos signalPos = data.pos;
            if ((component.gain() - 30) / 2 >= 1 ){
               signalPos = signalPos.add(new Vec3i(random.nextInt((int) ((component.gain() - 30) / 2)), random.nextInt((int) ((component.gain() - 30) / 2)), random.nextInt((int) ((component.gain() - 30) / 2))));
            }

            if (data.isPowered && !MinecraftClient.getInstance().player.getUuid().equals(data.owner)) continue;

            if (component.lensItemstack().isOf(ModItems.PRIVACY_LENS) && !MinecraftClient.getInstance().player.getUuid().equals(data.owner)) continue;
            if (component.lensItemstack().isOf(ModItems.VANTAGE_LENS) && MinecraftClient.getInstance().player.getUuid().equals(data.owner)) continue;
            if (component.frequency() != data.frequency) continue;
            if (component.lensItemstack().isEmpty()) continue;

            float factor = (float) (1.0 - Math.getLookFactor(
                    MinecraftClient.getInstance().player,
                    signalPos,
                    75F,
                    renderTickCounter.getTickProgress(false)
            ));

            int maxDistance = (int) (130 * factor);

            Vec3d targetColor = new Vec3d(1.0F, 1.0F, 1.0F);
            if (factor == 0) {
                targetColor = new Vec3d(0.3F, 1.0F, 0.6F);
                if (signal != null){
                    targetColor = new Vec3d((double) signal.color.getX() / 255, (double) signal.color.getY() / 255, (double) signal.color.getZ() / 255);
                }
            } else {
                targetColor = new Vec3d((double) 255 / 255, (double) 78 / 255, (double) 78 / 255);
            }

            double distanceToSignal = MinecraftClient.getInstance().player.getEntityPos().distanceTo(Vec3d.ofCenter(signalPos));

            float fadeFactor = 1.0f;
            float fadeStart = (float) (maxDis - (maxDis / 10));
            float fadeEnd = (float) maxDis;

            if (distanceToSignal > fadeStart) {
                if (distanceToSignal >= fadeEnd) {
                    fadeFactor = 0f;
                } else {
                    fadeFactor = 1.0f - (float)((distanceToSignal - fadeStart) / (fadeEnd - fadeStart));
                }
            }

            float alpha1 = 1.0F - factor;
            alpha1 *= fadeFactor;

            Vec3i vec3i = new Vec3i((int) (targetColor.x * 255), (int) (targetColor.y * 255), (int) (targetColor.z * 255));
            int r  = vec3i.getX();
            int g  = vec3i.getY();
            int b  = vec3i.getZ();
            int a = (int)(255 * alpha1);
            a = java.lang.Math.clamp(a, 0, 255);
            int color = (a << 24) | (r << 16) | (g << 8) | b;

            drawContext.drawGuiTexture(
                    RenderPipelines.GUI_TEXTURED,
                    GAIN,
                    80,
                    80,
                    0,
                    0,
                    drawContext.getScaledWindowWidth() / 2 - 40 - maxDistance,
                    drawContext.getScaledWindowHeight() / 2 - 40,
                    40,
                    80,
                    color
            );

            drawContext.drawGuiTexture(
                    RenderPipelines.GUI_TEXTURED,
                    GAIN,
                    80,
                    80,
                    40,
                    0,
                    drawContext.getScaledWindowWidth() / 2 + maxDistance,
                    drawContext.getScaledWindowHeight() / 2 - 40,
                    40,
                    80,
                    color
            );
        }

        if (signal != null){
            double distance = MinecraftClient.getInstance().player.getEntityPos().distanceTo(Vec3d.ofCenter(signal.pos));
            Text text = Text.literal("Signal: " + String.format("%.0f", distance) + "m").formatted(Formatting.BOLD);
            int color = 0xFF4cff99;

            if (signal.isPowered && !MinecraftClient.getInstance().player.getUuid().equals(signal.owner)) return;

            if (component.lensItemstack().isOf(ModItems.PRIVACY_LENS) && !MinecraftClient.getInstance().player.getUuid().equals(signal.owner)) return;
            if (component.lensItemstack().isOf(ModItems.VANTAGE_LENS) && MinecraftClient.getInstance().player.getUuid().equals(signal.owner)) return;
            if (component.frequency() != signal.frequency) return;
            if (component.lensItemstack().isEmpty()) return;

            float fadeFactor = 1.0f;
            float fadeStart = (float) (maxDis - (maxDis / 10));
            float fadeEnd = (float) maxDis;

            if (distance > fadeStart) {
                if (distance >= fadeEnd) {
                    fadeFactor = 0f;
                } else {
                    fadeFactor = 1.0f - (float)((distance - fadeStart) / (fadeEnd - fadeStart));
                }
            }

            text = Text.literal(signal.name + ": " + String.format("%.0f", distance) + "m").formatted(Formatting.BOLD);
            Vec3i vec3i = signal.color;
            int r  = vec3i.getX();
            int g  = vec3i.getY();
            int b  = vec3i.getZ();
            int a = (int)(255 * fadeFactor);
            a = java.lang.Math.clamp(a, 0, 255);
            color = (a << 24) | (r << 16) | (g << 8) | b;

            if (distance < (fadeEnd)){
                drawContext.drawText(
                        MinecraftClient.getInstance().textRenderer,
                        text,
                        (drawContext.getScaledWindowWidth() / 2) - (MinecraftClient.getInstance().textRenderer.getWidth(text) / 2), (int) (((double) drawContext.getScaledWindowHeight() / 2) - 50 + (component.gain() * 2) - 60), color, true
                );
            }
        }
    }

    private static int getColor(float factor, Vec3i col, boolean isLookedAt) {
        int alpha = (int) ((1.0 - factor) * 255);
        int red = 255;
        int green = 78;
        int blue = 78;

        if (isLookedAt || factor == 1) {
            red = col.getX();
            green = col.getY();
            blue = col.getZ();
        }

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
