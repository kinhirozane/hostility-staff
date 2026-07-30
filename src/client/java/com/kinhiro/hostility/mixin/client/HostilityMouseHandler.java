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
package com.kinhiro.hostility.mixin.client;

import com.kinhiro.hostility.client.util.AreaCornerCache;
import com.kinhiro.hostility.common.item.HostilityStaffItem;
import com.kinhiro.hostility.common.network.ServerboundAreaPositionPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class HostilityMouseHandler {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void hostility_staff$onScroll(
        final long handle,
        final double xoffset,
        final double yoffset,
        final CallbackInfo ci
    ) {
        if (yoffset == 0) return;
        final var player = Minecraft.getInstance().player;
        if (player == null) return;
        final var stack = player.getMainHandItem();
        if (stack.isEmpty() || !(stack.getItem() instanceof HostilityStaffItem)) return;
        final var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.getBooleanOr("hostility_staff_skill", false)) return;
        if (!tag.getBooleanOr("hostility_staff_area_selector", false)) return;
        if (!tag.getBooleanOr("hostility_staff_expand_mode", false)) return;
        if (AreaCornerCache.getFromBox() == null || AreaCornerCache.getToBox() == null) return;
        final var fromBox = AreaCornerCache.getFromBox();
        final var toBox = AreaCornerCache.getToBox();
        if (fromBox == null || toBox == null) return;
        var activeBox = AreaCornerCache.getActiveBox();
        if (activeBox == null) return;
        final var lookAngle = player.getLookAngle();
        final var axis = Direction.getNearest((int) (lookAngle.x * 100), (int) (lookAngle.y * 100), (int) (lookAngle.z * 100), Direction.UP);
        final var scrollDelta = yoffset > 0 ? 1 : -1;
        final var dx = axis.getStepX() * scrollDelta;
        final var dy = axis.getStepY() * scrollDelta;
        final var dz = axis.getStepZ() * scrollDelta;
        activeBox = activeBox.move(dx, dy, dz);
        final var newPos = BlockPos.containing(activeBox.minX, activeBox.minY, activeBox.minZ);
        var fromPos = BlockPos.containing(fromBox.minX, fromBox.minY, fromBox.minZ);
        var toPos = BlockPos.containing(toBox.minX, toBox.minY, toBox.minZ);
        if (AreaCornerCache.isActiveFrom()) fromPos = newPos;
        else toPos = newPos;

        ClientPlayNetworking.send(new ServerboundAreaPositionPayload(player.getId(), fromPos, toPos));
        ci.cancel();
    }

    @Inject(method = "onButton", at = @At("HEAD"), cancellable = true)
    private void hostility_staff$onButton(
        final long handle,
        final MouseButtonInfo rawButtonInfo,
        final int action,
        final CallbackInfo ci
    ) {
        if (action != 1) return;
        final var player = Minecraft.getInstance().player;
        if (player == null) return;
        final var stack = player.getMainHandItem();
        if (stack.isEmpty() || !(stack.getItem() instanceof HostilityStaffItem)) return;
        final var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.getBooleanOr("hostility_staff_skill", false)) return;
        if (!tag.getBooleanOr("hostility_staff_area_selector", false)) return;
        if (!tag.getBooleanOr("hostility_staff_expand_mode", false)) return;
        if (rawButtonInfo.button() == 1 && !player.isShiftKeyDown()) {
            if (AreaCornerCache.getFromBox() == null) return;
            ClientPlayNetworking.send(new ServerboundAreaPositionPayload(player.getId(), null, null));
            ci.cancel();
        } else if (rawButtonInfo.button() == 2) {
            if (AreaCornerCache.getFromBox() == null || AreaCornerCache.getToBox() == null) return;
            AreaCornerCache.setActiveByHit(player.getEyePosition(), player.getLookAngle());
            ci.cancel();
        }
    }
}
