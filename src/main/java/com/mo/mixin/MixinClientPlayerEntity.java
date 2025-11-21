package com.mo.mixin;

import com.mo.utils.Rotation;
import com.mo.utils.RotationManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
    @Shadow @Final public ClientPlayNetworkHandler networkHandler;
    @Shadow @Final protected MinecraftClient client;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickTail(CallbackInfo ci) {
        RotationManager.INSTANCE.onTick(client.player.getYaw(), client.player.getPitch());
    }

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void onSendMovementPacketsHead(CallbackInfo ci) {
        Rotation target = RotationManager.INSTANCE.getTargetRotation();

        if (target != null) {
            Rotation smoothed = RotationManager.INSTANCE.smoothRotation(target, 180f);
            RotationManager.INSTANCE.setNextRotation(smoothed);
        } else {
            RotationManager.INSTANCE.setNextRotation(null);
            RotationManager.INSTANCE.updateServerRotation(client.player.getYaw(), client.player.getPitch());
        }
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private void onSendPacket(ClientPlayNetworkHandler instance, Packet<?> packet) {
        Rotation rotation = RotationManager.INSTANCE.getNextRotation();

        if (rotation == null) {
            instance.sendPacket(packet);
            return;
        }

        if (packet instanceof PlayerMoveC2SPacket) {
            PlayerMoveC2SPacket movePacket = (PlayerMoveC2SPacket) packet;
            double x = movePacket.getX(this.client.player.getX());
            double y = movePacket.getY(this.client.player.getY());
            double z = movePacket.getZ(this.client.player.getZ());
            boolean onGround = movePacket.isOnGround();

            Packet<?> newPacket;

            if (movePacket.changesPosition() && movePacket.changesLook()) {
                newPacket = new PlayerMoveC2SPacket.Full(x, y, z, rotation.getYaw(), rotation.getPitch(), onGround);
            } else if (movePacket.changesLook()) {
                newPacket = new PlayerMoveC2SPacket.LookAndOnGround(rotation.getYaw(), rotation.getPitch(), onGround);
            } else {
                newPacket = new PlayerMoveC2SPacket.Full(x, y, z, rotation.getYaw(), rotation.getPitch(), onGround);
            }

            instance.sendPacket(newPacket);
            RotationManager.INSTANCE.updateServerRotation(rotation.getYaw(), rotation.getPitch());
        } else {
            instance.sendPacket(packet);
        }
    }
}