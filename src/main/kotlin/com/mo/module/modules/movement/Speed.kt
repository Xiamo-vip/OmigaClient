package com.mo.module.modules.movement

import com.mo.module.Category
import com.mo.module.Module
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW

class Speed : Module("Speed","Speed", GLFW.GLFW_KEY_V, Category.Movement) {


    override fun onTick() {
        MinecraftClient.getInstance().player?.setJumping(true)
        MinecraftClient.getInstance().player?.forwardSpeed = 100f

    }

    override fun onEnable() {


        super.onEnable()
    }



}