/*
 * Copyright (C) 2017 Chikachi
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

package chikachi.discord.core.config.types;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DimensionConfigTypeAdapter implements JsonSerializer<DimensionConfigType>, JsonDeserializer<DimensionConfigType> {
    @Override
    public JsonElement serialize(DimensionConfigType src, Type typeOfSrc, JsonSerializationContext context) {
        if (src.isDisabled()) {
            return new JsonPrimitive(false);
        }
        if (src.isDefault()) {
            return new JsonPrimitive(true);
        }

        ArrayList<Integer> dimensions = src.getDimensions();
        if (dimensions == null) {
            return null;
        }

        if (dimensions.size() == 0) {
            return new JsonPrimitive(false);
        }

        if (dimensions.size() == 1) {
            return new JsonPrimitive(dimensions.get(0));
        }

        JsonArray array = new JsonArray();
        for (Integer dimensionId : dimensions) {
            array.add(new JsonPrimitive(dimensionId));
        }
        return array;
    }

    @Override
    public DimensionConfigType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        DimensionConfigType dimensionConfigType = new DimensionConfigType();

        if (json.isJsonArray()) {
            ArrayList<Integer> dimensions = new ArrayList<>();

            JsonArray array = json.getAsJsonArray();
            for (JsonElement element : array) {
                if (element.isJsonPrimitive()) {
                    JsonPrimitive primitive = (JsonPrimitive) element;
                    if (primitive.isNumber()) {
                        int intValue = primitive.getAsInt();
                        if (intValue > 0) {
                            dimensions.add(intValue);
                        }
                    }
                }
            }

            dimensionConfigType
                .setDimensions(dimensions)
                .setDefault(false)
                .setDisabled(false);
        } else if (json.isJsonPrimitive()) {
            JsonPrimitive primitive = (JsonPrimitive) json;
            if (primitive.isNumber()) {
                int intValue = primitive.getAsInt();
                if (intValue > 0) {
                    dimensionConfigType
                        .addDimension(intValue)
                        .setDefault(false)
                        .setDisabled(false);
                }
            } else if (primitive.isBoolean()) {
                if (primitive.getAsBoolean()) {
                    dimensionConfigType
                        .setDefault(true)
                        .setDisabled(false);
                } else {
                    dimensionConfigType
                        .setDefault(false)
                        .setDisabled(true);
                }
            }
        }

        return dimensionConfigType;
    }
}
