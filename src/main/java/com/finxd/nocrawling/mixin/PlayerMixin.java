package com.finxd.nocrawling.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static net.minecraft.world.entity.Pose.*;
import static net.minecraft.world.entity.player.Player.STANDING_DIMENSIONS;

@Mixin(Player.class)

public abstract class PlayerMixin {

    //changed crouch height from 1.5 to 1.65 blocks like in older versions
    //also changed swimming pose to the same as standing so the player can't "crawl" in 1 block tall spaces
    @Unique
    private static final Map<Pose, EntityDimensions> POSES = ImmutableMap.<Pose, EntityDimensions>builder()
            .put(Pose.STANDING, STANDING_DIMENSIONS)
            .put(Pose.SLEEPING, EntityDimensions.fixed(0.2F, 0.2F))
            .put(FALL_FLYING, EntityDimensions.scalable(0.6F, 0.6F))
            .put(SWIMMING, STANDING_DIMENSIONS)
            .put(SPIN_ATTACK, EntityDimensions.scalable(0.6F, 0.6F))
            .put(Pose.CROUCHING, EntityDimensions.scalable(0.6F, 1.65F))
            .put(Pose.DYING, EntityDimensions.fixed(0.2F, 0.2F)).build();

    @Inject(method = "getDefaultDimensions", at = @At("HEAD"), cancellable = true)
    public void getDefaultDimensions(Pose pPose, CallbackInfoReturnable<EntityDimensions> cir) {
        cir.setReturnValue(POSES.getOrDefault(pPose, STANDING_DIMENSIONS));
    }
}
