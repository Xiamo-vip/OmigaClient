package com.mo.mixin;


import com.mo.event.KeyboardScreenEvent;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {
    @Inject(method = "onKey",at = @At("HEAD"))
    public void keyPress(long l, int i, int j, int k, int m, CallbackInfo ci){
        if (k == GLFW.GLFW_PRESS) {
            new KeyboardScreenEvent(i, true).broadcast();
        }






    }
}
