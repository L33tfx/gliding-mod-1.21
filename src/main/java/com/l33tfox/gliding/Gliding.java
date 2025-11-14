package com.l33tfox.gliding;

import com.l33tfox.gliding.config.GlidingServersideConfig;
import com.l33tfox.gliding.items.ModItemsRegistry;
import com.l33tfox.gliding.networking.ModPacketsRegistry;
import com.l33tfox.gliding.networking.payload.GlidingConfigSyncS2CPayload;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gliding implements ModInitializer {
	public static final String MOD_ID = "gliding";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static GlidingServersideConfig SERVER_CONFIG;

	@Override
	public void onInitialize() {
		ModItemsRegistry.initialize();

		// Registered on common side (both client and server)
		ModPacketsRegistry.registerC2SPackets();
		ModPacketsRegistry.registerS2CPackets();
		ModPacketsRegistry.registerC2SReceivers();

		AutoConfig.register(GlidingServersideConfig.class, GsonConfigSerializer::new);
		SERVER_CONFIG = AutoConfig.getConfigHolder(GlidingServersideConfig.class).getConfig();

		// Synchronize server config with clients for use in GliderClientUtil
		ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
			ServerPlayNetworking.send(handler.player, new GlidingConfigSyncS2CPayload(SERVER_CONFIG.offHandEnabled));
		}));
	}
}