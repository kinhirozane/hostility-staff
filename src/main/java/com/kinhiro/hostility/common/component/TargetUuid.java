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
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.UUID;

public record TargetUuid(Optional<UUID> uuid, Optional<Component> name) {
    public static final TargetUuid EMPTY = new TargetUuid(Optional.empty(), Optional.empty());
    public static final Codec<TargetUuid> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            UUIDUtil.CODEC.optionalFieldOf("uuid").forGetter(TargetUuid::uuid),
            ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(TargetUuid::name)
        ).apply(instance, TargetUuid::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TargetUuid> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NonNull TargetUuid decode(@NonNull RegistryFriendlyByteBuf input) {
            final var uuid = input.readBoolean()
                ? Optional.of(FriendlyByteBuf.readUUID(input))
                : Optional.<UUID>empty();

            final var name = input.readBoolean()
                ? Optional.of(ComponentSerialization.STREAM_CODEC.decode(input))
                : Optional.<Component>empty();

            return new TargetUuid(uuid, name);
        }

        @Override
        public void encode(@NonNull RegistryFriendlyByteBuf output, @NonNull TargetUuid value) {
            output.writeBoolean(value.uuid.isPresent());
            value.uuid.ifPresent(uuid -> FriendlyByteBuf.writeUUID(output, uuid));
            output.writeBoolean(value.name.isPresent());
            value.name.ifPresent(name -> ComponentSerialization.STREAM_CODEC.encode(output, name));
        }
    };
}
