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
package com.kinhiro.hostility.mixin.client;

import com.kinhiro.hostility.util.TextEffect;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Font.class)
public abstract class HostilityFont {
    @Shadow
    public abstract int width(FormattedCharSequence text);

    @ModifyVariable(method = "split", at = @At("HEAD"), argsOnly = true, name = "input")
    private FormattedText hostility_staff$split(final FormattedText input) {
        return TextEffect.formatted(input);
    }

    @Inject(method = "width(Ljava/lang/String;)I", at = @At("HEAD"), cancellable = true)
    private void hostility_staff$width(final String str, final CallbackInfoReturnable<Integer> cir) {
        final var formatted = TextEffect.visualOrder(str);
        if (formatted != null) cir.setReturnValue(width(formatted));
    }

    @ModifyVariable(
        method = "width(Lnet/minecraft/network/chat/FormattedText;)I",
        at = @At("HEAD"),
        argsOnly = true,
        name = "text"
    )
    private FormattedText hostility_staff$width(final FormattedText text) {
        return TextEffect.formatted(text);
    }

    @Mixin(targets = "net.minecraft.client.gui.Font$PreparedTextBuilder")
    public static abstract class HostilityPreparedTextBuilder {
        @Shadow
        private float x;

        @Shadow
        private float y;

        @Unique
        private boolean hostility_staff$delegating;

        @Unique
        private boolean hostility_staff$active;

        @Unique
        private int hostility_staff$index;

        @Inject(method = "accept(ILnet/minecraft/network/chat/Style;I)Z", at = @At("HEAD"), cancellable = true)
        private void hostility_staff$accept(
            final int position,
            final Style style,
            final int c,
            final CallbackInfoReturnable<Boolean> cir
        ) {
            if (!hostility_staff$delegating) {
                if (TextEffect.isHostilityMarked(style)) {
                    hostility_staff$index = hostility_staff$active ? hostility_staff$index + 1 : 0;
                    hostility_staff$active = true;
                    final var time = TextEffect.time();
                    final var savedX = x;
                    final var savedY = y;
                    var flowOff = 0f;
                    var waveOff = 0f;

                    if (TextEffect.hasEffectFlag(style, 'w'))
                        waveOff = TextEffect.waveOffset(time, hostility_staff$index);

                    if (TextEffect.hasEffectFlag(style, 'f'))
                        flowOff = TextEffect.flowOffset(time, hostility_staff$index);

                    x += flowOff;
                    y += waveOff;

                    final var textColor = TextEffect.hasEffectFlag(style, 'h')
                        ? TextEffect.hostilityAnimatedColorAt(time, hostility_staff$index, System.identityHashCode(style))
                        : style.getColor() != null ? style.getColor().getValue() : 0xFFFFFF;

                    final var renderStyle = TextEffect.dropHostilityStyle(style).withColor(textColor);
                    hostility_staff$delegating = true;
                    try {
                        cir.setReturnValue(((FormattedCharSink) this).accept(position, renderStyle, c));
                    } finally {
                        x -= flowOff;
                        y = savedY;
                        hostility_staff$delegating = false;
                    }
                }
            }
        }
    }
}
