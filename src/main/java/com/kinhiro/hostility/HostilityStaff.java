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
package com.kinhiro.hostility;

import com.kinhiro.hostility.common.component.HostilityDataComponents;
import com.kinhiro.hostility.common.entity.HostilityEntities;
import com.kinhiro.hostility.common.item.HostilityItems;
import com.kinhiro.hostility.common.network.NetworkHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public class HostilityStaff implements ModInitializer {
    public static final String NAMESPACE = "hostility_staff";

    @Override
    public void onInitialize() {
        HostilityDataComponents.initialize();
        HostilityItems.initialize();
        HostilityEntities.initialize();
        NetworkHandler.initialize();
    }

    public static Identifier id(String id) {
        return Identifier.fromNamespaceAndPath(NAMESPACE, id);
    }
}
