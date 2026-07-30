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
import com.kinhiro.hostility.util.Targeting;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class MagicCircle extends Entity {
    public static final EntityDataAccessor<Float> DATA_RADIUS =
        SynchedEntityData.defineId(MagicCircle.class, EntityDataSerializers.FLOAT);

    public UUID owner;
    private final List<Mob> trackedMobs = new ArrayList<>();
    private final Set<UUID> hostedMobs = new HashSet<>();
    private int lifetime = 20;

    public MagicCircle(final EntityType<MagicCircle> type, final Level level) {
        super(type, level);
        noPhysics = true;
    }

    public MagicCircle(final Level level, final Vec3 pos) {
        this(HostilityEntities.MAGIC_CIRCLE, level);
        snapTo(pos);
        if (trackedMobs.isEmpty()) {
            final var radius = (float) entityData.get(DATA_RADIUS);
            final var aabb = new AABB(
                pos.x - radius,
                pos.y - radius,
                pos.z - radius,
                pos.x + radius,
                pos.y + radius,
                pos.z + radius
            );

            final var mobs = level.getEntities(null, aabb).stream()
                .filter(entity -> entity instanceof Mob)
                .map(entity -> (Mob) entity)
                .toList();

            trackedMobs.addAll(mobs);
            randomlyAssignTargets(trackedMobs);
        }
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.@NonNull Builder entityData) {
        entityData.define(DATA_RADIUS, 32f);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) {
            if (getRemovalReason() != RemovalReason.DISCARDED) unsetRemoved();
            if (trackedMobs.size() <= 1) discard();
            if (trackedMobs.size() > 1) lifetime = 20;
            if (lifetime > 0) lifetime--;
            if (lifetime <= 0) discard();
            final var pos = position();
            final var radius = (float) entityData.get(DATA_RADIUS);
            final var aabb = new AABB(
                pos.x - radius, pos.y - radius, pos.z - radius,
                pos.x + radius, pos.y + radius, pos.z + radius
            );

            final var newMobs = level().getEntities(null, aabb).stream()
                .filter(e -> e instanceof Mob)
                .map(e -> (Mob) e)
                .filter(m -> !trackedMobs.contains(m))
                .toList();

            trackedMobs.removeIf(m -> {
                if (!m.isAlive() || m.isRemoved()) {
                    hostedMobs.remove(m.getUUID());
                    return true;
                }

                return false;
            });

            trackedMobs.addAll(newMobs);
            randomlyAssignTargets(trackedMobs);
            trackedMobs.forEach(mob -> {
                final var forcedTarget = ((Targetable) mob).hostility_staff$forcedTarget();
                if (forcedTarget != null || mob.getTarget() != null) {
                    if (hostedMobs.add(mob.getUUID()))
                        level().addFreshEntity(new Hostility(level(), mob.position(), mob));
                } else hostedMobs.remove(mob.getUUID());
            });
        }
    }

    private void randomlyAssignTargets(final List<Mob> mobs) {
        final var unassigned = new ArrayList<>(mobs);
        unassigned.removeIf(m -> m.getTarget() != null || ((Targetable) m).hostility_staff$forcedTarget() != null);
        for (final var mob : unassigned) {
            final var target = mobs.get(random.nextInt(mobs.size()));
            if (target == mob) continue;
            Targeting.setAttackTarget(mob, target, false);
        }
    }

    @Override
    protected void readAdditionalSaveData(final @NonNull ValueInput input) {
    }

    @Override
    protected void addAdditionalSaveData(final @NonNull ValueOutput output) {
    }

    @Override
    public boolean shouldRender(final double camX, final double camY, final double camZ) {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(final double distance) {
        return true;
    }

    @Override
    public boolean hurtServer(
        final @NonNull ServerLevel level,
        final @NonNull DamageSource source,
        final float damage
    ) {
        return false;
    }

    @Override
    public void teleportTo(final double x, final double y, final double z) {
    }

    @Override
    public void setDeltaMovement(final @NonNull Vec3 deltaMovement) {
    }

    @Override
    public void setDeltaMovement(final double xd, final double yd, final double zd) {
    }
}
