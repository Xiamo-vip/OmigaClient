package com.mo.module.render

import com.mo.gui.ClickGui
import com.mo.module.Category
import com.mo.module.Module
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW

object ClickGui : Module("ClickGui","clickgui", GLFW.GLFW_KEY_RIGHT_SHIFT, Category.Render) {


    override fun onEnable() {
        MinecraftClient.getInstance().setScreen(ClickGui())
        super.onEnable()
    }


    override fun onDisable() {
        MinecraftClient.getInstance().setScreen(null)
        super.onDisable()
    }


}