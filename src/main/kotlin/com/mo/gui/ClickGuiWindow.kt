package com.mo.gui

import com.mo.module.Category
import com.mo.utils.AnimationUtil
import com.mo.utils.GuiGraphicsAdapter
import com.mo.utils.RenderUtil
import net.minecraft.client.gui.DrawContext
import java.awt.Color

class ClickGuiWindow(category: Category,var x : Int,var y : Int) {
    val width = 100
    var height = 20
    var onDrag = false
    var dragX: Int =0
    var dragY: Int = 0

    var onHover = false

    var isShow = false

    var fadeInTime = 800L
    var showTimer = 0L
    var animationProgress = 0F

    fun render(context: DrawContext,mouseX : Int,mouseY : Int, delta: Float){
        val gui = GuiGraphicsAdapter(context)
        gui.pushPose()
        if (showTimer == 0L ) showTimer = System.currentTimeMillis()
        if (animationProgress >=1) isShow = true
        if (isShow == false){
            animationProgress = ((System.currentTimeMillis() - showTimer).toFloat() / fadeInTime)
            println(animationProgress)
            gui.translate(width.toDouble(),height.toDouble(),0.0)
            gui.scale(1* AnimationUtil.easeOutQuad(
                animationProgress
            ))
            gui.translate(-width.toDouble(),-height.toDouble(),0.0)
        }


        if (onDrag){
            x=mouseX - dragX
            y=mouseY - dragY
        }

        if(onHover){
            RenderUtil.drawRoundedRect(context,x.toFloat(),y.toFloat(),x+width.toFloat(),y+height.toFloat(), 20f,Color.BLUE.rgb)
        }else RenderUtil.drawRoundedRect(context,x.toFloat(),y.toFloat(),x+width.toFloat(),y+height.toFloat(), 10f,Color.RED.rgb)

        gui.popPose()
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int){
        isDrag(mouseX,mouseY)
    }

    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int){
        if (onDrag){
            onDrag = false
        }

    }



    fun mouseMoved(mouseX: Double, mouseY: Double){
        isHover(mouseX,mouseY)
    }

    fun isDrag(mouseX: Double,mouseY: Double){
        if (onHover){
            dragX= mouseX.toInt()-x
            dragY=mouseY.toInt() -y
            onDrag =true
        }
    }

    fun isHover(mouseX: Double,mouseY: Double){
        if (mouseY< height+y && mouseY>y && mouseX<width+x && mouseX> x){
            onHover = true
        }else onHover = false
    }

}