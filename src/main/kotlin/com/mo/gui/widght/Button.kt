package com.mo.gui.widght

import com.mo.gui.screen.TitleScreen
import com.mo.module.render.notification.NotificationManager.mc
import com.mo.utils.AnimationUtil
import com.mo.utils.FontUtils
import com.mo.utils.RenderUtil
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.navigation.GuiNavigation
import net.minecraft.client.gui.navigation.GuiNavigationPath
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.world.SelectWorldScreen
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.gui.widget.PressableWidget
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundManager
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Colors
import java.awt.Color
import java.util.function.Consumer
import kotlin.math.abs

open class Button( x : Int,  y : Int, width: Int, height: Int,val text : String,val onClick :()-> Unit){
    var isHover = false

    var x : Int
    var y : Int
    val width: Int
    val height: Int

    init {
        this.x = x
        this.y = y
        this.width = width
        this.height = height

    }

    val transition : Long = 500L

    var hoverTime : Long = 0L

    var background = ColorUtils.color(180,0,0,0)
    val b2a = 30
    var backgroundHover = ColorUtils.color(210 ,0,0,0)
    val fontSize = 10
    val fontColor = Color.WHITE.rgb

    open fun render(drawContext: DrawContext,mouseX : Int,mouseY : Int,delta : Float){
        RenderSystem.enableBlend()

        if (hoverTime != 0L){

            val rawProgress = ((System.currentTimeMillis()-hoverTime).toFloat() / transition.toFloat())
            val clampedProgress = rawProgress.coerceIn(0.0f, 1.0f)
            if (!isHover){
                val alpha = b2a* AnimationUtil.easeOutQuad(clampedProgress)
                background = ColorUtils.color(210 - alpha.toInt(),0,0,0)
                RenderUtil.drawRoundedRect(drawContext ,x,y,width,height,background)

            }else{
                val alpha = b2a* AnimationUtil.easeOutQuad(clampedProgress)
                backgroundHover = ColorUtils.color(180 + alpha.toInt(),0,0,0)
                RenderUtil.drawRoundedRect(drawContext,x,y,width,height,backgroundHover)
            }

        } else {
            if (isHover) {
                RenderUtil.drawRoundedRect(drawContext, x, y, width, height, backgroundHover)
            } else {
                RenderUtil.drawRoundedRect(drawContext, x, y, width, height, background)
            }
        }


        RenderUtil.drawString(drawContext,
            this.text,
            (x+width/2- FontUtils.getStringWidth(text,fontSize)/2),
            (y+height/2- FontUtils.getStringHeight(text,fontSize)/2),
            fontColor,
            fontSize
        )
        RenderSystem.disableBlend()
    }


    open fun onMouse(mouseX: Double,mouseY: Double){
        isHover(mouseX,mouseY)

    }



    private fun isHover(mouseX: Double,mouseY: Double){
        val shouldHover = mouseY < height + y && mouseY > y && mouseX < width + x && mouseX > x
        if (shouldHover != isHover) {
            hoverTime = System.currentTimeMillis()
        }

        isHover = shouldHover


    }




    open fun  mouseReleased(mouseX: Double, mouseY: Double) {

        if (mouseY< this.height+this.y && mouseY>this.y && mouseX<this.width+this.x && mouseX> this.x){
            mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            this.onClick()
        }


    }





}