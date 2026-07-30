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
package com.kinhiro.hostility.client.renderer.entity;

import com.kinhiro.hostility.HostilityStaff;
import com.kinhiro.hostility.client.renderer.HostilityRenderTypes;
import com.kinhiro.hostility.common.entity.Hostility;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public final class HostilityRenderer extends EntityRenderer<Hostility, HostilityRenderState> {
    private static final Identifier TEXTURE = HostilityStaff.id("textures/vfx/hostility.png");

    public HostilityRenderer(final EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NonNull HostilityRenderState createRenderState() {
        return new HostilityRenderState();
    }

    @Override
    public void extractRenderState(
        final @NonNull Hostility entity,
        final @NonNull HostilityRenderState state,
        final float partialTicks
    ) {
        super.extractRenderState(entity, state, partialTicks);
        state.size = entity.getEntityData().get(Hostility.DATA_SIZE);
    }

    @Override
    public void submit(
        final @NonNull HostilityRenderState state,
        final @NonNull PoseStack poseStack,
        final @NonNull SubmitNodeCollector submitNodeCollector,
        final @NonNull CameraRenderState camera
    ) {
        super.submit(state, poseStack, submitNodeCollector, camera);
        poseStack.pushPose();
        poseStack.mulPose(camera.orientation);
        final var skullScale = Mth.clamp(state.size * 0.25f, 0.025f, 0.325f);
        final var flutter = 1.0f + Mth.sin(state.ageInTicks * 8.0f) * 0.025f;
        poseStack.scale(skullScale * flutter, skullScale * flutter, skullScale * flutter);
        submitNodeCollector.submitCustomGeometry(
            poseStack,
            HostilityRenderTypes.hostility(TEXTURE),
            (pose, buffer) -> {
                buffer.addVertex(pose, -1f, -1f, 0f).setUv(0f, 1f);
                buffer.addVertex(pose, -1f, 1f, 0f).setUv(0f, 0f);
                buffer.addVertex(pose, 1f, 1f, 0f).setUv(1f, 0f);
                buffer.addVertex(pose, 1f, -1f, 0f).setUv(1f, 1f);
            }
        );

        poseStack.popPose();
    }

    @Override
    protected boolean affectedByCulling(final @NonNull Hostility entity) {
        return false;
    }
}
