package com.mo.mixin;


import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntryListWidget.class)
public class MixinListWidget {

    @Shadow private boolean renderBackground;

    @Inject(method = "renderWidget",at = @At("HEAD"))
    public void mixinListWidget(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci){
        renderBackground = false;
    }

}
