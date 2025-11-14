package com.l33tfox.gliding.networking.payload;

import com.l33tfox.gliding.Gliding;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record GlidingConfigSyncS2CPayload(boolean offHandEnabled) implements CustomPayload {

    public static final Identifier GLIDING_CONFIG_SYNC_ID = Identifier.of(Gliding.MOD_ID, "configsyncs2c");

    public static final CustomPayload.Id<GlidingConfigSyncS2CPayload> ID =
            new CustomPayload.Id<>(GLIDING_CONFIG_SYNC_ID);

    public static final PacketCodec<RegistryByteBuf, GlidingConfigSyncS2CPayload> CODEC =
            PacketCodec.tuple(PacketCodecs.BOOL, GlidingConfigSyncS2CPayload::offHandEnabled, GlidingConfigSyncS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
