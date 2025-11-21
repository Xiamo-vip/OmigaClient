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


}
