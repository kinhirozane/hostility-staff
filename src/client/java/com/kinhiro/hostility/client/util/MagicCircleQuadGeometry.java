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

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public final class MagicCircleQuadGeometry {
    private static final List<QuadVertex> VERTICES = build();

    public static void forEachVertex(final QuadVertexConsumer consumer) {
        for (final var v : VERTICES) consumer.accept(v.position, v.uv);
    }

    private static List<QuadVertex> build() {
        final var out = new ArrayList<QuadVertex>();
        out.add(new QuadVertex(new Vector3f(-1f, 0f, -1f), new Vector2f(-1f, -1f)));
        out.add(new QuadVertex(new Vector3f(1f, 0f, -1f), new Vector2f(1f, -1f)));
        out.add(new QuadVertex(new Vector3f(1f, 0f, 1f), new Vector2f(1f, 1f)));
        out.add(new QuadVertex(new Vector3f(-1f, 0f, -1f), new Vector2f(-1f, -1f)));
        out.add(new QuadVertex(new Vector3f(1f, 0f, 1f), new Vector2f(1f, 1f)));
        out.add(new QuadVertex(new Vector3f(-1f, 0f, 1f), new Vector2f(-1f, 1f)));
        return out;
    }

    public record QuadVertex(Vector3f position, Vector2f uv) {
    }

    @FunctionalInterface
    public interface QuadVertexConsumer {
        void accept(final Vector3f position, final Vector2f uv);
    }
}
