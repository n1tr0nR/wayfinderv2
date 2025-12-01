package dev.nitron.wayfinder.client;

import dev.nitron.wayfinder.Wayfinder;
import dev.nitron.wayfinder.client.hud.SignalscopeHand;
import dev.nitron.wayfinder.init.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.util.Identifier;

public class WayfinderClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.HOTBAR, Identifier.of(Wayfinder.MOD_ID, "signalscope_hand"), new SignalscopeHand());

        BlockRenderLayerMap.putBlock(ModBlocks.SIGNAL_ARRAY, BlockRenderLayer.CUTOUT);
    }
}
