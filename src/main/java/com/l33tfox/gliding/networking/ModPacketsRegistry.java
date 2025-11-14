package com.l33tfox.gliding.networking;

import com.l33tfox.gliding.networking.payload.GliderActivatedC2SPayload;
import com.l33tfox.gliding.networking.payload.GlidingConfigSyncS2CPayload;
import com.l33tfox.gliding.networking.payload.OtherPlayerGliderActivatedS2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ModPacketsRegistry {

    public static void registerC2SPackets() {
        PayloadTypeRegistry.playC2S().register(GliderActivatedC2SPayload.ID, GliderActivatedC2SPayload.CODEC);
    }

    public static void registerC2SReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(GliderActivatedC2SPayload.ID, C2SPacketHandler::receiveGliderActivated);
    }

    public static void registerS2CPackets() {
        PayloadTypeRegistry.playS2C().register(OtherPlayerGliderActivatedS2CPayload.ID, OtherPlayerGliderActivatedS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GlidingConfigSyncS2CPayload.ID, GlidingConfigSyncS2CPayload.CODEC);
    }

    public static void registerS2CReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(OtherPlayerGliderActivatedS2CPayload.ID, S2CPacketHandler::receiveOtherGliderActivated);
        ClientPlayNetworking.registerGlobalReceiver(GlidingConfigSyncS2CPayload.ID, S2CPacketHandler::receiveConfigSync);
    }
}
