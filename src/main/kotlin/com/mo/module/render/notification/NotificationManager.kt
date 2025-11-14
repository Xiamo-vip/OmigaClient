package com.mo.module.render.notification


import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvents
import kotlin.math.min

object NotificationManager {
    public val notifies: MutableList<Notify> = ArrayList<Notify>()
    val mc: MinecraftClient = MinecraftClient.getInstance()

    class Notify(private val time: Long, val msg: String) {
        private val startTime: Long = System.currentTimeMillis()
        private val y: Long = 0



        val progress: Float
            get() = min(1f, (System.currentTimeMillis() - startTime) / time.toFloat())

        val isExpired: Boolean
            get() = System.currentTimeMillis() - startTime >= time

        val expiredTime : Long
            get() = System.currentTimeMillis() - startTime - time

    }

    fun addNotificaiton(time: Long, msg: String) {
        notifies.add(Notify(time, msg))
        mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
    }

}