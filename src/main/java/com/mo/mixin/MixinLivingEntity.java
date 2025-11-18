package com.mo.mixin;

import com.mo.module.modules.combat.KillAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Shadow()

    @Inject(method = "turnHead",at = @At("HEAD"),cancellable = true)
    public void turnHead(float bodyRotation, float headRotation, CallbackInfoReturnable<Float> ci){
//        ci.cancel();
    }

    @Shadow protected abstract void initDataTracker();

    @Inject(method = "baseTick",at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;tickStatusEffects()V",ordinal = -3), cancellable = true)
    public void headtick1(CallbackInfo ci){



    }

    @Inject(method = "turnHead",at = @At(value = "HEAD"), cancellable = true)
    public void headtick2(float bodyRotation, float headRotation, CallbackInfoReturnable<Float> cir){
//        if ((LivingEntity)(Object)this == MinecraftClient.getInstance().player){
//            cir.cancel();
//        }


    }


}
