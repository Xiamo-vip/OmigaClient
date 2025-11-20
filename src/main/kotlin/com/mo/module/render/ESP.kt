package com.mo.module.render

import com.mo.module.Category
import com.mo.module.Module
import com.mo.utils.RenderUtil
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper
import java.awt.Color

object ESP : Module("ESP","透视",-1, Category.Render) {



    init {
        this.enabled = true

    }




    override fun onRender(drawContext: DrawContext, tickDelta: Float) {

    }



    override fun onRenderEntity(
        entity: Entity,
        yaw: Float,
        matrixStack: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        tickDelta: Float
    ) {
        val mc = MinecraftClient.getInstance()
        val textRenderer = mc.textRenderer
        if (mc.world != null && entity.distanceTo(mc.player) <= 100 && entity != mc.player) {
            val matrix4f = matrixStack.peek().positionMatrix

            val cameraX = mc.gameRenderer.camera.pos.x
            val cameraY = mc.gameRenderer.camera.pos.y
            val cameraZ = mc.gameRenderer.camera.pos.z

            val entityX = MathHelper.lerp(tickDelta,entity.lastRenderX.toFloat(),entity.x.toFloat())
            val entityY = MathHelper.lerp(tickDelta,entity.lastRenderY.toFloat(),entity.y.toFloat())
            val entityZ = MathHelper.lerp(tickDelta,entity.lastRenderZ.toFloat(),entity.z.toFloat())
            val x = (entityX - cameraX).toFloat()
            val y = (entityY - cameraY).toFloat()
            val z = (entityZ - cameraZ).toFloat()


            val box = entity.boundingBox.offset(-entity.x,-entity.y,-entity.z)
            RenderUtil.drawBox3D(matrixStack,box, 25.toFloat(),35.toFloat(),115.toFloat(),1f)






        }
    }
}