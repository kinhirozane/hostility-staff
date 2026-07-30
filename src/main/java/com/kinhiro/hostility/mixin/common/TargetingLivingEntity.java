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

import com.kinhiro.hostility.api.mixin.Targetable;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public abstract class TargetingLivingEntity implements Targetable {
    @Unique
    private boolean hostility_staff$targeting;

    @Unique
    @Nullable
    private LivingEntity hostility_staff$forcedTarget;

    @Override
    public boolean hostility_staff$targeting() {
        return this.hostility_staff$targeting;
    }

    @Override
    public void hostility_staff$targeting(final boolean targeting) {
        this.hostility_staff$targeting = targeting;
    }

    @Override
    public @Nullable LivingEntity hostility_staff$forcedTarget() {
        return this.hostility_staff$forcedTarget;
    }

    @Override
    public void hostility_staff$forcedTarget(@Nullable final LivingEntity target) {
        this.hostility_staff$forcedTarget = target;
    }
}
