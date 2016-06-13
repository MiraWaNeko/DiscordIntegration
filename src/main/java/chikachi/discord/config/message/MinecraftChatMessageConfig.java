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

import chikachi.discord.DiscordClient;
import com.google.common.base.Joiner;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;

import java.io.IOException;

public class MinecraftChatMessageConfig extends BaseMessageConfig {
    private boolean relaySayCommand = true;

    public MinecraftChatMessageConfig(boolean enabled, String message) {
        super("chat", enabled, message);
    }

    public void handleCommandEvent(CommandEvent event) {
        if (!this.isEnabled() || !this.relaySayCommand) {
            return;
        }

        String username = event.sender.getName();

        String message = Joiner.on(" ").join(event.parameters);
        message = message.replaceAll("§.", "");

        DiscordClient.getInstance().sendMessage(
                this.getMessage()
                        .replace("%USER%", username)
                        .replace("%MESSAGE%", message)
        );
    }

    public void handleChatEvent(ServerChatEvent event) {
        if (!this.isEnabled()) return;

        String username = event.username;

        String message = event.message;
        message = message.replaceAll("§.", "");

        DiscordClient.getInstance().sendMessage(
                this.getMessage()
                        .replace("%USER%", username)
                        .replace("%MESSAGE%", message)
        );
    }

    @Override
    protected void readExtra(JsonReader reader, String name) throws IOException {
        if (name.equalsIgnoreCase("relaySayCommand") && reader.peek() == JsonToken.BOOLEAN) {
            this.relaySayCommand = reader.nextBoolean();
            return;
        }

        reader.skipValue();
    }

    @Override
    protected void writeExtra(JsonWriter writer) throws IOException {
        writer.name("relaySayCommand");
        writer.value(this.relaySayCommand);
    }
}
