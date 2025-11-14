package com.mo.utils

import net.minecraft.client.gui.DrawContext

class GuiGraphicsAdapter(val drawContext: DrawContext) {

    fun popPose(){
        drawContext.matrices.pop()
    }

    fun pushPose(){
        drawContext.matrices.push()
    }

    fun scale(x: Float){
        drawContext.matrices.scale(x,x,1f)
    }
    fun scale(x: Double){
        drawContext.matrices.scale(x.toFloat(),x.toFloat(),1f)

    }


    fun translate(x: Float,y: Float,z: Float){
        drawContext.matrices.translate(x,y,z)
    }

    fun translate(x: Double,y: Double,z: Double){
        drawContext.matrices.translate(x,y,z)
    }

    fun translate(x: Double,y: Double){
        drawContext.matrices.translate(x,y,0.0)

    }
    fun translate(x: Float,y: Float){
        drawContext.matrices.translate(x,y,0f)
    }


}