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

import com.kinhiro.hostility.common.component.AreaPosition;
import com.kinhiro.hostility.common.component.HostilityDataComponents;
import com.kinhiro.hostility.common.component.TargetUuid;
import com.kinhiro.hostility.common.component.TargetUuidList;
import com.kinhiro.hostility.common.item.HostilityItems;
import com.kinhiro.hostility.common.item.HostilityStaffItem;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Items.class)
public abstract class MixinItems {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void hostility_staff$clinit(CallbackInfo ci) {
        HostilityItems.HOSTILITY_STAFF = Registry.register(
            BuiltInRegistries.ITEM,
            HostilityItems.HOSTILITY_STAFF_KEY,
            new HostilityStaffItem(
                new Item.Properties().setId(HostilityItems.HOSTILITY_STAFF_KEY)
                    .rarity(Rarity.EPIC)
                    .fireResistant()
                    .stacksTo(1)
                    .component(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                    .component(HostilityDataComponents.AREA_POSITION, AreaPosition.EMPTY)
                    .component(HostilityDataComponents.TARGET_UUID, TargetUuid.EMPTY)
                    .component(HostilityDataComponents.TARGET_UUID_LIST, TargetUuidList.EMPTY)
                    .component(DataComponents.ATTACK_RANGE, new AttackRange(2f, 128f, 2f, 128f, 0.125f, 0.5f))
            )
        );
    }
}
