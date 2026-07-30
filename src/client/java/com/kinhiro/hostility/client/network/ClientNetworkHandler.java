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
package com.kinhiro.hostility.client.network;

import com.kinhiro.hostility.client.gui.item.HostilityStaffScreen;
import com.kinhiro.hostility.client.util.AreaCornerCache;
import com.kinhiro.hostility.common.component.HostilityDataComponents;
import com.kinhiro.hostility.common.network.ClientboundClearSelectionPayload;
import com.kinhiro.hostility.common.network.ClientboundOpenHostilityStaffScreenPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public final class ClientNetworkHandler {
    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(ClientboundClearSelectionPayload.TYPE, (payload, context) -> {
            if (Minecraft.getInstance().level != null) {
                final var entity = Minecraft.getInstance().level.getEntity(payload.id());
                if (entity instanceof final Player player) {
                    final var stack = player.getMainHandItem();
                    if (stack.has(HostilityDataComponents.AREA_POSITION))
                        stack.remove(HostilityDataComponents.AREA_POSITION);

                    AreaCornerCache.update(null, null);
                }
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(
            ClientboundOpenHostilityStaffScreenPayload.TYPE,
            (payload, context) -> {
                if (Minecraft.getInstance().level != null) {
                    final var entity = Minecraft.getInstance().level.getEntity(payload.id());
                    if (entity instanceof final Player player
                        && FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) Minecraft.getInstance()
                        .setScreenAndShow(new HostilityStaffScreen(player, player.getMainHandItem()));
                }
            }
        );
    }
}
