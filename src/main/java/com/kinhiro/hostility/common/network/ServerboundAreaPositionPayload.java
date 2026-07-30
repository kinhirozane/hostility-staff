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
package com.kinhiro.hostility.common.network;

import com.kinhiro.hostility.HostilityStaff;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record ServerboundAreaPositionPayload(
    int id,
    @Nullable BlockPos from,
    @Nullable BlockPos to
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerboundAreaPositionPayload> TYPE =
        new CustomPacketPayload.Type<>(HostilityStaff.id("area_position_payload"));

    public static final StreamCodec<FriendlyByteBuf, ServerboundAreaPositionPayload> STREAM_CODEC =
        new StreamCodec<>() {
            @Override
            public @NonNull ServerboundAreaPositionPayload decode(@NonNull final FriendlyByteBuf input) {
                final var id = input.readInt();
                final var from = FriendlyByteBuf.readNullable(input, BlockPos.STREAM_CODEC);
                final var to = FriendlyByteBuf.readNullable(input, BlockPos.STREAM_CODEC);
                return new ServerboundAreaPositionPayload(id, from, to);
            }

            @Override
            public void encode(
                @NonNull final FriendlyByteBuf output,
                @NonNull final ServerboundAreaPositionPayload value
            ) {
                output.writeInt(value.id());
                FriendlyByteBuf.writeNullable(output, value.from(), BlockPos.STREAM_CODEC);
                FriendlyByteBuf.writeNullable(output, value.to(), BlockPos.STREAM_CODEC);
            }
        };

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
