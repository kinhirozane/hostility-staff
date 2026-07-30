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
package com.kinhiro.hostility.common.network;

import com.kinhiro.hostility.common.component.AreaPosition;
import com.kinhiro.hostility.common.component.HostilityDataComponents;
import com.kinhiro.hostility.common.component.TargetUuidList;
import com.kinhiro.hostility.common.entity.MagicCircle;
import com.kinhiro.hostility.common.item.HostilityStaffItem;
import com.kinhiro.hostility.util.Targeting;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.component.CustomData;

public final class NetworkHandler {
    public static void initialize() {
        PayloadTypeRegistry.clientboundPlay().register(
            ClientboundClearSelectionPayload.TYPE,
            ClientboundClearSelectionPayload.STREAM_CODEC
        );

        PayloadTypeRegistry.clientboundPlay().register(
            ClientboundOpenHostilityStaffScreenPayload.TYPE,
            ClientboundOpenHostilityStaffScreenPayload.STREAM_CODEC
        );

        PayloadTypeRegistry.serverboundPlay().register(
            ServerboundAreaPositionPayload.TYPE,
            ServerboundAreaPositionPayload.STREAM_CODEC
        );

        PayloadTypeRegistry.serverboundPlay().register(
            ServerboundHostilityStaffSettingPayload.TYPE,
            ServerboundHostilityStaffSettingPayload.STREAM_CODEC
        );

        PayloadTypeRegistry.serverboundPlay().register(
            ServerboundHostilityStaffSkillPayload.TYPE,
            ServerboundHostilityStaffSkillPayload.STREAM_CODEC
        );

        ServerPlayNetworking.registerGlobalReceiver(ServerboundAreaPositionPayload.TYPE, (payload, context) -> {
            var player = context.player();
            var stack = player.getMainHandItem();
            if (stack.isEmpty() || !(stack.getItem() instanceof HostilityStaffItem)) return;
            var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            if (tag.getBooleanOr("hostility_staff_skill", false)) return;
            var area = new AreaPosition(payload.from(), payload.to());
            stack.set(HostilityDataComponents.AREA_POSITION, area);
            if (area.from() != null && area.to() != null) {
                var aabb = Targeting.getBoundingBoxSelectedArea(area.from(), area.to());
                var uuids = player.level().getEntities(null, aabb).stream()
                    .filter(m -> m instanceof Mob)
                    .map(Entity::getUUID).toList();

                stack.set(HostilityDataComponents.TARGET_UUID_LIST, new TargetUuidList(uuids));
            } else if (area.from() == null && area.to() == null) {
                stack.remove(HostilityDataComponents.TARGET_UUID_LIST);
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(
            ServerboundHostilityStaffSettingPayload.TYPE,
            (payload, context) -> {
                final var player = context.player();
                final var stack = player.getMainHandItem();
                if (!stack.isEmpty() && stack.getItem() instanceof HostilityStaffItem)
                    CustomData.update(
                        DataComponents.CUSTOM_DATA,
                        stack,
                        tag -> tag.putBoolean(payload.tag(), payload.value())
                    );
            }
        );

        ServerPlayNetworking.registerGlobalReceiver(ServerboundHostilityStaffSkillPayload.TYPE, (payload, context) -> {
            final var pos = payload.pos();
            final var player = context.player();
            final var stack = player.getMainHandItem();
            if (stack.isEmpty() || !(stack.getItem() instanceof HostilityStaffItem)) return;
            final var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            if (!tag.getBooleanOr("hostility_staff_skill", false)) return;
            final var magic = new MagicCircle(player.level(), pos);
            magic.owner = player.getUUID();
            player.level().addFreshEntity(magic);
        });
    }
}
