/**
 * Copyright (C) 2016 Chikachi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package chikachi.discord.config.message;

import chikachi.discord.ChikachiDiscord;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public abstract class BaseMessageConfig {
    private final String name;
    private boolean enabled;
    protected String message;

    BaseMessageConfig(String name, boolean enabled, String message) {
        this.name = name;
        this.enabled = enabled;
        this.message = message;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getMessage() {
        return this.message;
    }

    public void read(JsonReader reader) throws IOException {
        String name;

        JsonToken type = reader.peek();

        if (type == JsonToken.BEGIN_OBJECT) {
            reader.beginObject();
            while (reader.hasNext()) {
                name = reader.nextName();
                if (name.equalsIgnoreCase("enabled") && reader.peek() == JsonToken.BOOLEAN) {
                    this.enabled = reader.nextBoolean();
                } else if (name.equalsIgnoreCase("message") && reader.peek() == JsonToken.STRING) {
                    this.message = reader.nextString();
                } else {
                    this.readExtra(reader, name);
                }
            }
            reader.endObject();
        } else if (type == JsonToken.STRING) {
            this.message = reader.nextString();
            this.enabled = !this.message.equals("");
        } else {
            ChikachiDiscord.Log(String.format("Invalid value of message config for %s", this.name), true);
            reader.skipValue();
        }
    }

    public void write(JsonWriter writer) throws IOException {
        writer.name(this.name);
        writer.beginObject();
        writer.name("enabled");
        writer.value(this.enabled);
        writer.name("message");
        writer.value(this.message);
        this.writeExtra(writer);
        writer.endObject();
    }

    protected void readExtra(JsonReader reader, String name) throws IOException {
        reader.skipValue();
    }

    protected void writeExtra(JsonWriter writer) throws IOException {
    }
}
