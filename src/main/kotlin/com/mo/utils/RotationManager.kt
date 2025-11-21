package com.mo.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.MathHelper
import kotlin.math.abs

object RotationManager {
    var targetRotation: Rotation? = null

    private var serverYaw: Float = 0f
    private var serverPitch: Float = 0f
    var nextRotation: Rotation? = null

    var renderYaw: Float = 0f
    var renderPitch: Float = 0f
    var prevRenderYaw: Float = 0f
    var prevRenderPitch: Float = 0f

    var isActive: Boolean = false

    fun setTarget(rot: Rotation?) {
        targetRotation = rot
    }

    fun onTick(realYaw: Float, realPitch: Float) {
        prevRenderYaw = renderYaw
        prevRenderPitch = renderPitch

        val targetYaw: Float
        val targetPitch: Float

        if (targetRotation != null) {
            isActive = true
            targetYaw = targetRotation!!.yaw
            targetPitch = targetRotation!!.pitch
        } else {
            targetYaw = realYaw
            targetPitch = realPitch

            val yawDiff = abs(MathHelper.wrapDegrees(targetYaw - renderYaw))
            val pitchDiff = abs(MathHelper.wrapDegrees(targetPitch - renderPitch))

            if (yawDiff < 1.0f && pitchDiff < 1.0f) {
                isActive = false
                renderYaw = realYaw
                renderPitch = realPitch
                return
            } else {
                isActive = true
            }
        }

        val turnSpeed = 40f

        renderYaw = interpolateAngle(renderYaw, targetYaw, turnSpeed)
        renderPitch = interpolateAngle(renderPitch, targetPitch, turnSpeed)
    }

    private fun interpolateAngle(current: Float, target: Float, speed: Float): Float {
        var diff = MathHelper.wrapDegrees(target - current)
        if (diff > speed) diff = speed
        if (diff < -speed) diff = -speed
        return current + diff
    }

    fun updateServerRotation(yaw: Float, pitch: Float) {
        serverYaw = yaw
        serverPitch = pitch
    }

    fun smoothRotation(target: Rotation, maxSpeed: Float): Rotation {
        val yawDiff = MathHelper.wrapDegrees(target.yaw - serverYaw)
        val pitchDiff = MathHelper.wrapDegrees(target.pitch - serverPitch)

        val clampedYaw = yawDiff.coerceIn(-maxSpeed, maxSpeed)
        val clampedPitch = pitchDiff.coerceIn(-maxSpeed, maxSpeed)

        val newYaw = serverYaw + clampedYaw
        val newPitch = serverPitch + clampedPitch

        val fixedYaw = applyGCDFix(newYaw, serverYaw)
        val fixedPitch = applyGCDFix(newPitch, serverPitch).coerceIn(-90f, 90f)

        return Rotation(fixedYaw, fixedPitch)
    }

    private fun applyGCDFix(current: Float, previous: Float): Float {
        val sensitivity = MinecraftClient.getInstance().options.mouseSensitivity.value.toFloat()
        val f = sensitivity * 0.6f + 0.2f
        val gcd = f * f * f * 1.2f
        var delta = current - previous
        delta -= delta % gcd
        return previous + delta
    }
}