package com.mo.module.render.notification

import com.mo.module.Category
import com.mo.module.Module
import com.mo.utils.AnimationUtil
import com.mo.utils.RenderUtil
import net.minecraft.client.gui.DrawContext
import kotlin.contracts.Effect

class Notification : Module("Notificaiton","notification",-1, Category.Render) {
    val fadeOutTime = 800F
    init {
        this.enabled = true
    }


    override fun onRender(drawContext: DrawContext, tickDelta : Float) {
        val startX = RenderUtil.getWindowsWidth() - (RenderUtil.getWindowsWidth() * 0.2).toInt() - (RenderUtil.getWindowsWidth() * 0.02).toInt()

        val startY = (RenderUtil.getWindowsHeight() * 0.85).toInt()



        val y = intArrayOf(startY)

        NotificationManager.notifies.removeIf {
            it.expiredTime >fadeOutTime

        }

        NotificationManager.notifies.forEach {

            if (it.isExpired == true && it.expiredTime <= fadeOutTime){
                NotificationRender(it,y[0]).exit(drawContext,it.expiredTime.toFloat()/fadeOutTime.toFloat())

                (30*AnimationUtil.easeOutQuad((it.expiredTime.toFloat()/fadeOutTime.toFloat()))).toInt() + y[0]
                y[0] -=30

            }
        }




        NotificationManager.notifies.forEach {
            if (!it.isExpired){
                it.let { notify -> NotificationRender(notify,y[0]).draw(drawContext) }
                y[0] = y[0] - 30

            }
        }







    }



}