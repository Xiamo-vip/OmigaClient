package com.mo.event

import com.mo.gui.ClickGui
import com.mo.gui.screen.OverlayScreen
import com.mo.module.Category
import com.mo.module.ModuleManager
import com.mo.module.ModuleManager.modules
import com.mo.utils.RotationManager
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.SplashOverlay
import net.minecraft.client.gui.screen.TitleScreen


object  EvenManager {


    fun init(){
        eventBus.subscribe()

    }



    @EventTarget
    fun onTick(tick : Tick){
        modules.filter { module -> module.enabled }.forEach {
            it.onTick()
        }


    }



    @EventTarget
    fun onRender(event: RenderHudEvent){
        modules.filter { module -> module.enabled }.forEach {
            it.onRender(drawContext = event.drawContext,event.tickDelta)
        }

    }


    @EventTarget
    fun onKey(event:KeyboardScreenEvent){
       if (MinecraftClient.getInstance().currentScreen == null){
           modules.forEach { module ->
               if (event.pressed && event.key == module.key){
                   module.toggle()
               }
           }
       }

    }

    @EventTarget
    fun onSetScreen(event: SetScreenEvent){

    }

    @EventTarget
    fun onLoadingFinished(event: OnLoadingFinished){
        MinecraftClient.getInstance().setScreen(TitleScreen())
    }

    @EventTarget
    fun onTravel(event: TravelEvent){
        modules.filter { module -> module.enabled }.forEach {
            module -> module.travel(event.movementInput,event.ci)
        }
    }


    @EventTarget
    fun onRenderEntity(event: RenderEntityEvent){
        modules.filter { it.enabled && it.category == Category.Render }.forEach { module ->
            module.onRenderEntity(event.entity,event.yaw,event.matrixStack,event.vertexConsumers,event.tickDelta)
        }
    }


}