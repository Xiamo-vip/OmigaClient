package com.mo.mixin;


import com.mo.event.TravelEvent;
import com.mo.module.ModuleManager;
import com.mo.module.modules.movement.Speed;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
    @Inject(method = "travel",at = @At("HEAD"), cancellable = true)
    public void modiferTravel(Vec3d movementInput, CallbackInfo ci){
        new TravelEvent(movementInput,ci).broadcast();
    }
}
