package com.mo.module.modules.movement

import com.mo.module.Category
import com.mo.module.Module
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.MovementType
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object Speed : Module("Speed","Speed", GLFW.GLFW_KEY_V, Category.Movement) {




    override fun onTick() {
        val mc = MinecraftClient.getInstance()
        val player = mc.player
        if (player != null && !player.isInFluid){
            if (mc.options.forwardKey.isPressed || mc.options.leftKey.isPressed || mc.options.backKey.isPressed || mc.options.rightKey.isPressed){
                if (player.isOnGround){
                    player.jump()
                }

                val baseSpeed = 0.38
                val yaw = player.yaw
                var motionX = player.velocity.x
                var motionZ = player.velocity.z
                val currentSpeed = sqrt(motionX * motionX + motionZ * motionZ)
                val ratio = baseSpeed / currentSpeed
                motionX *= ratio
                motionZ *= ratio
                player.setVelocity(motionX, player.velocity.y, motionZ)




            }
        }






    }

    override fun onEnable() {


        super.onEnable()
    }

    override fun travel(movementInput: Vec3d, ci: CallbackInfo) {
        val mc = MinecraftClient.getInstance()
        val player = mc.player
        if (player != null){
            if (!player.isOnGround && player.input.movementInput.lengthSquared() > 0.0 && !player.isInFluid ) {

                val BASE_HORIZONTAL_MODIFIER = 0.0004

                val HORIZONTAL_SPEED_AMPLIFIER = 0.0007
                val VERTICAL_SPEED_AMPLIFIER = 0.0004

                val speedAmplifier = player.getStatusEffect(StatusEffects.SPEED)?.amplifier ?: 0
                val horizontalMod = BASE_HORIZONTAL_MODIFIER + HORIZONTAL_SPEED_AMPLIFIER * speedAmplifier


                val yBoost = if (player.velocity.y < 0 && player.fallDistance < 1) {
                    0.0004
                } else {
                    0.0
                }

                player.velocity = player.velocity.multiply(1.0 + horizontalMod, 1.0 + yBoost, 1.0 + horizontalMod)
            }
        }
    }



}