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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.kinhiro.hostility.HostilityStaff;
import com.kinhiro.hostility.common.item.HostilityStaffItem;
import com.kinhiro.hostility.common.item.HostilityStaffSetting;
import com.kinhiro.hostility.common.network.ServerboundHostilityStaffSettingPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class HostilityStaffScreen extends Screen {
    private static final Identifier BACKGROUND_TEXTURE =
        HostilityStaff.id("textures/gui/hostility_staff_background.png");

    private static final Identifier BACKGROUND_EFFECT_TEXTURE =
        HostilityStaff.id("textures/gui/hostility_staff_background_effect.png");

    private final ImmutableList<HostilityStaffSetting> settings = HostilityStaffItem.SETTINGS;
    private final Map<HostilityStaffSetting, HostilityCheckbox> checkboxes = Maps.newHashMap();
    private final int imageWidth = 256;
    private final int imageHeight = 200;
    private int posX;
    private int posY;
    private final Player player;
    private final ItemStack stack;

    public HostilityStaffScreen(Player player, ItemStack stack) {
        super(Component.translatable("item.hostility_staff.hostility_staff"));
        this.player = player;
        this.stack = stack;
    }

    @Override
    protected void init() {
        super.init();
        posX = (width - imageWidth) / 2;
        posY = (height - imageHeight) / 2;
        final var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        for (var i = 0; i < settings.size(); i++) {
            final var setting = settings.get(i);
            if (tag.getBooleanOr("hostility_staff_skill", false)
                && ("hostility_staff_area_selector".equals(setting.tag)
                || "hostility_staff_corner_mode".equals(setting.tag)
                || "hostility_staff_expand_mode".equals(setting.tag))) {
                final var checkbox = new HostilityCheckbox(
                    posX + 10,
                    posY + 15 + 30 * i,
                    20,
                    20,
                    setting.name,
                    setting.description,
                    tag.getBooleanOr(setting.tag, setting.defaultValue)
                );

                checkbox.active = false;
                checkbox.isSelected = false;
                checkboxes.putIfAbsent(setting, addRenderableWidget(checkbox));
            } else if (!tag.getBooleanOr("hostility_staff_area_selector", false)
                && ("hostility_staff_corner_mode".equals(setting.tag) || "hostility_staff_expand_mode".equals(setting.tag))) {
                final var checkbox = new HostilityCheckbox(
                    posX + 10,
                    posY + 15 + 30 * i,
                    20,
                    20,
                    setting.name,
                    setting.description,
                    tag.getBooleanOr(setting.tag, setting.defaultValue)
                );

                checkbox.active = false;
                checkboxes.putIfAbsent(setting, addRenderableWidget(checkbox));
            } else checkboxes.putIfAbsent(
                setting,
                addRenderableWidget(new HostilityCheckbox(
                    posX + 10,
                    posY + 15 + 30 * i,
                    20,
                    20,
                    setting.name,
                    setting.description,
                    tag.getBooleanOr(setting.tag, setting.defaultValue)
                ))
            );
        }

        setupRadioBehavior();
    }

    private void setupRadioBehavior() {
        final var skillSetting = settings.stream()
            .filter(s -> "hostility_staff_skill".equals(s.tag))
            .findFirst().orElse(null);

        final var areaSetting = settings.stream()
            .filter(s -> "hostility_staff_area_selector".equals(s.tag))
            .findFirst().orElse(null);

        final var cornerSetting = settings.stream()
            .filter(s -> "hostility_staff_corner_mode".equals(s.tag))
            .findFirst().orElse(null);

        final var expandSetting = settings.stream()
            .filter(s -> "hostility_staff_expand_mode".equals(s.tag))
            .findFirst().orElse(null);

        if (skillSetting == null || areaSetting == null || cornerSetting == null || expandSetting == null) return;
        final var skillCheckbox = checkboxes.get(skillSetting);
        final var areaCheckbox = checkboxes.get(areaSetting);
        final var cornerCheckbox = checkboxes.get(cornerSetting);
        final var expandCheckbox = checkboxes.get(expandSetting);
        if (skillCheckbox == null || areaCheckbox == null || cornerCheckbox == null || expandCheckbox == null) return;
        skillCheckbox.setOnPressCallback(() -> {
            final var enbaled = skillCheckbox.isSelected;
            areaCheckbox.active = !enbaled;
            cornerCheckbox.active = !enbaled;
            expandCheckbox.active = !enbaled;
            if (enbaled) {
                areaCheckbox.isSelected = false;
                cornerCheckbox.isSelected = false;
                expandCheckbox.isSelected = false;
            }
        });

        areaCheckbox.setOnPressCallback(() -> {
            final var enabled = areaCheckbox.isSelected;
            cornerCheckbox.active = enabled;
            expandCheckbox.active = enabled;
        });

        cornerCheckbox.setOnPressCallback(() -> {
            if (cornerCheckbox.isSelected) expandCheckbox.isSelected = false;
        });

        expandCheckbox.setOnPressCallback(() -> {
            if (expandCheckbox.isSelected) cornerCheckbox.isSelected = false;
        });
    }

    @Override
    public void extractBackground(
        @NonNull final GuiGraphicsExtractor graphics,
        final int mouseX,
        final int mouseY,
        final float a
    ) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            BACKGROUND_TEXTURE,
            posX,
            posY,
            0f,
            0f,
            imageWidth,
            imageHeight,
            imageWidth,
            imageHeight
        );
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (minecraft.options.keyInventory.matches(event)) {
            onClose();
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        super.onClose();
        for (final var entry : checkboxes.entrySet()) {
            final var setting = entry.getKey();
            final var checkbox = entry.getValue();
            ClientPlayNetworking.send(
                new ServerboundHostilityStaffSettingPayload(player.getId(), setting.tag, checkbox.isSelected)
            );
        }
    }
}
