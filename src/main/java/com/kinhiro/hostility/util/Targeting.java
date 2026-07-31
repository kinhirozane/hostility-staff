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
package com.kinhiro.hostility.util;

import com.kinhiro.hostility.HostilityStaff;
import com.kinhiro.hostility.api.mixin.Targetable;
import com.kinhiro.hostility.common.tag.HostilityEntityTags;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Targeting {
    public static final Identifier POWERFUL_FOLLOW_RANGE = HostilityStaff.id("powerful_follow_range");

    public static @Nullable Entity tryGetTarget(@Nullable final Entity target) {
        switch (target) {
            case null -> {
                return null;
            }

            case final EnderDragonPart part -> {
                return part.parentMob;
            }

            case final OwnableEntity ownable when target.is(HostilityEntityTags.MULTIPART_ENTITY) -> {
                return target;
            }

            case final TraceableEntity traceable when target.is(HostilityEntityTags.MULTIPART_ENTITY) -> {
                final var owner = traceable.getOwner();
                if (owner instanceof LivingEntity) return owner;
            }

            default -> {
            }
        }

        return target;
    }

    public static @Nullable LivingEntity tryGetLivingTarget(@Nullable final Entity target) {
        final var living = tryGetTarget(target);
        if (living instanceof final LivingEntity entity) return entity;
        return null;
    }

    public static @Nullable Mob tryFromUuid(final Level level, final UUID uuid) {
        final var target = level.getEntity(uuid);
        if (target instanceof final Mob mob) return mob;
        return null;
    }

    public static List<Mob> findAll(final Level level, final Vec3 pos, final float radius) {
        final var bb = new AABB(pos.x - radius, pos.y, pos.z - radius, pos.x + radius, pos.y + radius, pos.z + radius);
        final var mobs = new ArrayList<Mob>();
        final var entities = level.getEntities(null, bb).stream()
            .filter(entity -> entity instanceof Mob)
            .map(e -> (Mob) e)
            .toList();

        for (final var entity : entities) {
            final var dx = entity.getX() - pos.x;
            final var dz = entity.getZ() - pos.z;
            if (dx * dx + dz * dz > radius * radius) continue;
            final var targeting = (Targetable) entity;
            if (targeting.hostility_staff$targeting()) continue;
            mobs.add(entity);
        }

        return List.copyOf(mobs);
    }

    public static AABB getBoundingBoxSelectedArea(final BlockPos from, @Nullable final BlockPos to) {
        if (to == null) return new AABB(0d, 0d, 0d, 1d, 1d, 1d).move(from);
        final var minX = Math.min(from.getX(), to.getX());
        final var minY = Math.min(from.getY(), to.getY());
        final var minZ = Math.min(from.getZ(), to.getZ());
        final var maxX = Math.max(from.getX(), to.getX()) + 1;
        final var maxY = Math.max(from.getY(), to.getY()) + 1;
        final var maxZ = Math.max(from.getZ(), to.getZ()) + 1;
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static boolean isTargeting(@Nullable final Entity target) {
        if (target == null) return false;
        if (target instanceof final LivingEntity living) {
            final var targeting = (Targetable) living;
            return targeting.hostility_staff$targeting();
        }

        return false;
    }

    public static void setAttackTarget(final Mob attacker, final LivingEntity target, final boolean both) {
        if (attacker == null || target == null) return;
        if (target instanceof Player) return;
        increaceFollowRange(attacker);
        setTargetTo(attacker, target);
        if (target instanceof final Mob mob && both) {
            increaceFollowRange(mob);
            setTargetTo(mob, attacker);
        }
    }

    private static void increaceFollowRange(final Mob mob) {
        final var followRange = mob.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRange != null && !followRange.hasModifier(POWERFUL_FOLLOW_RANGE)) {
            final var modifier = new AttributeModifier(
                POWERFUL_FOLLOW_RANGE,
                1584d,
                AttributeModifier.Operation.ADD_VALUE
            );

            followRange.addTransientModifier(modifier);
        }
    }

    private static void setTargetTo(final Mob attacker, final LivingEntity target) {
        attacker.setTarget(target);
        attacker.getBrain().setMemory(MemoryModuleType.ANGRY_AT, target.getUUID());
        attacker.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
        attacker.getBrain().setActiveActivityIfPossible(Activity.FIGHT);
        if (attacker instanceof final Warden warden) {
            warden.increaseAngerAt(target, AngerLevel.ANGRY.getMinimumAnger() + 20, false);
            warden.setAttackTarget(target);
        }

        if (attacker instanceof final Targetable tageting) {
            tageting.hostility_staff$targeting(true);
            tageting.hostility_staff$forcedTarget(target);
        }
    }
}
