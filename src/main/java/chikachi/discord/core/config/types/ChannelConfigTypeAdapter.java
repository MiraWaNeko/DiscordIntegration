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

public class ChannelConfigTypeAdapter implements JsonSerializer<ChannelConfigType>, JsonDeserializer<ChannelConfigType> {
    @Override
    public JsonElement serialize(ChannelConfigType src, Type typeOfSrc, JsonSerializationContext context) {
        if (src.isDisabled()) {
            return new JsonPrimitive(false);
        }
        if (src.isDefault()) {
            return new JsonPrimitive(true);
        }

        ArrayList<Long> channels = src.getChannels();
        if (channels == null) {
            return null;
        }

        if (channels.size() == 0) {
            return new JsonPrimitive(false);
        }

        if (channels.size() == 1) {
            return new JsonPrimitive(channels.get(0));
        }

        JsonArray array = new JsonArray();
        for (Long channelId : channels) {
            array.add(new JsonPrimitive(channelId));
        }
        return array;
    }

    @Override
    public ChannelConfigType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ChannelConfigType channelConfigType = new ChannelConfigType();

        if (json.isJsonArray()) {
            ArrayList<Long> channels = new ArrayList<>();

            JsonArray array = json.getAsJsonArray();
            for (JsonElement element : array) {
                if (element.isJsonPrimitive()) {
                    JsonPrimitive primitive = (JsonPrimitive) element;
                    if (primitive.isNumber()) {
                        Long longValue = primitive.getAsLong();
                        if (longValue > 0) {
                            channels.add(longValue);
                        }
                    }
                }
            }

            channelConfigType
                .setChannels(channels)
                .setDefault(false)
                .setDisabled(false);
        } else if (json.isJsonPrimitive()) {
            JsonPrimitive primitive = (JsonPrimitive) json;
            if (primitive.isNumber()) {
                Long longValue = primitive.getAsLong();
                if (longValue > 0) {
                    channelConfigType.addChannel(longValue)
                        .setDefault(false)
                        .setDisabled(false);
                }
            } else if (primitive.isBoolean()) {
                if (primitive.getAsBoolean()) {
                    channelConfigType
                        .setDefault(true)
                        .setDisabled(false);
                } else {
                    channelConfigType
                        .setDefault(false)
                        .setDisabled(true);
                }
            }
        }

        return channelConfigType;
    }
}
