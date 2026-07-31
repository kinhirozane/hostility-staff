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
package com.kinhiro.hostility.mixin.common;

import com.kinhiro.hostility.common.entity.Hostility;
import com.kinhiro.hostility.common.entity.HostilityEntities;
import com.kinhiro.hostility.common.entity.MagicCircle;
import com.kinhiro.hostility.common.entity.PreviewHostility;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTypes.class)
public abstract class MixinEntityTypes {
    @Shadow
    private static <T extends Entity> EntityType<T> register(
        final ResourceKey<EntityType<?>> id,
        final EntityType.Builder<T> builder
    ) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void hostility_staff$clinit(CallbackInfo ci) {
        HostilityEntities.HOSTILITY = register(
            HostilityEntities.HOSTILITY_KEY,
            EntityType.Builder.<Hostility>of(Hostility::new, MobCategory.MISC)
                .sized(0f, 0f)
                .clientTrackingRange(8)
                .fireImmune()
                .noLootTable()
                .noSave()
                .noSummon()
        );

        HostilityEntities.MAGIC_CIRCLE = register(
            HostilityEntities.MAGIC_CIRCLE_KEY,
            EntityType.Builder.<MagicCircle>of(MagicCircle::new, MobCategory.MISC)
                .sized(0f, 0f)
                .clientTrackingRange(8)
                .fireImmune()
                .noLootTable()
                .noSave()
                .noSummon()
        );

        HostilityEntities.PREVIEW_HOSTILITY = register(
            HostilityEntities.PREVIEW_HOSTILITY_KEY,
            EntityType.Builder.<PreviewHostility>of(PreviewHostility::new, MobCategory.MISC)
                .sized(0f, 0f)
                .clientTrackingRange(8)
                .fireImmune()
                .noLootTable()
                .noSave()
                .noSummon()
        );
    }
}
