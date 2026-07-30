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

import com.kinhiro.hostility.common.component.AreaPosition;
import com.kinhiro.hostility.common.component.HostilityDataComponents;
import com.kinhiro.hostility.common.component.TargetUuid;
import com.kinhiro.hostility.common.component.TargetUuidList;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataComponents.class)
public abstract class MixinDataComponents {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void hostility_staff$clinit(CallbackInfo ci) {
        HostilityDataComponents.AREA_POSITION = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            HostilityDataComponents.AREA_POSITION_KEY,
            DataComponentType.<AreaPosition>builder()
                .persistent(AreaPosition.CODEC)
                .networkSynchronized(AreaPosition.STREAM_CODEC)
                .build()
        );

        HostilityDataComponents.TARGET_UUID = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            HostilityDataComponents.TARGET_UUID_KEY,
            DataComponentType.<TargetUuid>builder()
                .persistent(TargetUuid.CODEC)
                .networkSynchronized(TargetUuid.STREAM_CODEC)
                .build()
        );

        HostilityDataComponents.TARGET_UUID_LIST = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            HostilityDataComponents.TARGET_UUID_LIST_KEY,
            DataComponentType.<TargetUuidList>builder()
                .persistent(TargetUuidList.CODEC)
                .networkSynchronized(TargetUuidList.STREAM_CODEC)
                .build()
        );
    }
}
