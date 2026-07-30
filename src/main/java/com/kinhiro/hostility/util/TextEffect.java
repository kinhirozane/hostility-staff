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
package com.kinhiro.hostility.util;

import com.kinhiro.hostility.mixin.common.StyleAccessor;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.WeakHashMap;

public final class TextEffect {
    private static final ThreadLocal<Boolean> FORMATTING_GUARD = ThreadLocal.withInitial(() -> false);
    private static final Map<Component, Component> CACHE = new WeakHashMap<>();
    private static final long START_NANOS = System.nanoTime();
    private static final Random RANDOM = new Random();
    private static final int[] COLORS = new int[]{
        0x230106, 0x4B0012, 0x6A1308, 0x7B2A12, 0x8F1A1A, 0xC2181A, 0xE4362A, 0xFF5C3A
    };

    private static final float SPEED_SCALE = 30f;
    private static final float DEFAULT_SPEED = 0.01f;
    private static final float CHAR_STEP = 0.08f;
    private static final String MARKER = "\uE007hostility_staff\uE009";
    private static final float WAVE_AMPLITUDE = 0.6f;
    private static final float WAVE_SPEED = 0.3f;
    private static final float FLOW_AMPLITUDE = 0.25f;
    private static final float FLOW_SPEED = 0.2f;

    public static Component hostility(final Component message, final boolean wave, final boolean flow) {
        final var builder = new StringBuilder();
        builder.append("\uE007\uE009h");
        if (wave) builder.append("\uE007\uE009w");
        if (flow) builder.append("\uE007\uE009f");
        builder.append(message.getString());
        return component(Component.literal(builder.toString()));
    }

    public static Component hostility(final String text) {
        return component(Component.literal("\uE007\uE009h" + text));
    }

    public static float time() {
        final var time = System.nanoTime() - START_NANOS;
        if (time < 0L) return 0f;
        return (time / 1000000000f) * SPEED_SCALE;
    }

    public static int hostilityAnimatedColorAt(final float time, final int charIndex, final int styleCode) {
        final var base = time * DEFAULT_SPEED;
        final var i = (float) charIndex;
        final var s = CHAR_STEP;
        RANDOM.setSeed(styleCode * 104729L + charIndex * 7919L + (long) (time * 0.4f));
        var r0 = RANDOM.nextFloat() * s * 4f;
        var r1 = (RANDOM.nextFloat() - 0.5f) * s * 3f;
        var waveSin = (float) Math.sin(time * 0.02f + i * 0.15f) * s * 2.5f;
        var waveCos = (float) Math.cos(time * 0.03f + i * 0.12f) * s * 1.5f;
        var phase = base + r0 + r1 + waveSin + waveCos;
        return sampleHostilityColor(phase, COLORS);
    }

    public static float waveOffset(final float time, final int charIndex) {
        return (float) Math.sin(time * WAVE_SPEED + charIndex * CHAR_STEP) * WAVE_AMPLITUDE;
    }

    public static float flowOffset(final float time, final int charIndex) {
        return (float) Math.cos(time * FLOW_SPEED + charIndex * CHAR_STEP) * FLOW_AMPLITUDE;
    }

    public static Style markEffectStyle(@Nullable final Style style, final boolean hostility, final boolean wave, final boolean flow) {
        final var safeStyle = style == null ? Style.EMPTY : style;
        final var accessor = (StyleAccessor) (Object) safeStyle;
        final var insertion = accessor.hostility_staff$insertion();
        if (insertion != null && insertion.startsWith(MARKER)) return safeStyle;
        final var builder = new StringBuilder(MARKER);
        if (hostility) builder.append('h');
        if (wave) builder.append('w');
        if (flow) builder.append('f');
        builder.append(insertion == null ? "" : insertion);
        return safeStyle.withInsertion(builder.toString());
    }

    public static Style markHostilityStyle(@Nullable final Style style) {
        return markEffectStyle(style, true, false, false);
    }

    public static boolean isHostilityMarked(@Nullable final Style style) {
        if (style == null) return false;
        final var insertion = ((StyleAccessor) (Object) style).hostility_staff$insertion();
        return insertion != null && insertion.startsWith(MARKER);
    }

    public static boolean hasEffectFlag(@Nullable final Style style, final char flag) {
        if (!isHostilityMarked(style)) return false;
        final var insertion = ((StyleAccessor) (Object) style).hostility_staff$insertion();
        return insertion != null && insertion.indexOf(flag, MARKER.length()) >= 0;
    }

    public static Style dropHostilityStyle(@Nullable final Style style) {
        final var safeStyle = style == null ? Style.EMPTY : style;
        final var accessor = (StyleAccessor) (Object) safeStyle;
        final var insertion = accessor.hostility_staff$insertion();
        if (insertion == null || !insertion.startsWith(MARKER)) return safeStyle;
        final var dropped = insertion.substring(MARKER.length());
        return safeStyle.withInsertion(dropped.isEmpty() ? null : dropped);
    }

    public static FormattedText formatted(final FormattedText input) {
        if (FORMATTING_GUARD.get()) return input;
        if (input instanceof final Component component) return component(component);
        if (!containsFormattingSyntax(input)) return input;
        FORMATTING_GUARD.set(true);
        try {
            final var message = Component.empty();
            input.visit(
                (style, text) -> {
                    if (!text.isEmpty()) message.append(parse(text, style));
                    return Optional.empty();
                },
                Style.EMPTY
            );

            return message;
        } finally {
            FORMATTING_GUARD.set(false);
        }
    }

