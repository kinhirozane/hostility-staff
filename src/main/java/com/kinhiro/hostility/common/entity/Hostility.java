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
package com.kinhiro.hostility.common.entity;

import com.kinhiro.hostility.api.mixin.Targetable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class Hostility extends Entity {
    public static final EntityDataAccessor<Float> DATA_SIZE =
        SynchedEntityData.defineId(Hostility.class, EntityDataSerializers.FLOAT);

    public Mob owner;

    public Hostility(final EntityType<? extends Hostility> type, final Level level) {
        super(type, level);
        noPhysics = true;
    }

    public Hostility(final Level level, final Vec3 pos, final Mob owner) {
        this(HostilityEntities.HOSTILITY, level);
        this.owner = owner;
        snapTo(pos.x, pos.y + owner.getBbHeight() * 1.5d, pos.z);
        entityData.set(DATA_SIZE, owner.getBbHeight());
    }

    @Override
    protected final void defineSynchedData(final SynchedEntityData.@NonNull Builder entityData) {
        entityData.define(DATA_SIZE, 0.5f);
    }

    @Override
    public final void tick() {
        super.tick();
        if (owner == null) return;
        if (!level().isClientSide()) {
            if (shouldDiscard()) discard();
            followOwner();
        }
    }

    protected boolean shouldDiscard() {
        return (owner.getTarget() == null && ((Targetable) owner).hostility_staff$forcedTarget() == null)
            || !owner.isAlive() || owner.isRemoved();
    }

    protected final void followOwner() {
        final var pos = new Vec3(owner.getX(), owner.getY() + owner.getBbHeight() * 1.5, owner.getZ());
        setPos(pos.x, pos.y, pos.z);
    }

    @Override
    protected final void readAdditionalSaveData(final @NonNull ValueInput input) {
    }

    @Override
    protected final void addAdditionalSaveData(final @NonNull ValueOutput output) {
    }

    @Override
    public final boolean shouldRender(final double camX, final double camY, final double camZ) {
        return true;
    }

    @Override
    public final boolean shouldRenderAtSqrDistance(final double distance) {
        return true;
    }

    @Override
    public final boolean hurtServer(
        final @NonNull ServerLevel level,
        final @NonNull DamageSource source,
        final float damage
    ) {
        return false;
    }

    @Override
    public final void teleportTo(final double x, final double y, final double z) {
    }

    @Override
    public final void setDeltaMovement(final @NonNull Vec3 movement) {
    }
}
