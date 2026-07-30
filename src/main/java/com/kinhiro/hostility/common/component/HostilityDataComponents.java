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
package com.kinhiro.hostility.common.component;

import com.kinhiro.hostility.HostilityStaff;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

public final class HostilityDataComponents {
    public static final ResourceKey<DataComponentType<?>> AREA_POSITION_KEY =
        ResourceKey.create(Registries.DATA_COMPONENT_TYPE, HostilityStaff.id("are_position"));

    public static final ResourceKey<DataComponentType<?>> TARGET_UUID_KEY =
        ResourceKey.create(Registries.DATA_COMPONENT_TYPE, HostilityStaff.id("target_uuid"));

    public static final ResourceKey<DataComponentType<?>> TARGET_UUID_LIST_KEY =
        ResourceKey.create(Registries.DATA_COMPONENT_TYPE, HostilityStaff.id("target_uuid_list"));

    public static DataComponentType<AreaPosition> AREA_POSITION;
    public static DataComponentType<TargetUuid> TARGET_UUID;
    public static DataComponentType<TargetUuidList> TARGET_UUID_LIST;

    public static void initialize() {
    }
}
