package com.mo.utils


import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Identifier
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import java.awt.Color
import kotlin.math.atan
import kotlin.math.floor
import kotlin.math.max

object RenderUtil {
    val mc = MinecraftClient.getInstance()








    fun drawRect(drawContext: DrawContext ,x:Int,y:Int,width:Int,height:Int,background:Int){
        drawContext.fill(x,y,x+width,y+height,background)

    }


    fun getWindowsWidth(): Int {
        return MinecraftClient.getInstance().getWindow().scaledWidth
    }

    fun getWindowsHeight(): Int {
        return MinecraftClient.getInstance().getWindow().scaledHeight
    }
    fun drawProgress_reverse(
        drawContext: DrawContext,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        progress: Float,
        color: Int
    ) {
        drawContext.fill(x, y, (x + width * (1 - progress)).toInt(), y + height, color)
    }

    fun HSBtoARGB(hue: Float, sat: Float, bri: Float, alpha: Int): Int {
        val rgb = Color.HSBtoRGB(hue, sat, bri)

        val r = (rgb shr 16) and 0xFF
        val g = (rgb shr 8) and 0xFF
        val b = (rgb) and 0xFF
        return (alpha and 0xFF) shl 24 or (r shl 16) or (g shl 8) or b
    }

    fun drawString(
        drawContext: DrawContext,
        text: String,
        x: Float,
        y: Float,
        color: Int,
        size: Int
    ){
        FontUtils.drawCustomString(drawContext,text,x,y,color,size)

    }
    fun drawString(
        drawContext: DrawContext,
        text: String,
        x: Int,
        y: Int,
        color: Int,
        size: Int
    ){
        FontUtils.drawCustomString(drawContext,text,x,y,color,size)

    }

    fun drawStringWithShadow(
        drawContext: DrawContext,
        text: String,
        x: Int,
        y: Int,
        color: Int,
        size: Int
    ){
        FontUtils.drawCustomString(drawContext,text,x+2,y+2, Color.BLACK.rgb,size)
        FontUtils.drawCustomString(drawContext,text,x,y,color,size)


    }





    fun drawString(
        drawContext: DrawContext,
        text: String,
        x: Int,
        y: Int,
        color: Int,
    ){
        FontUtils.drawCustomString(drawContext,text,x,y,color,8)

    }



    fun drawRainbowString(
        drawContext: DrawContext,
        text: String?,
        x: Float,
        y: Float,
        timeMs: Long,
        speed: Float,
        hueStep: Float,
        saturation: Float,
        brightness: Float,
        alpha: Int,
        scale: Float,
        size: Int
    ) {


        val gui = GuiGraphicsAdapter(drawContext)

        if (text == null || text.isEmpty()) return


        var baseHue = 0f
        if (speed > 0.001f) {
            baseHue = ((timeMs % (max(1f, speed)).toLong()) / max(1f, speed)) // 0..1
        }


        gui.pushPose()
        if (scale != 1.0f) {
            gui.scale(scale)

        }

        var xPos = x / scale
        val yPos = y / scale


        for (i in 0..<text.length) {
            val c = text[i]
            val s = c.toString()


            var hue = baseHue + i * hueStep

            hue = hue - floor(hue.toDouble()).toFloat()

            val color = HSBtoARGB(hue, saturation, brightness, alpha)
            drawString(drawContext,s,xPos,yPos,color,size)



            xPos += FontUtils.getStringWidthF(s,size).toFloat()

        }

        gui.popPose()
    }


    fun drawImage(drawContext: DrawContext, img : Identifier, x: Float, y: Float, width: Float, height: Float){
        val tessellator = Tessellator.getInstance()
        val bufferBuilder = tessellator.buffer
        val matrices = drawContext.matrices.peek().positionMatrix
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader(GameRenderer::getPositionTexProgram)
        RenderSystem.setShaderTexture(0,img)
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
        bufferBuilder.vertex(matrices,x,y,5f).texture(0f,0f).next()
        bufferBuilder.vertex(matrices,x,y+height,5f).texture(0f,1f).next()
        bufferBuilder.vertex(matrices,x+width,y+height,5f).texture(1f,1f).next()
        bufferBuilder.vertex(matrices,x+width,y,5f).texture(1f,0f).next()
        tessellator.draw()
        RenderSystem.disableBlend()


        //drawContext.drawTexture(img,x.toInt(),y.toInt(),200,200,200,200)


    }



    fun drawRoundedRect(context: DrawContext,x : Float , y: Float, width: Float,height: Float,radius : Float , color: Int){
        val tessellator = Tessellator.getInstance()
        val bufferBuilder = tessellator.buffer
        var i = 0
        RenderSystem.setShader(GameRenderer::getPositionColorProgram)
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR)

