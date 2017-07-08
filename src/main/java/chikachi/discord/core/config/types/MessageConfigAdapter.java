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

public class MessageConfigAdapter implements JsonSerializer<MessageConfig>, JsonDeserializer<MessageConfig> {
    @Override
    public JsonElement serialize(MessageConfig src, Type typeOfSrc, JsonSerializationContext context) {
        if (src.normal.equals(src.webhook)) {
            return new JsonPrimitive(src.normal);
        }

        JsonObject object = new JsonObject();
        object.add("normal", new JsonPrimitive(src.normal));
        object.add("webhook", new JsonPrimitive(src.webhook));
        return object;
    }

    @Override
    public MessageConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String normal = null;
        String webhook = null;

        if (json.isJsonObject()) {
            JsonObject object = json.getAsJsonObject();

            if (object.has("normal")) {
                normal = object.get("normal").getAsString();
            }
            if (object.has("webhook")) {
                webhook = object.get("webhook").getAsString();
            }

            if (normal == null && webhook == null) {
                return null;
            }

            return new MessageConfig(normal != null ? normal : webhook, webhook != null ? webhook : normal);
        } else if (json.isJsonPrimitive()) {
            return new MessageConfig(json.getAsString());
        }

        return null;
    }
}
