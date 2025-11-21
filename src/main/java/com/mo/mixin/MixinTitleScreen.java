package com.mo.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(TitleScreen.class)
public class MixinTitleScreen{



    @Inject(method = "init",at=@At("HEAD"), cancellable = true)
    public void mixinInit(CallbackInfo ci){
        ci.cancel();
        MinecraftClient.getInstance().setScreen(new com.mo.gui.screen.TitleScreen());
    }
    
}
