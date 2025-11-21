package com.mo.mixin;

import com.mo.utils.IEntityRotations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public class MixinEntity implements IEntityRotations {
    @Unique private float cachedYaw;
    @Unique private float cachedPitch;
    @Unique private float cachedHeadYaw;
    @Unique private float cachedBodyYaw;


    @Unique private float cachedPrevYaw;
    @Unique private float cachedPrevPitch;
    @Unique private float cachedPrevHeadYaw;
    @Unique private float cachedPrevBodyYaw;

    @Override
    public void omiga_saveOriginalRotations() {
        Entity self = (Entity) (Object) this;

        // 保存当前
        this.cachedYaw = self.getYaw();
        this.cachedPitch = self.getPitch();
        this.cachedPrevYaw = self.prevYaw;
        this.cachedPrevPitch = self.prevPitch;

        if (self instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) self;
            this.cachedHeadYaw = living.headYaw;
            this.cachedBodyYaw = living.bodyYaw;
            this.cachedPrevHeadYaw = living.prevHeadYaw;
            this.cachedPrevBodyYaw = living.prevBodyYaw;
        }
    }

    @Override
    public void omiga_restoreOriginalRotations() {
        Entity self = (Entity) (Object) this;

        self.setYaw(this.cachedYaw);
        self.setPitch(this.cachedPitch);
        self.prevYaw = this.cachedPrevYaw;
        self.prevPitch = this.cachedPrevPitch;

        if (self instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) self;
            living.headYaw = this.cachedHeadYaw;
            living.bodyYaw = this.cachedBodyYaw;
            living.prevHeadYaw = this.cachedPrevHeadYaw;
            living.prevBodyYaw = this.cachedPrevBodyYaw;
        }
    }
}