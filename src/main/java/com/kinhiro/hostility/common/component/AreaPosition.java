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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public record AreaPosition(@Nullable BlockPos from, @Nullable BlockPos to) {
    public static final AreaPosition EMPTY = new AreaPosition(null, null);
    public static final Codec<AreaPosition> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            BlockPos.CODEC.optionalFieldOf("from").forGetter(pos -> Optional.ofNullable(pos.from)),
            BlockPos.CODEC.optionalFieldOf("to").forGetter(pos -> Optional.ofNullable(pos.to))
        ).apply(instance, (from, to) -> new AreaPosition(from.orElse(null), to.orElse(null)))
    );

    public static final StreamCodec<FriendlyByteBuf, AreaPosition> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NonNull AreaPosition decode(@NonNull FriendlyByteBuf input) {
            final var from = FriendlyByteBuf.readNullable(input, BlockPos.STREAM_CODEC);
            final var to = FriendlyByteBuf.readNullable(input, BlockPos.STREAM_CODEC);
            return new AreaPosition(from, to);
        }

        @Override
        public void encode(@NonNull FriendlyByteBuf output, @NonNull AreaPosition value) {
            FriendlyByteBuf.writeNullable(output, value.from, BlockPos.STREAM_CODEC);
            FriendlyByteBuf.writeNullable(output, value.to, BlockPos.STREAM_CODEC);
        }
    };

    public AreaPosition withFrom(BlockPos from) {
        return new AreaPosition(from, to);
    }

    public AreaPosition withTo(BlockPos to) {
        return new AreaPosition(from, to);
    }
}
