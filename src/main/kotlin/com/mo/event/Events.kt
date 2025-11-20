package com.mo.event

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Overlay
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.SplashOverlay
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.floatprovider.FloatProvider
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo


val overLayScreen : ArrayList<Class<Screen>> = arrayListOf(

)

val overlayLayer : ArrayList<Class<Overlay>> = arrayListOf(


)



class RenderHudEvent(val drawContext: DrawContext,val tickDelta: Float) : Event()
class MouseClickEvent(val button: Int, val pressed: Boolean) : Event()
//im sorry for the names of these 5
class RenderScreenEvent() : Event(){
}
class MouseScreenEvent(val pressed: Boolean, val button: Int) : Event()
class MouseScrollScreenEvent(val scrollX: Double, val scrollY: Double) : Event()
class KeyboardScreenEvent(val key: Int, val pressed: Boolean) : Event()
class KeyboardCharScreenEvent(val char: Char): Event()
class SetScreenEvent(val screen : Screen): Event(){

    fun isCancelled() : Boolean {
        if (overLayScreen.indexOf(screen.javaClass)!=-1){
            return true
        }
        return false
    }
}

class OnLoadingFinished(): Event(){
}
class Tick() : Event()

class TravelEvent(val movementInput : Vec3d,val ci : CallbackInfo) : Event()


class RenderEntityEvent(val entity: Entity,val yaw : Float,val tickDelta: Float,val matrixStack: MatrixStack,val vertexConsumers: VertexConsumerProvider) : Event()
