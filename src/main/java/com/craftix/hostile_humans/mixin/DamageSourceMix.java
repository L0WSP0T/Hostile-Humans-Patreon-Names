package com.craftix.hostile_humans.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.craftix.hostile_humans.entity.entities.Human;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;

@Mixin(value = DamageSource.class, priority = 999)
public class DamageSourceMix {

    @Inject(method = "mobAttack", at = @At("HEAD"), cancellable = true)
    private static void getRenderDistance(LivingEntity entity, CallbackInfoReturnable<DamageSource> cir) {
        if (entity instanceof Human) {
        	if (entity.hasCustomName()) {
                cir.setReturnValue(new EntityDamageSource("human_with_name", entity));
        	}
        	else {
                cir.setReturnValue(new EntityDamageSource("human", entity));
        	}
        }
    }
}