package dev.nitron.wayfinder;

import dev.nitron.wayfinder.content.commands.SignalCommand;
import dev.nitron.wayfinder.init.ModBlocks;
import dev.nitron.wayfinder.init.ModDataComponents;
import dev.nitron.wayfinder.init.ModItems;
import dev.nitron.wayfinder.init.ModSounds;
import dev.nitron.wayfinder.networking.c2s.SignalTweakerSlotC2SPayload;
import dev.nitron.wayfinder.networking.c2s.SignalscopeChangeC2SPayload;
import dev.nitron.wayfinder.networking.c2s.SignalscopeSlotC2SPayload;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ItemEvent;

public class Wayfinder implements ModInitializer {
	public static final String MOD_ID = "wayfinder";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.init();
		ModDataComponents.init();
		ModSounds.init();
		ModBlocks.init();

		CommandRegistrationCallback.EVENT.register(SignalCommand::register);

		PayloadTypeRegistry.playC2S().register(SignalscopeChangeC2SPayload.ID, SignalscopeChangeC2SPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SignalscopeChangeC2SPayload.ID, new SignalscopeChangeC2SPayload.Receiver());

		PayloadTypeRegistry.playC2S().register(SignalscopeSlotC2SPayload.ID, SignalscopeSlotC2SPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SignalscopeSlotC2SPayload.ID, new SignalscopeSlotC2SPayload.Receiver());

		PayloadTypeRegistry.playC2S().register(SignalTweakerSlotC2SPayload.ID, SignalTweakerSlotC2SPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SignalTweakerSlotC2SPayload.ID, new SignalTweakerSlotC2SPayload.Receiver());
	}

	public static void grantAdvancement(PlayerEntity player, Identifier identifier, String criterion){
		if (!(player instanceof ServerPlayerEntity serverPlayer)) return;
		MinecraftServer server = serverPlayer.getEntityWorld().getServer();
        AdvancementEntry advancement = server.getAdvancementLoader().get(identifier);
		serverPlayer.getAdvancementTracker().grantCriterion(advancement, criterion);
	}
}