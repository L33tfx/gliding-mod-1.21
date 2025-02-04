package com.l33tfox.gliding.mixin;

import com.l33tfox.gliding.PlayerEntityDuck;
import com.l33tfox.gliding.items.GliderItem;
import com.l33tfox.gliding.util.GliderUtil;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> {

    private float prevLegPitch = 0;
    private float prevArmPitch = 0;

    @Shadow @Final public ModelPart leftArm;
    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftLeg;
    @Shadow @Final public ModelPart rightLeg;

    @Inject(at = @At("TAIL"), method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V")
    private void setLimbsGliding(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity player) {
            if (((PlayerEntityDuck) player).gliding$isActivatingGlider()) {
                if (player.getOffHandStack().isEmpty() || player.getOffHandStack().getItem() instanceof GliderItem) {
                    leftArm.pitch = (float) Math.PI;
                    leftArm.roll = (float) Math.PI / 16;
                }

                if (player.getMainHandStack().isEmpty() || player.getMainHandStack().getItem() instanceof GliderItem) {
                    rightArm.pitch = (float) Math.PI;
                    rightArm.roll = (float) -Math.PI / 16;
                }

                if (((PlayerEntityDuck) player).gliding$isGliding() && !player.isOnGround()) {
                    // only set legs straight if they were already straight, otherwise first allow stepping leg swing
                    // animation to finish
                    if (prevLegPitch == 0 || Math.abs(leftLeg.pitch) < Math.PI / 32) {
                        leftLeg.pitch = 0;
                        rightLeg.pitch = 0;
                    }

                    // if either hand is holding an item other than a glider while gliding
                    if ((!player.getMainHandStack().isEmpty() && !GliderUtil.mainHandHoldingGlider(player)) ||
                       (!player.getOffHandStack().isEmpty() && !GliderUtil.offHandHoldingGlider(player))) {
                        ModelPart armHoldingOtherItem = GliderUtil.mainHandHoldingGlider(player) ? leftArm : rightArm;

                        if ((prevArmPitch == (float) -Math.PI / 16 || Math.abs(armHoldingOtherItem.pitch) < Math.PI / 16) && !player.isUsingItem())
                            armHoldingOtherItem.pitch = (float) -Math.PI / 16;

                        prevArmPitch = armHoldingOtherItem.pitch;
                    }
                } else prevArmPitch = leftArm.pitch;

                prevLegPitch = leftLeg.pitch;

            }

        }
    }

}
