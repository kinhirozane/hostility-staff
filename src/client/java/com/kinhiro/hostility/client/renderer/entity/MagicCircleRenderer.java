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

import com.kinhiro.hostility.client.renderer.HostilityRenderTypes;
import com.kinhiro.hostility.client.util.HollowCylinderGeometry;
import com.kinhiro.hostility.client.util.MagicCircleQuadGeometry;
import com.kinhiro.hostility.common.entity.MagicCircle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.jspecify.annotations.NonNull;

public final class MagicCircleRenderer extends EntityRenderer<MagicCircle, MagicCircleRenderState> {
    public MagicCircleRenderer(final EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NonNull MagicCircleRenderState createRenderState() {
        return new MagicCircleRenderState();
    }

    @Override
    public void extractRenderState(
        final @NonNull MagicCircle entity,
        final @NonNull MagicCircleRenderState state,
        final float partialTicks
    ) {
        super.extractRenderState(entity, state, partialTicks);
        state.radius = entity.getEntityData().get(MagicCircle.DATA_RADIUS);
    }

    @Override
    public void submit(
        final @NonNull MagicCircleRenderState state,
        final @NonNull PoseStack poseStack,
        final @NonNull SubmitNodeCollector submitNodeCollector,
        final @NonNull CameraRenderState camera
    ) {
        super.submit(state, poseStack, submitNodeCollector, camera);

        poseStack.pushPose();
        poseStack.translate(0f, 0.01f, 0f);
        poseStack.scale(state.radius, 1f, state.radius);
        submitNodeCollector.submitCustomGeometry(
            poseStack,
            HostilityRenderTypes.MAGIC_CIRCLE,
            (pose, buffer) -> MagicCircleQuadGeometry.forEachVertex((position, uv) ->
                buffer.addVertex(pose, position.x, position.y, position.z)
                    .setUv(uv.x, uv.y))
        );

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0f, 2.5f, 0f);
        poseStack.scale(state.radius, 2f, state.radius);
        submitNodeCollector.submitCustomGeometry(
            poseStack,
            HostilityRenderTypes.MAGIC_CIRCLE_CYLINDER,
            (pose, buffer) -> HollowCylinderGeometry.forEachVertex((position, uv) ->
                buffer.addVertex(pose, position.x, position.y, position.z)
                    .setUv(uv.x, uv.y)
                    .setColor(1f, 1f, 1f, state.speed))
        );

        poseStack.popPose();
    }

    @Override
    protected boolean affectedByCulling(final @NonNull MagicCircle entity) {
        return false;
    }
}
