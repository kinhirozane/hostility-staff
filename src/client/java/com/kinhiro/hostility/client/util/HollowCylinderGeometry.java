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

public final class HollowCylinderGeometry {
    private static final int SEGMENTS = 48;
    private static final float RADIUS = 1f;
    private static final float HEIGHT = 3f;
    private static final List<CylVertex> VERTICES = build();

    public static void forEachVertex(final CylVertexConsumer consumer) {
        for (final var v : VERTICES) consumer.accept(v.position, v.uv);
    }

    private static List<CylVertex> build() {
        final var out = new ArrayList<CylVertex>();
        final var halfH = HEIGHT * 0.5f;
        for (var i = 0; i < SEGMENTS; i++) {
            final var theta0 = (float) (2 * Math.PI * i / SEGMENTS);
            final var theta1 = (float) (2 * Math.PI * (i + 1) / SEGMENTS);
            final var cos0 = (float) Math.cos(theta0);
            final var sin0 = (float) Math.sin(theta0);
            final var cos1 = (float) Math.cos(theta1);
            final var sin1 = (float) Math.sin(theta1);
            final var u0 = (float) i / SEGMENTS;
            final var u1 = (float) (i + 1) / SEGMENTS;
            final var t0 = new Vector3f(RADIUS * cos0, halfH, RADIUS * sin0);
            final var t1 = new Vector3f(RADIUS * cos1, halfH, RADIUS * sin1);
            final var b0 = new Vector3f(RADIUS * cos0, -halfH, RADIUS * sin0);
            final var b1 = new Vector3f(RADIUS * cos1, -halfH, RADIUS * sin1);
            out.add(new CylVertex(t0, new Vector2f(u0, 1f)));
            out.add(new CylVertex(t1, new Vector2f(u1, 1f)));
            out.add(new CylVertex(b0, new Vector2f(u0, 0f)));
            out.add(new CylVertex(b0, new Vector2f(u0, 0f)));
            out.add(new CylVertex(t1, new Vector2f(u1, 1f)));
            out.add(new CylVertex(b1, new Vector2f(u1, 0f)));
        }

        return out;
    }

    public record CylVertex(Vector3f position, Vector2f uv) {
    }

    @FunctionalInterface
    public interface CylVertexConsumer {
        void accept(final Vector3f position, final Vector2f uv);
    }
}
