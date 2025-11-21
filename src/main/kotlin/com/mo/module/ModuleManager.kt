package com.mo.module

import com.mo.module.modules.combat.KillAura
import com.mo.module.modules.movement.Speed
import com.mo.module.modules.movement.Sprint
import com.mo.module.player.rotaion.Rotations
import com.mo.module.render.Brightness
import com.mo.module.render.ClickGui
import com.mo.module.render.ESP
import com.mo.module.render.Hud
import com.mo.module.render.NameTags
import com.mo.module.render.notification.Notification
import java.util.stream.Collectors

object ModuleManager {

    val modules: ArrayList<Module> = ArrayList<Module>()


    init {

        modules.add(Sprint)
        modules.add(Notification)
        modules.add(Hud)
        modules.add(Brightness)
        modules.add(ClickGui)
        modules.add(Speed)
        modules.add(KillAura)
        modules.add(Rotations)
        modules.add(ESP)
        modules.add(NameTags)

    }


    fun getEnableModules() : List<Module?>? {
        return  modules.stream().filter { module -> module.enabled }.collect(Collectors.toList<Module>())
    }


}