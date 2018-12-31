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

package net.discordintegration.core.config.types;

import java.util.ArrayList;

public class DimensionConfigType {
    private ArrayList<Integer> dimensions;
    private boolean isDefault;
    private boolean isDisabled;

    public DimensionConfigType() {
        this(false);
    }

    public DimensionConfigType(boolean isDisabled) {
        this(!isDisabled, isDisabled);
    }

    public DimensionConfigType(boolean isDefault, boolean isDisabled) {
        this(new ArrayList<>(), isDefault, isDisabled);
    }

    public DimensionConfigType(ArrayList<Integer> dimensions, boolean isDefault, boolean isDisabled) {
        this.dimensions = dimensions;
        this.isDefault = isDefault;
        this.isDisabled = isDisabled;
    }

    public DimensionConfigType addDimension(int dimension) {
        this.dimensions.add(dimension);
        return this;
    }

    public ArrayList<Integer> getDimensions() {
        return isDisabled() ? null : (isDefault() ? new ArrayList<>() : dimensions);
    }

    public ArrayList<Integer> getDimensions(ArrayList<Integer> defaultDimensions) {
        return isDisabled() ? null : (isDefault() ? defaultDimensions : dimensions);
    }

    public ArrayList<Integer> getDimensions(DimensionConfigType defaultDimensions) {
        return getDimensions(defaultDimensions.dimensions);
    }

    public DimensionConfigType setDimensions(ArrayList<Integer> dimensions) {
        if (dimensions != null) {
            this.dimensions = dimensions;
        }
        return this;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public DimensionConfigType setDefault(boolean aDefault) {
        isDefault = aDefault;
        return this;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public DimensionConfigType setDisabled(boolean disabled) {
        isDisabled = disabled;
        return this;
    }
}
