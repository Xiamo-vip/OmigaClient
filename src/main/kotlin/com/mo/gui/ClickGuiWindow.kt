package com.mo.gui

import com.mo.module.Category
import com.mo.module.Module
import com.mo.module.ModuleManager
import com.mo.utils.AnimationUtil
import com.mo.utils.FontUtils
import com.mo.utils.GuiGraphicsAdapter
import com.mo.utils.RenderUtil
import net.minecraft.client.gui.DrawContext
import java.awt.Color
import java.awt.Font
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.forEach
import kotlin.random.Random

class ClickGuiWindow(val category: Category,var x : Int,var y : Int) {
    val width = 100
    var height = 20
    var onDrag = false
    var dragX: Int =0
    var dragY: Int = 0

    val fontSize = 10
    val moduleFontSize = 7

    var backgroundColor = ColorUtils.color(230,7,8,3)
    var backgroundHoverColor = ColorUtils.color(220,19,13,38)
    var moduleColor = ColorUtils.color(255,23,8,23)
    var moduleEnabledColor = ColorUtils.color(255,42,162,66)
    var onHover = false

    var isShow = false

    var fadeInTimeR = 300L
    var showTimer = 0L
    var animationProgress = 0F
    var fadeInTime = fadeInTimeR

    val moduleWidth = 70
    val moduleHeight = 15
    init {

        fadeInTime = fadeInTimeR + Random.nextInt(100,500)
    }
    val modules = CopyOnWriteArrayList<Module>()

    fun render(context: DrawContext,mouseX : Int,mouseY : Int, delta: Float){





        var moduleY = 0
        var moduleX=0

        val gui = GuiGraphicsAdapter(context)
        gui.pushPose()
        if (showTimer == 0L ) showTimer = System.currentTimeMillis()
        if (animationProgress >=1) isShow = true
        if (isShow == false){
            animationProgress = ((System.currentTimeMillis() - showTimer).toFloat() / fadeInTime)
            println(animationProgress)
            gui.translate(x+width.toDouble()/2,y+height.toDouble()/2,0.0)
            gui.scale(1* AnimationUtil.easeOutQuad(
                animationProgress
            ))
            gui.translate(-x-width.toDouble()/2,-y-height.toDouble()/2,0.0)
        }


        if (onDrag){
            x=mouseX - dragX
            y=mouseY - dragY
        }

        if(onHover){
            RenderUtil.drawRect(context,x,y,width,height, backgroundHoverColor)
        }else RenderUtil.drawRect(context,x,y,width,height,backgroundColor)



        FontUtils.drawCustomString(context,category.name,x+ width/2 - FontUtils.getStringWidth(category.name,fontSize)/2,y+height/2- FontUtils.getStringHeight(category.name,fontSize)/2,
            Color.WHITE.rgb )

        moduleY = height +y




        ModuleManager.modules.forEach { module ->
            if (module.category == this.category){
                modules.addIfAbsent(module)
                moduleX =  x+width/2-moduleWidth/2

                val moduleStringX= moduleX+moduleWidth/2- FontUtils.getStringWidth(module.name,moduleFontSize)/2
                val moduleStringY = moduleY+moduleHeight/2- FontUtils.getStringHeight(module.name,moduleFontSize)/2

                if (module.enabled){
                    RenderUtil.drawRect(
                        context,
                        moduleX,
                        moduleY,
                        moduleWidth,
                        moduleHeight,
                        moduleEnabledColor)

                    RenderUtil.drawString(
                        context,
                        module.name,
                        moduleStringX,
                        moduleStringY,
                        Color.WHITE.rgb,moduleFontSize)
                }else{
                    RenderUtil.drawRect(
                        context,
                        moduleX,
                        moduleY
                        ,moduleWidth,moduleHeight, moduleColor)

                    RenderUtil.drawString(
                        context,
                        module.name,
                        moduleStringX,
                        moduleStringY,
                        Color.WHITE.rgb,moduleFontSize)
                }

                module.xClickGui  = moduleX
                module.yClickGui = moduleY

                moduleY+=moduleHeight
            }

        }


        gui.popPose()
    }




    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int){
        isDraOnBar(mouseX,mouseY)
        isClickOnModule(mouseX,mouseY)
    }

    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int){
        if (onDrag){
            onDrag = false
        }

    }



    fun mouseMoved(mouseX: Double, mouseY: Double){
        isHover(mouseX,mouseY)
    }

    fun isDraOnBar(mouseX: Double,mouseY: Double){
        if (onHover){
            dragX= mouseX.toInt()-x
            dragY=mouseY.toInt() -y
            onDrag =true
        }
    }


    fun isClickOnModule(mouseX: Double,mouseY: Double){
        modules.forEach { module ->
            if (
                mouseX > module.xClickGui && mouseX < module.xClickGui+moduleWidth
                && mouseY > module.yClickGui && mouseY < module.yClickGui+moduleHeight
            ) module.toggle()


        }


    }

    fun isHover(mouseX: Double,mouseY: Double){
        if (mouseY< height+y && mouseY>y && mouseX<width+x && mouseX> x){
            onHover = true
        }else onHover = false
    }

}