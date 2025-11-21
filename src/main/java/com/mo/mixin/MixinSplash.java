package com.mo.mixin;


import com.mo.gui.screen.OverlayScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.resource.ResourceReload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(SplashOverlay.class)
public class MixinSplash {
    @Unique
    private static MinecraftClient c;

    @Unique
    private static ResourceReload r;

    @Unique
    private static Consumer consumer;


    @Inject(method = "<init>",at=@At("RETURN"))
    private static void getContext(MinecraftClient client, ResourceReload monitor, Consumer exceptionHandler, boolean reloading, CallbackInfo ci){
        c = client;
        r= monitor;
        consumer = exceptionHandler;
    }

    /**
     * @author XiaMo
     * @reason 替换加载界面
     */
    @Overwrite
    public void render(DrawContext context, int mouseX, int mouseY, float delta){
        Overlay custom = new OverlayScreen(c,r,consumer);
        c.setOverlay(custom);


    }






//    @Inject(method = "render",at = @At("HEAD"), cancellable = true)
//    public void stopRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci){
//        ci.cancel();
//    }
}








//    @Unique
//    public ResourceReload reload ;
//
//    @ModifyVariable(method = "<init>",at = @At("STORE"),ordinal = 0)
//    private ResourceReload reload(ResourceReload resourceReload){
//        this.reload = resourceReload;
//        return resourceReload;
//    }
//
//    @ModifyVariable(method = "<init>",at = @At("STORE"),ordinal = 0)
//    private MinecraftClient.LoadingContext loadingContext(MinecraftClient.LoadingContext loadingContext){
//        return loadingContext;
//    }
//
//
//
//





