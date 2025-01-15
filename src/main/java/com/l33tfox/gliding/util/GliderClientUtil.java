package com.l33tfox.gliding.util;

import com.l33tfox.gliding.PlayerEntityDuck;
import com.l33tfox.gliding.client.sound.GliderSoundManager;
import com.l33tfox.gliding.client.sound.GlidingWindSoundInstance;
import com.l33tfox.gliding.networking.payload.GliderActivatedC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/*
A bunch of client-side utility methods for using gliders.
 */

public class GliderClientUtil {

    public static int ticksUsingGlider = 0;

    public static boolean isGliderOpened = false;

    public static boolean isActivatingGlider(ClientPlayerEntity player) {
        Vec3d velocity = player.getVelocity();
        return GliderUtil.isHoldingGlider(player) && player.input.jumping && !player.getAbilities().flying && (velocity.y <= -0.45 || isGliderOpened) && !player.isSwimming() && !player.isInFluid();
    }

    public static boolean isUsingGliderMoreThanOneJump() {
        return ticksUsingGlider > 4;
    }

    public static boolean isGliding(ClientPlayerEntity player) {
        if (isActivatingGlider(player) && isUsingGliderMoreThanOneJump() && !player.isOnGround()
                && !player.isInFluid() && !player.isFallFlying()){
            isGliderOpened = true;
            return true;
        }
        isGliderOpened = false;
        return false;
    }

    // Called at the end of every tick on the client using a CLIENT_END_TICK event registered in GlidingClient class
    public static void glidingTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;

        // if player is in a world and is activating the glider
        if (player != null && GliderClientUtil.isActivatingGlider(player)) {
            ticksUsingGlider++;

            // if player is already gliding or in a state to glide
            if (GliderClientUtil.isGliding(player)) {

                // move the player on the client side
                GliderUtil.playerGliderMovement(player);
                GliderUtil.resetFallDamage(player);

                GliderSoundManager gliderSoundManager = GliderSoundManager.getInstance();
                gliderSoundManager.play(new GlidingWindSoundInstance(player, gliderSoundManager));

                // update the player model on this client
                ((PlayerEntityDuck) player).gliding$setIsActivatingGlider(true);
                ((PlayerEntityDuck) player).gliding$setIsGliding(true);

                // send a packet to the server so that it can move the player on the server side as well, and also
                // send packets to nearby players' clients who are tracking this player to update the player model
                ClientPlayNetworking.send(new GliderActivatedC2SPayload(true, true));

            // if the player is activating the glider but not gliding
            } else if (GliderClientUtil.isUsingGliderMoreThanOneJump()) {
                ((PlayerEntityDuck) player).gliding$setIsActivatingGlider(true);
                ((PlayerEntityDuck) player).gliding$setIsGliding(false);
                ClientPlayNetworking.send(new GliderActivatedC2SPayload(true, false));
            }

        // if the player exists and was previously activating glider but not anymore, send packets to update
        } else if (player != null && ((PlayerEntityDuck) player).gliding$isActivatingGlider()) {
            ticksUsingGlider = 0;
            ((PlayerEntityDuck) player).gliding$setIsActivatingGlider(false);
            ((PlayerEntityDuck) player).gliding$setIsGliding(false);
            ClientPlayNetworking.send(new GliderActivatedC2SPayload(false, false));
        }
    }
}
