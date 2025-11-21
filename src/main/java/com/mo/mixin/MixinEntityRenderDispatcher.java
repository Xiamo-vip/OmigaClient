package com.mo.mixin;

import com.mo.utils.IEntityRotations;
import com.mo.utils.RotationManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {

    @Inject(method = "render", at = @At("HEAD"))
    private <E extends Entity> void renderPre(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity == MinecraftClient.getInstance().player && RotationManager.INSTANCE.isActive()) {
            ((IEntityRotations) entity).omiga_saveOriginalRotations();

            float partialYaw = MathHelper.lerp(tickDelta, RotationManager.INSTANCE.getPrevRenderYaw(), RotationManager.INSTANCE.getRenderYaw());
            float partialPitch = MathHelper.lerp(tickDelta, RotationManager.INSTANCE.getPrevRenderPitch(), RotationManager.INSTANCE.getRenderPitch());

            entity.setYaw(partialYaw);
            entity.setPitch(partialPitch);
            entity.prevYaw = partialYaw;
            entity.prevPitch = partialPitch;

            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) entity;

                living.headYaw = partialYaw;
                living.prevHeadYaw = partialYaw;

                living.bodyYaw = partialYaw;
                living.prevBodyYaw = partialYaw;
            }
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private <E extends Entity> void renderPost(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity == MinecraftClient.getInstance().player && RotationManager.INSTANCE.isActive()) {
            ((IEntityRotations) entity).omiga_restoreOriginalRotations();
        }
    }
}