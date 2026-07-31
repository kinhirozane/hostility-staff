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
package com.kinhiro.hostility.common.item;

import com.google.common.collect.ImmutableList;
import com.kinhiro.hostility.common.component.AreaPosition;
import com.kinhiro.hostility.common.component.HostilityDataComponents;
import com.kinhiro.hostility.common.component.TargetUuid;
import com.kinhiro.hostility.common.component.TargetUuidList;
import com.kinhiro.hostility.common.entity.MagicCircle;
import com.kinhiro.hostility.common.entity.PreviewHostility;
import com.kinhiro.hostility.common.network.ClientboundClearSelectionPayload;
import com.kinhiro.hostility.common.network.ClientboundOpenHostilityStaffScreenPayload;
import com.kinhiro.hostility.util.Targeting;
import com.kinhiro.hostility.util.TextEffect;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public final class HostilityStaffItem extends Item {
    public static final ImmutableList<HostilityStaffSetting> SETTINGS = ImmutableList.<HostilityStaffSetting>builder()
        .add(new HostilityStaffSetting(
            Component.translatable("gui.hostility_staff.skill"),
            Component.translatable("gui.hostility_staff.skill.description"),
            "hostility_staff_skill",
            false
        ))
        .add(new HostilityStaffSetting(
            Component.translatable("gui.hostility_staff.area_selector"),
            Component.translatable("gui.hostility_staff.area_selector.description"),
            "hostility_staff_area_selector",
            false
        ))
        .add(new HostilityStaffSetting(
            Component.translatable("gui.hostility_staff.corner_mode"),
            Component.translatable("gui.hostility_staff.corner_mode.description"),
            "hostility_staff_corner_mode",
            false
        ))
        .add(new HostilityStaffSetting(
            Component.translatable("gui.hostility_staff.expand_mode"),
            Component.translatable("gui.hostility_staff.expand_mode.description"),
            "hostility_staff_expand_mode",
            false
        ))
        .build();

    public HostilityStaffItem(Properties properties) {
        super(properties);
    }

    public boolean onLeftClickEntity(
        @NonNull final ItemStack stack,
        @NonNull final Player player,
        @NonNull final Entity entity
    ) {
        if (stack.isEmpty() || !(stack.getItem() instanceof HostilityStaffItem)) return false;
        if (!player.level().isClientSide()) {
            checkStatus(stack);
            final var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            if (tag.getBooleanOr("hostility_staff_skill", false)) return true;
            final var living = Targeting.tryGetLivingTarget(entity);
            if (!(living instanceof final Mob target)) return false;
            final var uuidList = stack.get(HostilityDataComponents.TARGET_UUID_LIST);
            if (uuidList != null && !uuidList.uuids().isEmpty()) {
                for (final var uuid : uuidList.uuids()) {
                    final var mob = Targeting.tryFromUuid(player.level(), uuid);
                    if (mob != null && mob != target)
                        Targeting.setAttackTarget(mob, target, false);
                }

                stack.remove(HostilityDataComponents.TARGET_UUID_LIST);
                stack.remove(HostilityDataComponents.AREA_POSITION);
                clearPreviewHostility(player.level(), player, null);
                if (player instanceof final ServerPlayer serverPlayer) ServerPlayNetworking.send(
                    serverPlayer,
                    new ClientboundClearSelectionPayload(serverPlayer.getId())
                );

                return true;
            }

            final var targetUuid = stack.get(HostilityDataComponents.TARGET_UUID);
            if (targetUuid != null && targetUuid.uuid().isPresent()) {
                final var storedTarget = Targeting.tryFromUuid(player.level(), targetUuid.uuid().get());
                if (target != storedTarget) {
                    Targeting.setAttackTarget(target, storedTarget, true);
                    stack.remove(HostilityDataComponents.TARGET_UUID);
                    clearPreviewHostility(player.level(), player, storedTarget);
                    return true;
                }
            } else {
                final var uuid = Optional.of(target.getUUID());
                final var name = Optional.of(target.getName());
                stack.set(HostilityDataComponents.TARGET_UUID, new TargetUuid(uuid, name));
                addPreviewHostility(player.level(), target);
                final var message = Component.empty();
                message.append(TextEffect.hostility(Component.translatable("message.hostility_staff.add"), false, false));
                message.append(TextEffect.hostility(target.getName(), true, true));
                player.sendOverlayMessage(message);
                return true;
            }
        }

        return true;
    }

    @Override
    public @NonNull InteractionResult use(
        @NonNull final Level level,
        @NonNull final Player player,
        @NonNull final InteractionHand hand
    ) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.FAIL;
        if (level.isClientSide()) return InteractionResult.PASS;
        final var stack = player.getItemInHand(hand);
        if (stack.isEmpty() || !(stack.getItem() instanceof HostilityStaffItem)) return InteractionResult.FAIL;
        if (player.isShiftKeyDown()) {
            if (player instanceof final ServerPlayer serverPlayer)
                ServerPlayNetworking.send(
                    serverPlayer,
                    new ClientboundOpenHostilityStaffScreenPayload(serverPlayer.getId())
                );

            return InteractionResult.SUCCESS;
        }

        checkStatus(stack);
        var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.getBooleanOr("hostility_staff_skill", false)) {
            final var entities = player.level().getEntities(null, player.getBoundingBox().inflate(128d)).stream()
                .filter(e -> e instanceof final MagicCircle circle && circle.owner == player.getUUID())
                .toList();

            entities.forEach(Entity::discard);
            if (!entities.isEmpty()) player.sendOverlayMessage(
                TextEffect.hostility(Component.translatable("message.hostility_staff.reset"), false, false)
            );
        }

        final var targetUuid = stack.get(HostilityDataComponents.TARGET_UUID);
        if (targetUuid != null) {
            stack.remove(HostilityDataComponents.TARGET_UUID);
            player.sendOverlayMessage(
                TextEffect.hostility(Component.translatable("message.hostility_staff.reset"), false, false)
            );
        }

        final var targetUuidList = stack.get(HostilityDataComponents.TARGET_UUID_LIST);
        if (targetUuidList != null) {
            stack.remove(HostilityDataComponents.TARGET_UUID_LIST);
            player.sendOverlayMessage(
                TextEffect.hostility(Component.translatable("message.hostility_staff.reset"), false, false)
            );
        }

        clearPreviewHostility(level, player, null);
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NonNull InteractionResult useOn(@NonNull final UseOnContext context) {
        if (context.getLevel().isClientSide()) return InteractionResult.FAIL;
        if (context.getPlayer() == null) return InteractionResult.FAIL;
        if (context.getHand() != InteractionHand.MAIN_HAND) return InteractionResult.FAIL;
        final var stack = context.getItemInHand();
        if (stack.isEmpty() || !(stack.getItem() instanceof HostilityStaffItem)) return InteractionResult.FAIL;
        checkStatus(stack);
        final var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.getBooleanOr("hostility_staff_skill", false)) return InteractionResult.PASS;
        if (tag.getBooleanOr("hostility_staff_area_selector", false)) {
            if (tag.getBooleanOr("hostility_staff_corner_mode", false)) {
                var area = stack.getOrDefault(HostilityDataComponents.AREA_POSITION, AreaPosition.EMPTY);
                var update = false;
                if (area.from() != null && context.getPlayer().isShiftKeyDown()) {
                    area = area.withFrom(context.getClickedPos());
                    update = true;
                } else if (area.from() == null) {
                    area = area.withFrom(context.getClickedPos());
                    update = true;
                } else if (!Objects.equals(context.getClickedPos(), area.from())) {
                    area = area.withTo(context.getClickedPos());
                    update = true;
                }

                if (update) {
                    stack.set(HostilityDataComponents.AREA_POSITION, area);
                    final var aabb = Targeting.getBoundingBoxSelectedArea(area.from(), area.to());
                    final var mobs = context.getLevel().getEntities(null, aabb).stream()
                        .filter(m -> m instanceof Mob)
                        .map(m -> (Mob) m)
                        .toList();

                    clearPreviewHostility(context.getLevel(), context.getPlayer(), null);
                    mobs.forEach(mob -> addPreviewHostility(context.getLevel(), mob));
                    final var uuids = mobs.stream().map(Entity::getUUID).toList();

                    stack.set(HostilityDataComponents.TARGET_UUID_LIST, new TargetUuidList(uuids));
                    final var message = Component.empty();
                    message.append(
                        TextEffect.hostility(Component.translatable("message.hostility_staff.add"), false, false)
                    );

                    message.append(TextEffect.hostility(Component.literal(String.valueOf(uuids.size())), true, true));
                    context.getPlayer().sendOverlayMessage(message);
                    return InteractionResult.CONSUME;
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(
        @NonNull final ItemStack stack,
        @NonNull final TooltipContext context,
        @NonNull final TooltipDisplay display,
        @NonNull final Consumer<Component> builder,
        @NonNull final TooltipFlag flag
    ) {
        if (stack.isEmpty() || !(stack.getItem() instanceof HostilityStaffItem)) return;
        builder.accept(Component.literal(""));
        builder.accept(
            TextEffect.hostility(Component.translatable("tooltip.hostility_staff.enabled_features"), false, false)
        );

        final var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        var empty = true;
        for (final var setting : SETTINGS)
            if (tag.contains(setting.tag)) {
                final var enabled = tag.getBooleanOr(setting.tag, false);
                final var message = Component.empty();
                message.append(
                    Component.literal(enabled ? "✔ " : "✘ ").withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED)
                );

                message.append(
                    Component.literal(setting.name.getString()).withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED)
                );

                builder.accept(message);
                empty = false;
            }

        if (empty) builder.accept(Component.literal("✘ *").withStyle(ChatFormatting.RED));
        builder.accept(Component.literal(""));
        if (tag.getBooleanOr("hostility_staff_skill", false)) {
            final var message = Component.empty();
            message.append(Component.translatable("tooltip.hostility_staff.unleash_skill"));
            message.append(TextEffect.hostility(Component.translatable("gui.hostility_staff.skill"), true, true));
            builder.accept(message);
        }

        if (tag.getBooleanOr("hostility_staff_corner_mode", false))
            builder.accept(Component.translatable("tooltip.hostility_staff.corner_mode"));

        if (tag.getBooleanOr("hostility_staff_expand_mode", false)) {
            builder.accept(Component.translatable("tooltip.hostility_staff.expand_mode.1"));
            builder.accept(Component.translatable("tooltip.hostility_staff.expand_mode.2"));
            builder.accept(Component.translatable("tooltip.hostility_staff.expand_mode.3"));
        }

        if (!tag.getBooleanOr("hostility_staff_skill", false))
            builder.accept(Component.translatable("tooltip.hostility_staff.left_click"));

        builder.accept(Component.translatable("tooltip.hostility_staff.right_click"));
        builder.accept(Component.translatable("tooltip.hostility_staff.shift_right_click"));
    }

    @Override
    public @NonNull Component getName(final @NonNull ItemStack stack) {
        return TextEffect.hostility(super.getName(stack), true, true);
    }

    @Override
    public void inventoryTick(
        @NonNull final ItemStack stack,
        @NonNull final ServerLevel level,
        @NonNull final Entity owner,
        @Nullable final EquipmentSlot slot
    ) {
        super.inventoryTick(stack, level, owner, slot);
        checkStatus(stack);
    }

    @Override
    public boolean isFoil(@NonNull final ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof HostilityStaffItem)) return false;
        final var targetUuid = stack.get(HostilityDataComponents.TARGET_UUID);
        final var targetUuidList = stack.get(HostilityDataComponents.TARGET_UUID_LIST);
        return (targetUuid != null && targetUuid.uuid().isPresent())
            || (targetUuidList != null && !targetUuidList.uuids().isEmpty());
    }

    @Override
    public boolean canDestroyBlock(
        @NonNull final ItemStack stack,
        @NonNull final BlockState state,
        @NonNull final Level level,
        @NonNull final BlockPos pos,
        @NonNull final LivingEntity user
    ) {
        return false;
    }

    @Override
    public boolean mineBlock(
        @NonNull final ItemStack stack,
        @NonNull final Level level,
        @NonNull final BlockState state,
        @NonNull final BlockPos pos,
        @NonNull final LivingEntity owner
    ) {
        return false;
    }

    private static void addPreviewHostility(final Level level, final Mob mob) {
        level.addFreshEntity(new PreviewHostility(level, mob.position(), mob));
    }

    private static void clearPreviewHostility(final Level level, final Player player, final @Nullable Mob owner) {
        level.getEntities(null, player.getBoundingBox().inflate(128d)).stream()
            .filter(e -> e instanceof final PreviewHostility marker && (owner == null || marker.owner == owner))
            .forEach(Entity::discard);
    }

    public static void checkStatus(final ItemStack stack) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            for (final var setting : SETTINGS)
                if (!tag.contains(setting.tag)) tag.putBoolean(setting.tag, setting.defaultValue);
        });
    }
}
