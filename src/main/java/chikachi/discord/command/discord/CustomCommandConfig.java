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

import chikachi.discord.command.DiscordCommandSender;
import com.google.common.base.Joiner;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import net.dv8tion.jda.entities.User;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomCommandConfig extends CommandConfig {
    private final String command;

    private CustomCommandConfig(String name, String command, String... defaultRoles) {
        super(name, true, defaultRoles);

        this.command = command;
    }

    public static CustomCommandConfig createFromConfig(JsonReader reader) throws IOException {
        String name = null;
        String command = null;
        List<String> roles = new ArrayList<>();

        if (reader.peek() == JsonToken.BEGIN_OBJECT) {
            reader.beginObject();
            while (reader.hasNext()) {
                String _name = reader.nextName();
                if (_name.equalsIgnoreCase("name") && reader.peek() == JsonToken.STRING) {
                    name = reader.nextString();
                } else if ((_name.equalsIgnoreCase("cmd") || _name.equalsIgnoreCase("command")) && reader.peek() == JsonToken.STRING) {
                    command = reader.nextString();
                } else if (_name.equalsIgnoreCase("roles") && reader.peek() == JsonToken.BEGIN_ARRAY) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        if (reader.peek() == JsonToken.STRING) {
                            roles.add(reader.nextString().toLowerCase());
                        }
                    }
                    reader.endArray();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            if (name != null && command != null) {
                return new CustomCommandConfig(name, command, roles.toArray(new String[roles.size()]));
            }
        }
        return null;
    }

    @Override
    public void execute(User user, List<String> args) {
        String cmd = this.command;

        int argsCount = args.size();
        if (argsCount > 0) {
            for (int i = 0; i < argsCount; i++) {
                cmd = cmd.replace("%" + (i + 1) + "%", args.get(i));
            }
            cmd = cmd.replace("%args%", Joiner.on(' ').join(args));
        }
        cmd = cmd.replaceAll("/%([0-9]+|args)%/", "");

        MinecraftServer.getServer().getCommandManager().executeCommand(
                new DiscordCommandSender(user),
                cmd
        );
    }
}
