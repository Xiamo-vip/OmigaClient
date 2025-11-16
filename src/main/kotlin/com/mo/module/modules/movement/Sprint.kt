package com.mo.module.modules.movement

import com.mo.module.Category
import com.mo.module.Module
import com.mo.utils.CustomFonts
import com.mo.utils.FontUtils
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW

class Sprint : Module("Sprint","Keep Sprint", GLFW.GLFW_KEY_I, Category.Movement) {
    override fun onEnable() {
        super.onEnable()
    }

    override fun onDisable() {
        FontUtils.loadFont(CustomFonts.Jigsaw)
        super.onDisable()
    }

    override fun onTick() {
        if (MinecraftClient.getInstance().player != null &&
            MinecraftClient.getInstance().options.forwardKey.isPressed &&
            MinecraftClient.getInstance().player?.isInFluid == false
            ){
            MinecraftClient.getInstance().player?.isSprinting = true
        }
    }


}