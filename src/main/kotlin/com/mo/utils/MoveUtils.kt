package com.mo.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import kotlin.math.cos
import kotlin.math.sin

object MoveUtils {

    val mc = MinecraftClient.getInstance()
    val player = mc.player



    fun setSpeed(speed : Float,yaw : Float){
        if (player != null){
            val yaw = player.yaw * (Math.PI / 180.0)
            val movementForward = player.prevZ.toFloat()
            val movementStrafe = player.prevX.toFloat()
            player.sendMessage(Text.of(yaw.toString()))
            val acceleration = 0.002 // 示例加速系数

            // 绕着 Y 轴旋转的移动向量
            val sinYaw = Math.sin(yaw).toFloat()
            val cosYaw = Math.cos(yaw).toFloat()

            // 计算推力
            val pushX = (movementForward * sinYaw - movementStrafe * cosYaw) * acceleration
            val pushZ = (movementStrafe * sinYaw + movementForward * cosYaw) * acceleration
            if (mc.options.forwardKey.isPressed || mc.options.leftKey.isPressed || mc.options.backKey.isPressed || mc.options.rightKey.isPressed){
//                player.sendMessage(Text.of(
//                    "fun：" + Text.of(cos(yaw.toDouble()).toString()) + "yaw : " + yaw.toString()
//                ) )
                if (player.isOnGround){
                    mc.player?.jump()


                    mc.player?.addVelocity(yaw ,0.0,yaw)
                }




            }





        }





    }
}