package com.mo.utils

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import java.awt.Font


object FontUtils {
    val point = 128
    var fontRender = GlyphAtlasFontRenderer(
        baseFont = Font.createFont(Font.TRUETYPE_FONT, com.mo.Omiga.javaClass.getResourceAsStream(CustomFonts.JBMono)),
        atlasSize = 2048,
        antialias = true
    )


    fun loadFont(font : String){
        fontRender = GlyphAtlasFontRenderer(
            baseFont = Font.createFont(Font.TRUETYPE_FONT, com.mo.Omiga.javaClass.getResourceAsStream(font)),
            atlasSize = 2048,
            antialias = true
        )
    }
    fun drawCustomString(drawContext: DrawContext,text : String,x: Float,y: Float,color : Int,size : Int = 10){
        drawContext.matrices.push()
        drawContext.matrices.scale((size.toDouble() / point.toDouble()).toFloat(),(size.toDouble() / point.toDouble()).toFloat(),1f)
        drawContext.matrices.translate(x/(size.toDouble() / point.toDouble()).toFloat(),y/(size.toDouble() / point.toDouble()).toFloat(),1f)
        fontRender.drawString(drawContext,text,0,0,color,128)
        drawContext.matrices.pop()

    }

    fun drawCustomString(drawContext: DrawContext,text : String,x: Int,y: Int,color : Int,size : Int = 10){
        drawCustomString(drawContext,text,x.toFloat(),y.toFloat(),color,size)
    }


    fun getStringWidth(text: String,size: Int): Int {
        return fontRender.getStringWidth(text,size)
    }

    fun getStringWidthF(text: String,size: Int): Float {
        return fontRender.getStringWidth(text,size).toFloat()
    }

    fun getStringHeight(text: String,size: Int): Int {
        return fontRender.getStringHeight(size)+2
    }




}