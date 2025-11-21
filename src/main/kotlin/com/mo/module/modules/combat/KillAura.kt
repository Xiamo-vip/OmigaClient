package com.mo.module.modules.combat

import com.mo.module.Category
import com.mo.module.Module
import com.mo.module.player.rotaion.Rotations
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import org.lwjgl.glfw.GLFW

object KillAura : Module("KillAura","kill", GLFW.GLFW_KEY_R, Category.Combat) {



    val range = 3.0

    override fun onTick() {
        val mc = MinecraftClient.getInstance()
        val localPlayer = mc.player

        if (localPlayer != null && mc.world != null && mc.networkHandler != null){
            mc.world!!.entities.filter { it.distanceTo(localPlayer) <= range }.forEach {

                if (it != localPlayer && it != null && it.isAttackable && it.isAlive
                    ){
                    rotate(it)
                    if (localPlayer.getAttackCooldownProgress(0.5f) ==1.0f){
                        mc.networkHandler?.sendPacket(PlayerInteractEntityC2SPacket.attack(it,localPlayer.isSneaking))
                        localPlayer.attack(it)
                    }

                }
            }



        }



        super.onTick()
    }



    fun rotate(entity: Entity){
        //Rotations.target = entity as LivingEntity?
    }



}