package com.mo.gui


import com.mo.Omiga
import com.mo.gui.widght.Button
import com.mo.module.Category
import com.mo.module.ModuleManager
import com.mo.module.render.ClickGui
import com.mo.utils.RenderUtil
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.Font
import java.io.BufferedReader
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors

class ClickGui : Screen(Text.of("ClickGui")) {
    val mc = MinecraftClient.getInstance()

    val windows = CopyOnWriteArrayList<ClickGuiWindow>()

    var x : Int = 0;
    var y:Int = 60;

    init {
        Category.entries.forEach {
            windows.add(ClickGuiWindow(it,x,y))
            x+=200
        }

    }


    override fun renderBackground(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {

    }


    override fun shouldCloseOnEsc(): Boolean {

        return true
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (mc.options.forwardKey.matchesKey(keyCode,scanCode)) mc.options.forwardKey.isPressed = true
        if (mc.options.leftKey.matchesKey(keyCode,scanCode)) mc.options.leftKey.isPressed = true
        if (mc.options.rightKey.matchesKey(keyCode,scanCode)) mc.options.rightKey.isPressed = true
        if (mc.options.backKey.matchesKey(keyCode,scanCode)) mc.options.backKey.isPressed = true
        if (mc.options.jumpKey.matchesKey(keyCode,scanCode)) mc.options.jumpKey.isPressed = true
        

        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (mc.options.forwardKey.matchesKey(keyCode,scanCode)) mc.options.forwardKey.isPressed = false
        if (mc.options.leftKey.matchesKey(keyCode,scanCode)) mc.options.leftKey.isPressed = false
        if (mc.options.rightKey.matchesKey(keyCode,scanCode)) mc.options.rightKey.isPressed = false
        if (mc.options.backKey.matchesKey(keyCode,scanCode)) mc.options.backKey.isPressed = false
        if (mc.options.jumpKey.matchesKey(keyCode,scanCode)) mc.options.jumpKey.isPressed = false
        return super.keyReleased(keyCode, scanCode, modifiers)
    }

    override fun render(drawContext : DrawContext, mouseX: Int, mouseY: Int, partialTick: Float) {
        windows.forEach { it->
            it.render(drawContext,mouseX,mouseY, partialTick)
        }
        super.render(drawContext, mouseX, mouseY, partialTick)
    }



    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        windows.forEach { it->
            it.mouseMoved(mouseX,mouseY)
        }
        super.mouseMoved(mouseX, mouseY)
    }


    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        windows.forEach { it->
            it.mouseClicked(mouseX,mouseY, button)
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        windows.forEach { it->
            it.mouseReleased(mouseX,mouseY,button)
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun close() {
        ModuleManager.modules.last { module -> module.name == "ClickGui" }.toggle()
        super.close()
    }

    override fun shouldPause(): Boolean {
        return false
    }
}