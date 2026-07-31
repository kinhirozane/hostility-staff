/*
 * MIT License
 *
 * Copyright (c) 2025 Kinhiro Zane and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.kinhiro.hostility.mixin.common;

import com.kinhiro.hostility.api.mixin.Targetable;
import com.kinhiro.hostility.common.entity.Hostility;
import com.kinhiro.hostility.util.Targeting;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class TargetingMob extends LivingEntity {
    @Unique
    private @Nullable Hostility hostility_staff$marker;

    public TargetingMob(final EntityType<? extends LivingEntity> type, final Level level) {
        super(type, level);
    }

    @Shadow
    public abstract @Nullable LivingEntity getTarget();

    @Inject(method = "asValidTarget", at = @At("HEAD"), cancellable = true)
    private void hostility_staff$asValidTarget(
        final LivingEntity target,
        final CallbackInfoReturnable<LivingEntity> cir
    ) {
        if (target != null && ((Targetable) this).hostility_staff$targeting()) cir.setReturnValue(target);
        else if (target == null && ((Targetable) this).hostility_staff$targeting()) {
            final var forcedTarget = ((Targetable) this).hostility_staff$forcedTarget();
            if (forcedTarget != null) cir.setReturnValue(forcedTarget);
        }
    }

    @Inject(method = "canAttack", at = @At("HEAD"), cancellable = true)
    private void hostility_staff$canAttack$warden(
        final LivingEntity target,
        final CallbackInfoReturnable<Boolean> cir
    ) {
        if (((Targetable) this).hostility_staff$targeting()) cir.setReturnValue(true);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void hostility_staff$tick(final CallbackInfo ci) {
        if (level().isClientSide()) return;
        if (this instanceof final Targetable targeting && targeting.hostility_staff$targeting()) {
            final var attacker = (Mob) (Object) this;
            final var forcedTarget = targeting.hostility_staff$forcedTarget();
            if (forcedTarget == null || !forcedTarget.isAlive()) {
                targeting.hostility_staff$targeting(false);
                targeting.hostility_staff$forcedTarget(null);
                hostility_staff$removeMarker();
                return;
            }

            Targeting.setAttackTarget(attacker, forcedTarget, true);
            if (attacker.getTarget() == forcedTarget) {
                if (hostility_staff$marker == null || hostility_staff$marker.isRemoved()) {
                    final var marker = new Hostility(attacker.level(), attacker.position(), attacker);
                    attacker.level().addFreshEntity(marker);
                    hostility_staff$marker = marker;
                }
            } else hostility_staff$removeMarker();
        } else hostility_staff$removeMarker();
    }

    @Unique
    private void hostility_staff$removeMarker() {
        if (hostility_staff$marker != null) {
            if (!hostility_staff$marker.isRemoved()) hostility_staff$marker.discard();
            hostility_staff$marker = null;
        }
    }
}
