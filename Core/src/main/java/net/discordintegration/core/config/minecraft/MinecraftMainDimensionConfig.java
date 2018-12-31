/*
 * Copyright (C) 2018 Chikachi and other contributors
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package net.discordintegration.core.config.minecraft;

import com.google.gson.annotations.Since;

import java.util.HashMap;

public class MinecraftMainDimensionConfig {
    @Since(3.0)
    public MinecraftGenericConfig generic = new MinecraftGenericConfig();
    @Since(3.0)
    public HashMap<Integer, MinecraftDimensionConfig> dimensions = new HashMap<>();

    public MinecraftDimensionConfig getDimension(int dimension) {
        if (this.dimensions.containsKey(dimension)) {
            return this.dimensions.get(dimension);
        }

        return this.generic;
    }

    public void fillFields() {
        if (this.generic == null) {
            this.generic = new MinecraftGenericConfig();
        }
        this.generic.fillFields();

        if (this.dimensions == null) {
            this.dimensions = new HashMap<>();
        }
        this.dimensions.values().forEach(MinecraftDimensionConfig::fillFields);
    }
}
