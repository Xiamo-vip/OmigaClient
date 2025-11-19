package com.mo.mixin;


import com.mo.utils.RenderUtil;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {




    @Inject(method = "renderEntity",at =@At("HEAD"))
    public void hookRenderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci){
        // 插值计算保持不变，但结果 d, e, f 不再用于平移，仅用于可选的过滤/距离检查
        double d = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
        double e = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
        double f = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());

        float halfWidth = entity.getWidth() / 2.0f;

        // 核心修正：
        // 传入 drawBox3D 的 X1, Y1, Z1 必须是相对于实体原点 (0, 0, 0) 的偏移量。
        // 因为实体原点在矩阵栈中是 (d - cameraX, e - cameraY, f - cameraZ)

        // 1. X 轴起点：实体中心 (0) 减去宽度的一半 (-halfWidth)
        float x_start = -halfWidth;
        // 2. Y 轴起点：实体底部 (0)
        float y_start = 0.0f;
        // 3. Z 轴起点：实体中心 (0) 减去深度的一半 (-halfWidth)
        float z_start = -halfWidth;

        RenderUtil.INSTANCE.drawBox3D(matrices,
                x_start,
                y_start,
                z_start,
                entity.getWidth(),   // X 轴尺寸 (宽度)
                entity.getHeight(),  // Y 轴尺寸 (高度)
                entity.getWidth(),   // Z 轴尺寸 (深度)
                Color.RED.getRGB()
        );








    }

}
