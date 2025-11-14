package com.mo.mixin;


import com.mo.event.RenderHudEvent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinHUD {
    @Inject(method = "render",at=@At("TAIL"))
    private void onHud(DrawContext context, float tickDelta, CallbackInfo ci){
        new RenderHudEvent(context,tickDelta).broadcast();

    }





}
