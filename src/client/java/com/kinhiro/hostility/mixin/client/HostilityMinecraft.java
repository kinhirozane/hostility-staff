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

import com.kinhiro.hostility.common.item.HostilityStaffItem;
import com.kinhiro.hostility.common.network.ServerboundAreaPositionPayload;
import com.kinhiro.hostility.common.network.ServerboundHostilityStaffSkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class HostilityMinecraft {
    @Shadow
    @Nullable
    public LocalPlayer player;

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void hostility_staff$startAttack(CallbackInfoReturnable<Boolean> cir) {
        if (player == null) return;
        final var stack = player.getMainHandItem();
        if (stack.isEmpty() || !(stack.getItem() instanceof HostilityStaffItem)) return;
        final var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.getBooleanOr("hostility_staff_skill", false)) {
            final var pick = ProjectileUtil.getHitResultOnViewVector(player, EntitySelector.CAN_BE_PICKED, 128d);
            if (pick.getType() == HitResult.Type.BLOCK) {
                final var pos = pick.getLocation();
                ClientPlayNetworking.send(new ServerboundHostilityStaffSkillPayload(pos));
                cir.setReturnValue(true);
            } else if (pick.getType() == HitResult.Type.ENTITY) {
                final var pos = ((EntityHitResult) pick).getEntity().position();
                ClientPlayNetworking.send(new ServerboundHostilityStaffSkillPayload(pos));
                cir.setReturnValue(true);
            }

            return;
        }

        if (!tag.getBooleanOr("hostility_staff_area_selector", false)) return;
        if (!tag.getBooleanOr("hostility_staff_expand_mode", false)) return;
        if (!(hitResult instanceof BlockHitResult || hitResult instanceof EntityHitResult)) return;
        if (hitResult instanceof final BlockHitResult blockHit) {
            final var pos = blockHit.getBlockPos();
            ClientPlayNetworking.send(new ServerboundAreaPositionPayload(player.getId(), pos, pos));
            cir.setReturnValue(true);
        }
    }

    @Shadow
    @Nullable
    public HitResult hitResult;
}
