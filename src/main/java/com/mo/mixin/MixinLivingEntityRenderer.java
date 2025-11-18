package com.mo.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mo.module.player.rotaion.Rotations;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer {

    @ModifyExpressionValue(
            method = "render*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"
            )
    )
    private float hookPitch(float original, LivingEntity entity, float f, float g) {
        if (entity != MinecraftClient.getInstance().player) return original;
        if (!Rotations.INSTANCE.getEnabled()) return original;
        if (Rotations.INSTANCE.getModelRotation() != null && Rotations.INSTANCE.getModelPrevRotation() != null) {
            return MathHelper.lerpAngleDegrees(
                    g,
                    Rotations.INSTANCE.getModelPrevRotation().getPitch(),
                    Rotations.INSTANCE.getModelRotation().getPitch()
            );
        }
        return original;
    }

    @ModifyExpressionValue(
            method = "render*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/MathHelper;lerpAngleDegrees(FFF)F",
                    ordinal = 0
            )
    )
    private float hookBodyYaw(float original, LivingEntity entity, float f, float g) {
        if (entity != MinecraftClient.getInstance().player) return original;
        if (!Rotations.INSTANCE.getEnabled()) return original;
        if (Rotations.INSTANCE.getModelRotation() != null && Rotations.INSTANCE.getModelPrevRotation() != null) {
            return MathHelper.lerpAngleDegrees(
                    g,
                    Rotations.INSTANCE.getModelPrevRotation().getYaw(),
                    Rotations.INSTANCE.getModelRotation().getYaw()
            );
        }
        return original;
    }

    @ModifyExpressionValue(
            method = "render*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/MathHelper;lerpAngleDegrees(FFF)F",
                    ordinal = 1
            )
    )
    private float hookHeadYaw(float original, LivingEntity entity, float f, float g) {
        if (entity != MinecraftClient.getInstance().player) return original;
        if (!Rotations.INSTANCE.getEnabled()) return original;
        if (Rotations.INSTANCE.getModelRotation() != null && Rotations.INSTANCE.getModelPrevRotation() != null) {
            return MathHelper.lerpAngleDegrees(
                    g,
                    Rotations.INSTANCE.getModelPrevRotation().getYaw(),
                    Rotations.INSTANCE.getModelRotation().getYaw()
            );
        }
        return original;
    }
}
