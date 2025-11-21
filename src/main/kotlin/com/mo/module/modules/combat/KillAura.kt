package com.mo.module.modules.combat

import com.mo.module.Category
import com.mo.module.Module
import com.mo.utils.Rotation
import com.mo.utils.RotationManager
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.atan2
import kotlin.math.sqrt


object KillAura : Module("KillAura","kill", GLFW.GLFW_KEY_C, Category.Combat) {

    override fun onDisable() {
        RotationManager.targetRotation = null
        super.onDisable()
    }

    val range = 3.0

    override fun onTick() {
        val mc = MinecraftClient.getInstance()
        val localPlayer = mc.player
        val target = CopyOnWriteArrayList<LivingEntity>()

        if (localPlayer != null && mc.world != null && mc.networkHandler != null){
            mc.world!!.entities.filter { it.distanceTo(localPlayer) <= range }.forEach {
                if (it != localPlayer && it != null && it.isAttackable && it.isAlive
                    ){
                    target.add(it as LivingEntity)

                }
            }
        }



        if (target.count() >0 && localPlayer != null){
            val attack_target = target.sortedBy { it.distanceTo(localPlayer) }[0]

            rotate(attack_target)
            if (localPlayer.getAttackCooldownProgress(0.5f) ==1.0f){
                localPlayer.attack(attack_target)
                mc.networkHandler?.sendPacket(PlayerInteractEntityC2SPacket.attack(attack_target,localPlayer.isSneaking))

            }

        }else RotationManager.targetRotation = null



        super.onTick()
    }



    fun rotate(entity: Entity){
        val player = MinecraftClient.getInstance().player

        if (player != null){
            val eyesPos= player.eyePos
            val targetPos  = entity.eyePos

            val diffX = targetPos.x - eyesPos.x
            val diffY = targetPos.y - eyesPos.y
            val diffZ = targetPos.z - eyesPos.z

            val dist = sqrt(diffX * diffX + diffZ * diffZ)


            val yaw = (Math.toDegrees(atan2(diffZ, diffX)) - 90.0).toFloat()
            val pitch = (-Math.toDegrees(atan2(diffY, dist))).toFloat()
            RotationManager.targetRotation = Rotation(yaw,pitch)
        }


    }



}