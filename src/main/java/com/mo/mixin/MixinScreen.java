package com.mo.mixin;


import com.mo.event.OnLoadingFinished;
import com.mo.event.SetScreenEvent;
import com.mo.utils.RenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(Screen.class)
public class MixinScreen {
    @Shadow @Final private List<Drawable> drawables;

    @Shadow public int width;

    @Shadow public int height;

    /**
     * @author XiaMo
     * @reason UI
     */
    @Overwrite
    public void renderBackgroundTexture(DrawContext context) {
        RenderSystem.setShaderColor(0.25F,0.25F,0.25F,5.0F);
        RenderUtil.INSTANCE.drawImage(context,
                Objects.requireNonNull(Identifier.of("omiga", "/menubg.png")),
                0f,
                0f,
                this.width,
                this.height);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 5.0F);
    }
}

