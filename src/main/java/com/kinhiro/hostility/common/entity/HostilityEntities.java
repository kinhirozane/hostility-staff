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

import com.kinhiro.hostility.HostilityStaff;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

public final class HostilityEntities {
    public static final ResourceKey<EntityType<?>> HOSTILITY_KEY =
        ResourceKey.create(Registries.ENTITY_TYPE, HostilityStaff.id("hostility"));

    public static final ResourceKey<EntityType<?>> MAGIC_CIRCLE_KEY =
        ResourceKey.create(Registries.ENTITY_TYPE, HostilityStaff.id("magic_circle"));

    public static final ResourceKey<EntityType<?>> PREVIEW_HOSTILITY_KEY =
        ResourceKey.create(Registries.ENTITY_TYPE, HostilityStaff.id("preview_hostility"));

    public static EntityType<Hostility> HOSTILITY;
    public static EntityType<MagicCircle> MAGIC_CIRCLE;
    public static EntityType<PreviewHostility> PREVIEW_HOSTILITY;

    public static void initialize() {
    }
}