    public static Component component(final Component input) {
        if (FORMATTING_GUARD.get()) return input;
        if (!containsFormattingSyntax(input)) return input;
        synchronized (CACHE) {
            final var formatted = CACHE.get(input);
            if (formatted != null) return formatted;
        }

        FORMATTING_GUARD.set(true);
        try {
            final var message = Component.empty();
            input.visit(
                (style, text) -> {
                    if (!text.isEmpty()) message.append(parse(text, style));
                    return Optional.empty();
                },
                Style.EMPTY
            );

            synchronized (CACHE) {
                CACHE.put(input, message);
            }

            return message;
        } finally {
            FORMATTING_GUARD.set(false);
        }
    }

    public static FormattedCharSequence visualOrder(final Component original, final FormattedCharSequence fallback) {
        final var formatted = component(original);
        if (formatted == original) return fallback;
        return Language.getInstance().getVisualOrder(formatted);
    }

    public static @Nullable FormattedCharSequence visualOrder(final String text) {
        if (FORMATTING_GUARD.get()) return null;
        if (!containsFormattingSyntax(text)) return null;
        FORMATTING_GUARD.set(true);
        try {
            final var parsed = parse(text, Style.EMPTY);
            return Language.getInstance().getVisualOrder(parsed);
        } finally {
            FORMATTING_GUARD.set(false);
        }
    }

    private static int sampleHostilityColor(final float phase, final int[] colors) {
        var t = phase % 1f;
        if (t < 0f) t += 1f;
        return samplePaletteWrap(t, colors);
    }

    private static int samplePaletteWrap(final float t, final int[] colors) {
        final var count = colors.length;
        if (count == 1) return colors[0];
        final var scaled = t * count;
        final var index = (int) Math.floor(scaled) % count;
        final var f = scaled - (float) Math.floor(scaled);
        final var c1 = colors[index];
        final var c2 = colors[(index + 1) % count];
        return lerpRgb(c1, c2, f);
    }

    private static int lerpRgb(final int c1, final int c2, final float f) {
        final var r1 = (c1 >> 16) & 0xFF;
        final var g1 = (c1 >> 8) & 0xFF;
        final var b1 = c1 & 0xFF;
        final var r2 = (c2 >> 16) & 0xFF;
        final var g2 = (c2 >> 8) & 0xFF;
        final var b2 = c2 & 0xFF;
        final var r = (int) (r1 + (r2 - r1) * f + 0.5f);
        final var g = (int) (g1 + (g2 - g1) * f + 0.5f);
        final var b = (int) (b1 + (b2 - b1) * f + 0.5f);
        return (r << 16) | (g << 8) | b;
    }

    private static boolean containsFormattingSyntax(final FormattedText text) {
        final var found = new boolean[]{false};
        text.visit(
            (style, string) -> {
                if (!found[0]) {
                    var p = 0;
                    while (true) {
                        p = string.indexOf('\uE007', p);
                        if (p < 0 || p + 1 >= string.length()) break;
                        if (string.charAt(p + 1) == '\uE009') {
                            found[0] = true;
                            break;
                        }

                        p++;
                    }
                }

                return Optional.empty();
            },
            Style.EMPTY
        );

        return found[0];
    }

    private static boolean containsFormattingSyntax(final String text) {
        var p = 0;
        var found = false;
        while (true) {
            p = text.indexOf('\uE007', p);
            if (p < 0 || p + 1 >= text.length()) break;
            final var c = text.charAt(p + 1);
            if (c == '\uE009') {
                found = true;
                break;
            }

            p++;
        }

        return found;
    }

    private static Component parse(final String text, final Style style) {
        final var message = Component.empty();
        if (text.isEmpty()) return message;
        final var builder = new StringBuilder(text.length());
        var currentStyle = style == null ? Style.EMPTY : style;
        final var resetStyle = currentStyle;
        var hostility = false;
        var wave = false;
        var flow = false;
        var p = 0;
        while (p < text.length()) {
            final var c = text.charAt(p);
            if (c == '\uE007' && p + 1 < text.length()) {
                final var n = text.charAt(p + 1);
                if (n == '\uE009' && p + 2 < text.length()) {
                    final var f = text.charAt(p + 2);
                    final var k = Character.toLowerCase(f);
                    if (k == 'h' || k == 'w' || k == 'f' || k == 'r') {
                        flush(message, builder, currentStyle);
                        switch (k) {
                            case 'h' -> hostility = true;
                            case 'w' -> wave = true;
                            case 'f' -> flow = true;
                            case 'r' -> {
                                hostility = false;
                                wave = false;
                                flow = false;
                            }
                        }

                        p += 3;
                        continue;
                    }
                }
            }

            final var cp = text.codePointAt(p);
            final var cl = Character.charCount(cp);
            if (hostility || wave || flow) {
                flush(message, builder, currentStyle);
                final var effectStyle = markEffectStyle(currentStyle, hostility, wave, flow);
                message.append(Component.literal(new String(Character.toChars(cp))).setStyle(effectStyle));
            } else builder.appendCodePoint(cp);

            p += cl;
        }

        flush(message, builder, currentStyle);
        return message;
    }

    private static void flush(final MutableComponent message, final StringBuilder builder, final Style style) {
        if (builder.isEmpty()) return;
        message.append(Component.literal(builder.toString()).setStyle(style));
        builder.setLength(0);
    }
}