        bufferBuilder.vertex(20.0,20.0,0.0)
            .color(color).next()
        bufferBuilder.vertex(5.0,40.0,0.0)
            .color(color).next()
        bufferBuilder.vertex(35.0,40.0,0.0)
            .color(color).next()

//        while (i <=90){
//            val angle = Math.toRadians(i.toDouble())
//            bufferBuilder.vertex((x+radius+ sin(angle)*radius).toDouble(),(y+radius - cos(angle)*radius).toDouble(),0.0)
//                .color(color)
//                .next()
//            i+=3
//        }

        tessellator.draw()



    }


    fun drawBlur(context: DrawContext,x: Float,y: Float,width: Float,height: Float){
        val tessellator = Tessellator.getInstance()
        val bufferBuilder = tessellator.buffer
        RenderSystem.enableBlend()
        GlStateManager._colorMask(true, true, true, true)
        GlStateManager._disableDepthTest()
        GlStateManager._depthMask(false)
        GlStateManager._blendFunc(770,771)
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram)
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR)
        bufferBuilder.vertex(x.toDouble(),y.toDouble(),0.0).texture(0f,0f).color(255).next()
        bufferBuilder.vertex(x.toDouble(),y.toDouble()+height,0.0).texture(0f,1f).color(255).next()
        bufferBuilder.vertex(x.toDouble()+width,y.toDouble()+height,0.0).texture(1f,1f).color(255).next()
        bufferBuilder.vertex(x.toDouble()+width,y.toDouble(),0.0).texture(1f,0f).color(255).next()
        tessellator.draw()
        GlStateManager._enableDepthTest()
        GlStateManager._depthMask(true)
        GlStateManager._colorMask(true, true, true, true)
        RenderSystem.disableBlend()



    }


    fun drawEntity(
        context: DrawContext,
        x1: Int,
        y1: Int,
        x2: Int,
        y2: Int,
        size: Int,
        f: Float,
        mouseX: Float,
        mouseY: Float,
        entity: LivingEntity
    ) {
        val g = (x1 + x2).toFloat() / 2.0f
        val h = (y1 + y2).toFloat() / 2.0f
        context.enableScissor(x1, y1, x2, y2)
        val i = atan(((g - mouseX) / 40.0f).toDouble()).toFloat()
        val j = atan(((h - mouseY) / 40.0f).toDouble()).toFloat()
        val quaternionf = (Quaternionf()).rotateZ(Math.PI.toFloat())
        val quaternionf2 = (Quaternionf()).rotateX(j * 20.0f * (Math.PI.toFloat() / 180f))
        quaternionf.mul(quaternionf2)
        val k = entity.bodyYaw
        val l = entity.getYaw()
        val m = entity.getPitch()
        val n = entity.prevHeadYaw
        val o = entity.headYaw
        entity.bodyYaw = 180.0f + i * 20.0f
        entity.setYaw(180.0f + i * 40.0f)
        entity.setPitch(-j * 20.0f)
        entity.headYaw = entity.getYaw()
        entity.prevHeadYaw = entity.getYaw()
        val vector3f = Vector3f(0.0f, entity.getHeight() / 2.0f + f, 0.0f)
        drawEntity(context, g, h, size, vector3f, quaternionf, quaternionf2, entity)
        entity.bodyYaw = k
        entity.setYaw(l)
        entity.setPitch(m)
        entity.prevHeadYaw = n
        entity.headYaw = o
        context.disableScissor()
    }

    fun drawEntity(
        context: DrawContext,
        x: Float,
        y: Float,
        size: Int,
        vector3f: Vector3f,
        quaternionf: Quaternionf?,
        quaternionf2: Quaternionf?,
        entity: LivingEntity?
    ) {
        context.getMatrices().push()
        context.getMatrices().translate(x.toDouble(), y.toDouble(), 50.0)
        context.getMatrices()
            .multiplyPositionMatrix((Matrix4f()).scaling(size.toFloat(), size.toFloat(), (-size).toFloat()))
        context.getMatrices().translate(vector3f.x, vector3f.y, vector3f.z)
        context.getMatrices().multiply(quaternionf)
        DiffuseLighting.method_34742()
        val entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher()
        if (quaternionf2 != null) {
            quaternionf2.conjugate()
            entityRenderDispatcher.setRotation(quaternionf2)
        }

        entityRenderDispatcher.setRenderShadows(false)
        RenderSystem.runAsFancy(Runnable {
            entityRenderDispatcher.render<LivingEntity?>(
                entity,
                0.0,
                0.0,
                0.0,
                0.0f,
                1.0f,
                context.getMatrices(),
                context.getVertexConsumers(),
                15728880
            )
        })
        context.draw()
        entityRenderDispatcher.setRenderShadows(true)
        context.getMatrices().pop()
        DiffuseLighting.enableGuiDepthLighting()
    }



    fun drawBox3D(
        matrixStack: MatrixStack,
        x1: Float,
        y1: Float,
        z1 : Float,
        width: Float,
        heightY : Float, // 💡 推荐更名：Y 尺寸命名为 heightY
        depthZ : Float, // 💡 推荐更名：Z 尺寸命名为 depthZ
        color: Int
    ) {
        // 定义边界坐标
        val x2 = x1 + width
        val y2 = y1 + heightY // 修正后的 Y 最大坐标
        val z2 = z1 + depthZ

        // 绘制设置
        matrixStack.push()
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableDepthTest() // 确保透视
        RenderSystem.setShader(GameRenderer::getPositionColorProgram)

        val tessellator = Tessellator.getInstance()
        val bufferBuilder = tessellator.buffer
        val matrix4f = matrixStack.peek().positionMatrix
        RenderSystem.lineWidth(1.5f)
        // ⭐ 采用 LINES 模式：一次性绘制所有 12 条边 (12 * 2 = 24 个顶点)
        bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR)

        // --- 1. 绘制前脸 (Z=z1) ---
        // Bottom: V1(x1, y1, z1) -> V2(x2, y1, z1)
        bufferBuilder.vertex(matrix4f, x1, y1, z1).color(color).next()
        bufferBuilder.vertex(matrix4f, x2, y1, z1).color(color).next()
        // Right: V2(x2, y1, z1) -> V3(x2, y2, z1)
        bufferBuilder.vertex(matrix4f, x2, y1, z1).color(color).next()
        bufferBuilder.vertex(matrix4f, x2, y2, z1).color(color).next()
        // Top: V3(x2, y2, z1) -> V4(x1, y2, z1)
        bufferBuilder.vertex(matrix4f, x2, y2, z1).color(color).next()
        bufferBuilder.vertex(matrix4f, x1, y2, z1).color(color).next()
        // Left: V4(x1, y2, z1) -> V1(x1, y1, z1)
        bufferBuilder.vertex(matrix4f, x1, y2, z1).color(color).next()
        bufferBuilder.vertex(matrix4f, x1, y1, z1).color(color).next()

        // --- 2. 绘制后脸 (Z=z2) ---
        // Bottom: V5(x1, y1, z2) -> V6(x2, y1, z2)
        bufferBuilder.vertex(matrix4f, x1, y1, z2).color(color).next()
        bufferBuilder.vertex(matrix4f, x2, y1, z2).color(color).next()
        // Right: V6(x2, y1, z2) -> V7(x2, y2, z2)
        bufferBuilder.vertex(matrix4f, x2, y1, z2).color(color).next()
        bufferBuilder.vertex(matrix4f, x2, y2, z2).color(color).next()
        // Top: V7(x2, y2, z2) -> V8(x1, y2, z2)
        bufferBuilder.vertex(matrix4f, x2, y2, z2).color(color).next()
        bufferBuilder.vertex(matrix4f, x1, y2, z2).color(color).next()
        // Left: V8(x1, y2, z2) -> V5(x1, y1, z2)
        bufferBuilder.vertex(matrix4f, x1, y2, z2).color(color).next()
        bufferBuilder.vertex(matrix4f, x1, y1, z2).color(color).next()

        // --- 3. 绘制连接边 (4 条垂直边) ---
        // Front-Left V1(x1, y1, z1) -> Back-Left V5(x1, y1, z2)
        bufferBuilder.vertex(matrix4f, x1, y1, z1).color(color).next()
        bufferBuilder.vertex(matrix4f, x1, y1, z2).color(color).next()
        // Front-Right V2(x2, y1, z1) -> Back-Right V6(x2, y1, z2)
        bufferBuilder.vertex(matrix4f, x2, y1, z1).color(color).next()
        bufferBuilder.vertex(matrix4f, x2, y1, z2).color(color).next()
        // Front-Top-Right V3(x2, y2, z1) -> Back-Top-Right V7(x2, y2, z2)
        bufferBuilder.vertex(matrix4f, x2, y2, z1).color(color).next()
        bufferBuilder.vertex(matrix4f, x2, y2, z2).color(color).next()
        // Front-Top-Left V4(x1, y2, z1) -> Back-Top-Left V8(x1, y2, z2)
        bufferBuilder.vertex(matrix4f, x1, y2, z1).color(color).next()
        bufferBuilder.vertex(matrix4f, x1, y2, z2).color(color).next()


        tessellator.draw() // 提交绘制


        RenderSystem.enableDepthTest()
        RenderSystem.disableBlend()
        matrixStack.pop()
    }



}