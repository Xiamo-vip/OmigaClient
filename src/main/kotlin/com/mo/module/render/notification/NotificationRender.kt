package com.mo.module.render.notification

import com.mo.utils.AnimationUtil
import com.mo.utils.CustomFontRenderer
import com.mo.utils.CustomFonts
import com.mo.utils.FontUtils
import com.mo.utils.GuiGraphicsAdapter
import com.mo.utils.RenderUtil
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class NotificationRender(private val notify: NotificationManager.Notify, y: Int) {

    private val mc = MinecraftClient.getInstance()
    private val fontSize = 8
    private val fontWidth = FontUtils.getStringWidth(notify.msg,fontSize)
    private val fontHeight = FontUtils.getStringHeight(notify.msg,fontSize)
    private var width = fontWidth+12
    private var height = fontHeight +12

    private var x= RenderUtil.getWindowsWidth() - width
    private var y = 0f



    private var stringX = RenderUtil.getWindowsWidth() - width +( width - fontWidth)/ 2


    init {
        this.y = y.toFloat()

    }


    fun draw(drawContext: DrawContext) {
        val progress: Float = notify.progress
        val ay = (y.toFloat()  + 50* AnimationUtil.easeOutQuadReserve(min((notify.progress*2).toFloat(),1.toFloat())))
        RenderUtil.drawRect(
            drawContext,
            x.toInt(),
            ay.toInt(),
            width,
            height,

            ColorUtils.color( 46, 15, 43)//(255 * (1 - progress)).toInt(),
        )




        RenderUtil.drawProgress_reverse(
            drawContext,
            x.toInt(),
            ay.toInt() + height,
            width,
            (1),
            progress,
            ColorUtils.color(98, 98, 203)
        )

        RenderUtil.drawString(
            drawContext,
            notify.msg,
            stringX,
            ay.toInt() + height/2 - fontHeight /2,
            ColorUtils.color(255, 255, 255), //(max(5f, 255 * (1 - progress))).toInt(),
            fontSize
        )


        RenderUtil.drawRect(
            drawContext,
            x.toInt()-2,
            ay.toInt(),
            2,
            height,
            ColorUtils.color( 255, 255, 255),
        )
    }



    fun exit(drawContext: DrawContext,ex : Float) {

        val gui = GuiGraphicsAdapter(drawContext)

        gui.pushPose()
        gui.translate(width*ex,0f)
        RenderUtil.drawRect(
            drawContext,
            x.toInt(),
            y.toInt(),
            width,
            height,
            ColorUtils.color(255, 46, 15, 43)
        )
        RenderUtil.drawString(
            drawContext,
            notify.msg,
            stringX,
            y.toInt() + height/2 - fontHeight /2,
            ColorUtils.color(255, 255, 255, 255),
            fontSize

        )

        RenderUtil.drawRect(
            drawContext,
            x.toInt()-2,
            y.toInt(),
            2,
            height,
            ColorUtils.color( 255, 255, 255),
        )

        gui.popPose()



    }
}