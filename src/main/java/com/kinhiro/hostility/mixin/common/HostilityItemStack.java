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

import com.kinhiro.hostility.common.item.HostilityStaffItem;
import com.kinhiro.hostility.util.TextEffect;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class HostilityItemStack {
    @WrapMethod(method = "getHoverName")
    private Component hostility_staff$getHoverName(final Operation<Component> original) {
        final var stack = (ItemStack) (Object) this;
        if (!stack.isEmpty() && stack.getItem() instanceof HostilityStaffItem)
            return TextEffect.hostility(original.call(), true, true);

        return original.call();
    }

    @WrapMethod(method = "getCustomName")
    private Component hostility_staff$getCustomName(final Operation<Component> original) {
        final var stack = (ItemStack) (Object) this;
        if (!stack.isEmpty() && stack.getItem() instanceof HostilityStaffItem) {
            final var name = original.call();
            if (name != null) return TextEffect.hostility(name, true, true);
        }

        return original.call();
    }
}
