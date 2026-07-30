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
package com.kinhiro.hostility.client.renderer;

import com.kinhiro.hostility.client.util.AreaCornerCache;
import com.kinhiro.hostility.common.component.HostilityDataComponents;
import com.kinhiro.hostility.common.item.HostilityStaffItem;
import com.kinhiro.hostility.util.Targeting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.item.component.CustomData;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public final class AreaSelectionRenderer implements DebugRenderer.SimpleDebugRenderer {
    private static final int COLOR_FROM = 0xFF4444FF;
    private static final int COLOR_TO = 0xFFFF4444;
    private static final int COLOR_BOTH = 0xFFFF44FF;

    @Override
    public void emitGizmos(
        final double camX,
        final double camY,
        final double camZ,
        final @NonNull DebugValueAccess debugValues,
        final @NonNull Frustum frustum,
        final float partialTicks
    ) {
        final var mc = Minecraft.getInstance();
        final var player = mc.player;
        if (player == null) return;
        var stack = player.getMainHandItem();
        if (stack.isEmpty() || !(stack.getItem() instanceof HostilityStaffItem)) return;
        final var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.getBooleanOr("hostility_staff_area_selector", false)) {
            final var area = stack.get(HostilityDataComponents.AREA_POSITION);
            if (area != null) {
                AreaCornerCache.update(area.from(), area.to());
                var fromBox = AreaCornerCache.getFromBox();
                if (fromBox == null) return;
                if (Objects.equals(area.from(), area.to())) {
                    Gizmos.cuboid(fromBox, GizmoStyle.stroke(COLOR_BOTH));
                } else {
                    var aabb = Targeting.getBoundingBoxSelectedArea(area.from(), area.to());
                    Gizmos.cuboid(aabb, GizmoStyle.stroke(COLOR_TO));
                    var toBox = AreaCornerCache.getToBox();
                    if (toBox == null) {
                        Gizmos.cuboid(fromBox, GizmoStyle.stroke(COLOR_FROM));
                    } else if (AreaCornerCache.isActiveFrom()) {
                        Gizmos.cuboid(fromBox, GizmoStyle.stroke(COLOR_FROM));
                        Gizmos.cuboid(toBox, GizmoStyle.stroke(COLOR_TO));
                    } else {
                        Gizmos.cuboid(toBox, GizmoStyle.stroke(COLOR_FROM));
                        Gizmos.cuboid(fromBox, GizmoStyle.stroke(COLOR_TO));
                    }
                }
            }
        }
    }
}
