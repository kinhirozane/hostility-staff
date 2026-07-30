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
package com.kinhiro.hostility.client.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public final class AreaCornerCache {
    private static @Nullable AABB fromBox;
    private static @Nullable AABB toBox;
    private static boolean activeIsFrom = true;

    public static @Nullable AABB getFromBox() {
        return fromBox;
    }

    public static @Nullable AABB getToBox() {
        return toBox;
    }

    public static boolean isActiveFrom() {
        return activeIsFrom;
    }

    public static void update(final @Nullable BlockPos from, final @Nullable BlockPos to) {
        fromBox = from != null ? new AABB(from) : null;
        toBox = to != null ? new AABB(to) : null;
    }

    public static @Nullable AABB getActiveBox() {
        return activeIsFrom ? fromBox : toBox;
    }

    public static void setActiveByHit(final Vec3 origin, final Vec3 lookDirection) {
        if (fromBox == null || toBox == null) return;
        final var end = origin.add(lookDirection.scale(64d));
        final var fromHit = fromBox.clip(origin, end).orElse(null);
        final var toHit = toBox.clip(origin, end).orElse(null);
        if (fromHit != null && toHit != null)
            activeIsFrom = origin.distanceToSqr(fromHit) <= origin.distanceToSqr(toHit);
        else if (fromHit != null) activeIsFrom = true;
        else if (toHit != null) activeIsFrom = false;
    }
}
