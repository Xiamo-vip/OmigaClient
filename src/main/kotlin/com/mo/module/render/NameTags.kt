package com.mo.module.render

import com.mo.module.Category
import com.mo.module.Module
import com.mo.utils.AnimationUtil
import com.mo.utils.FontUtils
import com.mo.utils.RenderUtil
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import org.joml.AxisAngle4f
import org.joml.Quaternionf
import org.joml.Quaternionfc
import java.awt.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

object NameTags : Module("NameTags","nameTags",-1, Category.Render) {

    init {
        this.enabled = true
    }

    var tickDelta1 = 0f

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
        if (entity.isAlive && entity !=  mc.player && entity.distanceTo(mc.player) < 50 && mc.player != null && entity.isAttackable
            &&entity.isLiving
            ){

            val matrix4f = matrixStack.peek().positionMatrix
            val fontsize = 10f
            val fontWidth =  FontUtils.getStringWidth(Text.translatable(entity.type.translationKey).string,fontsize.toInt())
            val fontHeight = FontUtils.getStringHeight(Text.translatable(entity.type.translationKey).string,fontsize.toInt())
            val distance = entity.squaredDistanceTo(mc.player).coerceIn(0.0,150.0)
            val width = fontWidth + 20
            val height = fontHeight + 10
            val scale = (entity.squaredDistanceTo(mc.player) / 1000f).coerceIn(0.025,0.055)
            val tagX = (-(fontWidth)/2 ).toDouble() + (fontWidth)/4
            val tagY = (entity.height/scale).toDouble() + height
            val tagZ =1.0
            val livingEntity = entity as LivingEntity
            RenderSystem.disableDepthTest()
            RenderSystem.enableBlend()
            matrixStack.push()
            matrixStack.translate(
                0f,
                entity.nameLabelHeight,
                0f
            )
            matrixStack.multiply(mc.gameRenderer.camera.rotation)
            matrixStack.scale(-scale.toFloat(), -scale.toFloat(), scale.toFloat());
            RenderUtil.drawRoundedRect(matrixStack,
                (-width / 2).toFloat(),
                -height/2f,
                width.toFloat(),
                height.toFloat(),ColorUtils.color(230,0,0,0),3)

            RenderUtil.drawString(matrixStack,Text.translatable(entity.type.translationKey).string,
                -fontWidth / 2,
                -fontHeight/2,
                ColorUtils.color(255,255,255,255),
                fontsize.toInt())

            RenderUtil.drawRoundedRect(matrixStack,
                (-width / 2).toFloat(),
                -height/2f + height - height.toFloat()/8,
                width.toFloat() -width.toFloat()* (1-entity.health / entity.maxHealth),
                height.toFloat()/8, Color.RED.rgb,0)

            matrixStack.pop()
            RenderSystem.enableDepthTest()
            RenderSystem.disableBlend()

        }





    }

}