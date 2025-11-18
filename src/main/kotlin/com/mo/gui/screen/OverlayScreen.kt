package com.mo.gui.screen

import com.mo.utils.CustomFonts
import com.mo.utils.FontUtils
import com.mo.utils.RenderUtil
import com.mo.utils.RenderUtil.drawImage
import com.mo.utils.RenderUtil.drawString
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Overlay
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.resource.ResourceReload
import net.minecraft.util.Identifier
import java.awt.Color
import java.util.*
import java.util.Optional
import java.util.function.Consumer

class OverlayScreen(val mc : MinecraftClient,val reload: ResourceReload,val handle : Consumer<Optional<Throwable>>): Overlay() {
    private var startTime = -1L
    private val background = ColorUtils.color(26,11,34)
    private val title = "Loading Client"
    private val titleSize = 25


    override fun pausesGame(): Boolean {
        return true
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return super.toString()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (startTime == -1L) startTime = System.currentTimeMillis()
        println(reload.isComplete)
        if (System.currentTimeMillis() - startTime > 500L && reload.isComplete) {
            FontUtils.loadFont(CustomFonts.Jigsaw)
            handle.accept(Optional.empty())
            mc.setScreen(com.mo.gui.screen.TitleScreen())
            mc.overlay = null

            return
        }


        RenderUtil.drawRect(context,0,0, RenderUtil.getWindowsWidth(), RenderUtil.getWindowsHeight(),background)
        RenderUtil.drawString(context,title,
            RenderUtil.getWindowsWidth()/2- FontUtils.getStringWidth(title,titleSize)/2,
            RenderUtil.getWindowsHeight()/2- FontUtils.getStringHeight(title,titleSize)/2,
            Color.WHITE.rgb,
            titleSize

        )



//        drawImage(
//            context,
//            Identifier("omiga", "background.png"),
//            0f,
//            0f,
//            RenderUtil.getWindowsWidth().toFloat(),
//            RenderUtil.getWindowsHeight().toFloat(),
//        )


    }
}