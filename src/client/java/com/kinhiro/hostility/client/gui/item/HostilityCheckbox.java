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
package com.kinhiro.hostility.client.gui.item;

import com.kinhiro.hostility.HostilityStaff;
import com.kinhiro.hostility.util.TextEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.NonNull;

public class HostilityCheckbox extends AbstractButton {
    private static final Identifier CHECKBOX_TEXTURE =
        HostilityStaff.id("textures/gui/hostility_staff_checkbox.png");

    public boolean isSelected;
    private Runnable onPressCallback;

    public HostilityCheckbox(int x, int y, int width, int height, Component label, Component tooltip, boolean active) {
        super(x, y, width, height, TextEffect.hostility(label, false, false));
        isSelected = active;
        setTooltip(Tooltip.create(TextEffect.hostility(tooltip, false, false)));
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            CHECKBOX_TEXTURE,
            getX(),
            getY(),
            isHovered ? 20f : 0f,
            isSelected ? 20f : 0f,
            20,
            height,
            64,
            64
        );

        final var minecraft = Minecraft.getInstance();
        final var font = minecraft.font;
        graphics.text(font, message, getX() + 24, getY() + (height - 8) / 2, ARGB.white(0xfF));
    }

    public void setOnPressCallback(Runnable callback) {
        onPressCallback = callback;
    }

    @Override
    public void onPress(@NonNull InputWithModifiers input) {
        isSelected = !isSelected;
        if (onPressCallback != null) onPressCallback.run();
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, createNarrationMessage());
        if (active) {
            if (isFocused()) output.add(
                NarratedElementType.USAGE,
                isSelected ? Component.translatable("narration.checkbox.usage.focused.uncheck")
                    : Component.translatable("narration.checkbox.usage.focused.check")
            );
            else output.add(
                NarratedElementType.USAGE,
                isSelected ? Component.translatable("narration.checkbox.usage.hovered.uncheck")
                    : Component.translatable("narration.checkbox.usage.hovered.check")
            );
        }
    }
}
