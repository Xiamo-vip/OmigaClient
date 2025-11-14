package com.mo.module.render

import com.mo.module.Category
import com.mo.module.Module
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import org.lwjgl.glfw.GLFW

class Brightness : Module("Brightness", "brightness", GLFW.GLFW_KEY_G, Category.Render) {


    override fun onEnable() {
        MinecraftClient.getInstance().player?.addStatusEffect(StatusEffectInstance(StatusEffects.NIGHT_VISION,-1))
        super.onEnable()
    }



    override fun onDisable() {
        MinecraftClient.getInstance().player?.removeStatusEffect(StatusEffects.NIGHT_VISION)
        super.onDisable()
    }
}