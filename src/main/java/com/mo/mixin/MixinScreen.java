package com.mo.mixin;


import com.mo.event.OnLoadingFinished;
import com.mo.event.SetScreenEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinScreen {
//    @Inject(method = "setScreen",at = @At("HEAD"), cancellable = true)
//    public void setScreen(Screen screen, CallbackInfo ci){
//        SetScreenEvent setScreenEvent = new SetScreenEvent(screen);
//        if (setScreenEvent.isCancelled()) {
//            ci.cancel();
//        };
//
//        System.out.println("页面跳转："+screen.toString()+"\n拦截结果："+setScreenEvent.isCancelled());
//
//        setScreenEvent.broadcast();
//
//
//
//    }
}
