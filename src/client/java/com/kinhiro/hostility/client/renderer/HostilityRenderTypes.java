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

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public final class HostilityRenderTypes {
    private static final Function<Identifier, RenderType> HOSTILITY = Util.memoize(texture -> {
        final var state = RenderSetup.builder(HostilityRenderPipelines.HOSTILITY)
            .withTexture("Sampler0", texture)
            .createRenderSetup();

        return RenderType.create("hostility", state);
    });

    public static RenderType hostility(final Identifier texture) {
        return HOSTILITY.apply(texture);
    }

    public static final RenderType MAGIC_CIRCLE = RenderType.create(
        "magic_circle",
        RenderSetup.builder(HostilityRenderPipelines.MAGIC_CIRCLE).createRenderSetup()
    );

    public static final RenderType MAGIC_CIRCLE_CYLINDER = RenderType.create(
        "magic_circle_cylinder",
        RenderSetup.builder(HostilityRenderPipelines.MAGIC_CIRCLE_CYLINDER).createRenderSetup()
    );
}
