package com.finxd.nocrawling.mixin;

import com.finxd.nocrawling.Config;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Map;

import static net.minecraft.world.entity.Pose.*;
import static net.minecraft.world.entity.player.Player.STANDING_DIMENSIONS;

@Mixin(Player.class)

public abstract class PlayerMixin extends LivingEntity {
    private final Abilities abilities = new Abilities();
    @Nullable
    private Pose forcedPose;

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    //crouch height is now the same as standing height instead of 1.65 due to a bug with ladders that allows the player to phase through the block above
    //also changed swimming pose to the same as standing so the player can't "crawl" in 1 block tall spaces

    //also, whenever swimming, the player no longer fits in 1 block tall spaces
    @Unique
    private static final Map<Pose, EntityDimensions> POSES = ImmutableMap.<Pose, EntityDimensions>builder()
            .put(Pose.STANDING, STANDING_DIMENSIONS)
            .put(Pose.SLEEPING, SLEEPING_DIMENSIONS)
            .put(FALL_FLYING, EntityDimensions.scalable(0.6F, 0.6F))
            .put(SWIMMING, STANDING_DIMENSIONS)
            .put(SPIN_ATTACK, EntityDimensions.scalable(0.6F, 0.6F))
//            .put(Pose.CROUCHING, EntityDimensions.scalable(0.6F, 1.65F))
            .put(Pose.CROUCHING, STANDING_DIMENSIONS)
            .put(Pose.DYING, EntityDimensions.fixed(0.2F, 0.2F)).build();

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    public void getDimensions(Pose pPose, CallbackInfoReturnable<EntityDimensions> cir) {
        cir.setReturnValue(POSES.getOrDefault(pPose, STANDING_DIMENSIONS));
    }

    //old crouching eye height
    //clashes with Nostalgic Tweaks' "oldSneaking" tweak
    @Inject(method = "getStandingEyeHeight", at = @At("HEAD"), cancellable = true)
    public void getStandingEyeHeight(Pose pose, EntityDimensions p_36260_, CallbackInfoReturnable<Float> cir) {
        if (pose == CROUCHING && Config.OLD_EYE_HEIGHT.get()) {
            cir.setReturnValue(1.62F - 0.08F);
        }
    }

    //fixed player getting stuck in either standing or crouching position after a trapdoor is shut on the player's head
    //still doesn't fix the issue where the player inside the trapdoor stays standing
    //only the player inside the trapdoor sees this, it looks normal to other players (why does this happen?)
    @Inject(method = "updatePlayerPose", at = @At("HEAD"))
    protected void updatePlayerPose(CallbackInfo ci) {
        if(forcedPose != null) {
            this.setPose(forcedPose);
            return;
        }
        Pose pose;
        if (this.isFallFlying()) {
            pose = Pose.FALL_FLYING;
        } else if (this.isSleeping()) {
            pose = Pose.SLEEPING;
        } else if (this.isSwimming()) {
            pose = Pose.SWIMMING;
        } else if (this.isAutoSpinAttack()) {
            pose = Pose.SPIN_ATTACK;
        } else if (this.isShiftKeyDown() && !this.abilities.flying) {
            pose = Pose.CROUCHING;
        } else {
            pose = Pose.STANDING;
        }

        this.setPose(pose);
    }
}
