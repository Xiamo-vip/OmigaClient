package com.mo.module

import com.mo.module.render.notification.Notification
import com.mo.module.render.notification.NotificationManager
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.math.Vec3d
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import kotlin.math.abs

open class Module(val name: String, val description:String, val key: Int, val category: Category) {
    var enabled : Boolean = false

    var isOnToggle : Long = 0

    var animationProgress = 0F
    
    // 动画打断相关：记录动画开始时的进度和目标状态
    var animationStartProgress = 0F  // 动画开始时的进度（用于打断时平滑过渡）
    var animationTargetEnabled = false  // 动画的目标状态（true=开启，false=关闭）




    var xClickGui = 0
    var yClickGui = 0



    init {

    }

    open fun onEnable(){
        enabled = true
    }


    open fun onTick(){}
    open fun onDisable(){
        enabled = false
    }


    open fun travel(movementInput : Vec3d,ci : CallbackInfo){}


    open fun toggle(){
        val wasAnimating = isOnToggle != 0L
        
        // 如果正在动画中，记录当前进度作为新动画的起始点
        if (wasAnimating) {
            animationStartProgress = animationProgress
        } else {
            animationStartProgress = if (enabled) 1F else 0F
        }
        
        isOnToggle = abs(System.currentTimeMillis())

        if (enabled){
            animationTargetEnabled = false  // 目标：关闭
            onDisable()
            NotificationManager.addNotificaiton(2000,name + " is Disable")



        } else {
            animationTargetEnabled = true  // 目标：开启
            onEnable()
            NotificationManager.addNotificaiton(2000,name + " is Enable")

        }




    }

    open fun onRender(drawContext: DrawContext,tickDelta : Float){}

}