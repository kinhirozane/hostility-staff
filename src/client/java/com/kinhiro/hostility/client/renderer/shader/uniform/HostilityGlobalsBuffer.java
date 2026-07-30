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
package com.kinhiro.hostility.client.renderer.shader.uniform;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.system.MemoryStack;

public final class HostilityGlobalsBuffer implements AutoCloseable {
    private static final long START_NANOS = System.nanoTime();
    private static final int UBO_SIZE = new Std140SizeCalculator().putFloat().get();
    private static HostilityGlobalsBuffer instance;

    private final GpuBuffer buffer;
    private final GpuBufferSlice bufferSlice;

    public HostilityGlobalsBuffer() {
        final var device = RenderSystem.getDevice();
        buffer = device.createBuffer(
            () -> "Hostility Staff Globals UBO",
            GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST,
            UBO_SIZE
        );

        bufferSlice = buffer.slice(0, UBO_SIZE);
    }

    public GpuBufferSlice getBuffer() {
        return writeBuffer();
    }

    private GpuBufferSlice writeBuffer() {
        try (final var stack = MemoryStack.stackPush()) {
            final var byteBuffer = Std140Builder.onStack(stack, UBO_SIZE)
                .putFloat(time())
                .get();

            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(buffer.slice(), byteBuffer);
        }

        return bufferSlice;
    }

    public static HostilityGlobalsBuffer getInstance() {
        var inst = instance;
        if (inst == null) {
            inst = new HostilityGlobalsBuffer();
            instance = inst;
        }

        return inst;
    }

    @Override
    public void close() {
        buffer.close();
    }

    private static float time() {
        final var time = System.nanoTime() - START_NANOS;
        if (time <= 0L) return 0f;
        return time / 1_000_000_000f;
    }
}
