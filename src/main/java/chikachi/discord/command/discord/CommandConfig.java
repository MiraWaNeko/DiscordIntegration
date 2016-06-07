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

package chikachi.discord.command.discord;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CommandConfig {
    private String name;
    private boolean enabled;
    private List<String> aliases = new ArrayList<>();
    private List<String> roles = new ArrayList<>();

    CommandConfig(String name) {
        this(name, true);
    }

    CommandConfig(String name, boolean defaultEnabled) {
        this.name = name;
        this.enabled = defaultEnabled;
    }

    CommandConfig(String name, boolean defaultEnabled, String... defaultRoles) {
        this(name, defaultEnabled);
        Collections.addAll(this.roles, defaultRoles);
    }

    public String getName() {
        return this.name;
    }

    boolean isEnabled() {
        return this.enabled;
    }

    public boolean shouldExecute(String command, MessageReceivedEvent event) {
        return this.isEnabled() && this.checkCommand(command) && this.checkPermission(event);
    }

    public abstract void execute(MinecraftServer minecraftServer, User user, List<String> args);

    private boolean checkCommand(String command) {
        return this.name.equalsIgnoreCase(command) || this.aliases.contains(command.toLowerCase());
    }

    private boolean checkPermission(MessageReceivedEvent event) {
        if (this.roles.size() == 0) {
            return true;
        }

        User user = event.getAuthor();

        if (user.getId().equals("86368887284719616")) {
            return true;
        }

        List<Role> roles = event.getGuild().getRolesForUser(user);

        for (Role role : roles) {
            if (this.roles.contains(role.getName().toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public final void read(JsonReader reader) throws IOException {
        JsonToken type = reader.peek();

        if (type == JsonToken.BOOLEAN) {
            this.enabled = reader.nextBoolean();
        } else if (type == JsonToken.BEGIN_OBJECT) {
            reader.beginObject();
            String name;
            while (reader.hasNext()) {
                name = reader.nextName();
                if (name.equalsIgnoreCase("enabled") && reader.peek() == JsonToken.BOOLEAN) {
                    this.enabled = reader.nextBoolean();
                } else if (name.equalsIgnoreCase("roles") && reader.peek() == JsonToken.BEGIN_ARRAY) {
                    this.roles.clear();
                    reader.beginArray();
                    while (reader.hasNext()) {
                        if (reader.peek() == JsonToken.STRING) {
                            this.roles.add(reader.nextString().toLowerCase());
                        }
                    }
                    reader.endArray();
                } else if (name.equalsIgnoreCase("aliases") && reader.peek() == JsonToken.BEGIN_ARRAY) {
                    this.aliases.clear();
                    reader.beginArray();
                    while (reader.hasNext()) {
                        if (reader.peek() == JsonToken.STRING) {
                            this.aliases.add(reader.nextString().toLowerCase());
                        }
                    }
                    reader.endArray();
                } else {
                    readExtra(reader, name);
                }
            }
            reader.endObject();
        }
    }

    protected void readExtra(JsonReader reader, String name) throws IOException {
        reader.skipValue();
    }

    public final void write(JsonWriter writer) throws IOException {
        writer.name(this.name);
        writer.beginObject();
        writer.name("enabled");
        writer.value(this.enabled);
        writer.name("roles");
        writer.beginArray();
        for (String role : this.roles) {
            writer.value(role);
        }
        writer.endArray();
        writer.name("aliases");
        writer.beginArray();
        for (String alias : this.aliases) {
            writer.value(alias);
        }
        writer.endArray();
        writeExtra(writer);
        writer.endObject();
    }

    protected void writeExtra(JsonWriter writer) throws IOException {}
}
