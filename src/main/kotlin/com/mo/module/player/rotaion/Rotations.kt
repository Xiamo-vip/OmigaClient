package com.mo.module.player.rotaion

import com.mo.module.Category
import com.mo.module.Module
import com.mo.utils.Rotation
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.LivingEntity
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import java.lang.Math.toDegrees
import kotlin.math.atan2
import kotlin.math.sqrt

object Rotations : Module("Rotations", "转头", -1, Category.Player) {

    var modelRotation: Rotation? = null
    var modelPrevRotation: Rotation? = null
    var target: LivingEntity? = null
    init {
        this.enabled = true
    }
    var rotationSpeed = 0.5f // 每 tick最大旋转角度

    fun update() {
        val mc = MinecraftClient.getInstance()
        val player = mc.player ?: return
        val targetEntity = target ?: return

        val rot = calculateRotation(player, targetEntity,rotationSpeed)

        if (modelRotation != null && modelPrevRotation != null) {
            modelPrevRotation!!.yaw = modelRotation!!.yaw
            modelPrevRotation!!.pitch = modelRotation!!.pitch
            modelRotation!!.yaw = lerpAngle(modelRotation!!.yaw, rot.yaw, rotationSpeed)
            modelRotation!!.pitch = lerpAngle(modelRotation!!.pitch, rot.pitch, rotationSpeed)
        } else {
            modelRotation = rot.copy()
            modelPrevRotation = rot.copy()
        }

        // 更新本地 player rotation
        player.yaw = modelRotation!!.yaw
        player.pitch = modelRotation!!.pitch
        player.headYaw = modelRotation!!.yaw
        player.prevHeadYaw = modelPrevRotation!!.yaw

        // 发送封包同步服务器
        player.networkHandler?.sendPacket(
            PlayerMoveC2SPacket.LookAndOnGround(
                modelRotation!!.yaw,
                modelRotation!!.pitch,
                player.isOnGround
            )
        )
    }

    private fun calculateRotation(from: LivingEntity, to: LivingEntity,factor : Float): Rotation {
        val diffYaw =to.yaw - from.yaw
        val diffPitch =to.pitch - from.pitch

        val interpolateYaw = from.yaw + diffYaw * factor
        val interpolatePitch = from.pitch + diffPitch* factor


        return Rotation(interpolateYaw, interpolatePitch)
    }

    private fun lerpAngle(current: Float, target: Float, maxChange: Float): Float {
        var delta = wrapDegrees(target - current)

        if (delta > maxChange) delta = maxChange
        if (delta < -maxChange) delta = -maxChange

        return current + delta
    }

    private fun wrapDegrees(degrees: Float): Float {
        var deg = degrees
        while (deg >= 180f) deg -= 360f
        while (deg < -180f) deg += 360f
        return deg
    }

    override fun onTick() {
        update()
        super.onTick()
    }
}
