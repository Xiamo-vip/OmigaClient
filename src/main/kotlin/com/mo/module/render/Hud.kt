package com.mo.module.render

import ColorUtils
import com.mo.Omiga
import com.mo.module.Category
import com.mo.module.Module
import com.mo.module.ModuleManager
import com.mo.utils.AnimationUtil
import com.mo.utils.CustomFontRenderer
import com.mo.utils.CustomFonts
import com.mo.utils.FontUtils
import com.mo.utils.GlyphAtlasFontRenderer
import com.mo.utils.GuiGraphicsAdapter
import com.mo.utils.RenderUtil
import com.mo.utils.RenderUtil.drawRainbowString
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.math.MathHelper
import java.awt.Color
import java.awt.Font
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.function.Supplier


object Hud : Module("Hud","HUD",-1, category = Category.Render) {



    init {
        this.enabled  = true
    }


    val hudModuleFontSize = 10



    override fun onEnable() {
        super.onEnable()
    }




    override fun onRender(drawContext: DrawContext,tickDelta : Float) {

        var fadeOutTime = 1000F
        var y = 0
        val time = System.currentTimeMillis()
        val gui = GuiGraphicsAdapter(drawContext)
        val fps = MinecraftClient.getInstance().fpsDebugString

        RenderUtil.drawString(drawContext,"CNM Client",10,10,Color.BLUE.rgb,25)



        RenderUtil.drawString(drawContext, Omiga.clientVerson, 0, RenderUtil.getWindowsHeight()-FontUtils.getStringHeight(Omiga.clientVerson,hudModuleFontSize)*2-3, ColorUtils.color(255,255,255),hudModuleFontSize)
        RenderUtil.drawString(drawContext,fps, 0, RenderUtil.getWindowsHeight()-FontUtils.getStringHeight(fps,hudModuleFontSize), ColorUtils.color(255,255,255),hudModuleFontSize)



        ModuleManager.modules.filter { module -> module.enabled || module.isOnToggle != 0L }.sortedBy { module ->  module.name}.withIndex().forEach {(index, module) ->

            val hudModuleFontWidth = FontUtils.getStringWidth(module.name,hudModuleFontSize)+6
            val hudModuleFontHeidtht = FontUtils.getStringHeight(module.name,hudModuleFontSize)
            val hudMouduleBackgroundWidth = hudModuleFontWidth+4
            val hudMouduleBackgroundHeight = hudModuleFontHeidtht+5
            if (module.isOnToggle == 0L){
                RenderUtil.drawRect(drawContext,
                    RenderUtil.getWindowsWidth()-hudMouduleBackgroundWidth,
                    y,
                    hudMouduleBackgroundWidth,
                    hudMouduleBackgroundHeight, ColorUtils.color(100,0,0,0))
                RenderUtil.drawRect(drawContext,
                    RenderUtil.getWindowsWidth()- hudMouduleBackgroundWidth,
                    y,
                    1,
                    hudMouduleBackgroundHeight,
                    ColorUtils.color(255,255,255))

                drawRainbowString(drawContext,
                    module.name,
                    RenderUtil.getWindowsWidth()- hudModuleFontWidth.toFloat(),
                    y.toFloat() + hudMouduleBackgroundHeight/2 - hudModuleFontHeidtht/2, time, 2000f, 0.04f, 0.9f, 0.8f, 255, 1.0f,hudModuleFontSize);
            }else {
                var moduleAnimationOverTime = (time - module.isOnToggle)
                var rawProgress = (moduleAnimationOverTime / fadeOutTime).toFloat().coerceIn(0f, 1f)
                
                // 计算实际进度：从 animationStartProgress 平滑过渡到目标状态
                val startProgress = module.animationStartProgress
                val targetProgress = if (module.animationTargetEnabled) 1f else 0f
                val moduleAnimationProgress = startProgress + (targetProgress - startProgress) * (AnimationUtil.easeOutQuad(rawProgress))
                module.animationProgress = moduleAnimationProgress
                if (!module.enabled){
                    // 关闭动画：从当前位置向右滑出
                    gui.pushPose()
                    gui.translate(hudMouduleBackgroundWidth * AnimationUtil.easeOutQuad((1-moduleAnimationProgress)), 0f)
                    RenderUtil.drawRect(drawContext,
                        RenderUtil.getWindowsWidth()- hudMouduleBackgroundWidth,
                        y,
                        hudMouduleBackgroundWidth,
                        hudMouduleBackgroundHeight, ColorUtils.color(100,0,0,0))
                    RenderUtil.drawRect(drawContext,
                        RenderUtil.getWindowsWidth()-hudMouduleBackgroundWidth,
                        y,
                        1,
                        hudMouduleBackgroundHeight,
                        ColorUtils.color(255,255,255))

                    drawRainbowString(drawContext,
                        module.name,
                        RenderUtil.getWindowsWidth() - hudModuleFontWidth.toFloat(),
                        y.toFloat() + hudMouduleBackgroundHeight/2 - hudModuleFontHeidtht/2,
                        time, 2000f, 0.04f, 0.9f, 0.8f, 255, 1.0f,hudModuleFontSize);
                    gui.popPose()
                    if (rawProgress >= 1F){
                        module.isOnToggle = 0L
                        module.animationStartProgress = 0f
                    }

                }else{
                    // 开启动画：从右侧滑入到当前位置
                    gui.pushPose()
                    gui.translate(hudMouduleBackgroundWidth * AnimationUtil.easeOutQuadReserve(moduleAnimationProgress), 0f)
                    RenderUtil.drawRect(drawContext, RenderUtil.getWindowsWidth()- hudModuleFontWidth-3,
                        y,
                        hudMouduleBackgroundWidth,
                        hudMouduleBackgroundHeight, ColorUtils.color(100,0,0,0))
                    RenderUtil.drawRect(drawContext,
                        RenderUtil.getWindowsWidth()- hudMouduleBackgroundWidth,
                        y,
                        1,
                        hudMouduleBackgroundHeight,
                        ColorUtils.color(255,255,255))

                    drawRainbowString(drawContext, module.name,
                        RenderUtil.getWindowsWidth()- hudModuleFontWidth.toFloat(),
                        y.toFloat() + hudMouduleBackgroundHeight/2 - hudModuleFontHeidtht/2,
                        time, 2000f, 0.04f, 0.9f, 0.8f, 255, 1.0f,hudModuleFontSize);
                    gui.popPose()
                    if (rawProgress >= 1F){
                        module.isOnToggle = 0L
                        module.animationStartProgress = 1f
                    }
                }


//                if(index >0){
//                    drawContext.drawHorizontalLine(
//                        RenderUtil.getWindowsWidth()- hudMouduleBackgroundWidth,
//                        RenderUtil.getWindowsWidth()- (hudMouduleBackgroundWidth+hudMouduleBackgroundWidth-hudModuleFontWidth)/2 + (hudModuleFontWidth - FontUtils.getStringWidth(ModuleManager.modules[index-1].name,hudModuleFontSize)),
//                        y, Color.WHITE.rgb
//
//                    )
//                }

            }
            y=y+hudMouduleBackgroundHeight



        }


        ModuleManager.modules.filter { module -> module.enabled }.sortedBy { module -> module.name }.forEach {
            module ->
            val time = System.currentTimeMillis()


        }







        RenderUtil.drawRoundedRect(drawContext,100f,100f,120f,120f, 10f,Color.BLUE.rgb)
        RenderUtil.drawRect(drawContext,30,30,10,10, Color.BLUE.rgb)



        MinecraftClient.getInstance().player?.let { RenderUtil.drawEntity(drawContext,100,100,200,200,30,1f, (RenderUtil.getWindowsWidth()/2).toFloat(),(RenderUtil.getWindowsHeight()/2).toFloat(), it) }

    }



}