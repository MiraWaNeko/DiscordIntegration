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
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MinecraftChatMessageConfig extends BaseMessageConfig {
    private static Map<String, String> emoteMap;

    static {
        emoteMap = new HashMap<>();
        emoteMap.put(":)", ":slight_smile:");
        emoteMap.put(":D", ":smile:");
        emoteMap.put(";)", ":wink:");
        emoteMap.put(";D", ":wink:");
        emoteMap.put("xD", ":laughing:");
        emoteMap.put("XD", ":laughing:");
        emoteMap.put(":p", ":stuck_out_tongue:");
        emoteMap.put(":P", ":stuck_out_tongue:");
        emoteMap.put(";p", ":stuck_out_tongue_winking_eye:");
        emoteMap.put(";P", ":stuck_out_tongue_winking_eye:");
        emoteMap.put("xp", ":stuck_out_tongue_closed_eyes:");
        emoteMap.put("xP", ":stuck_out_tongue_closed_eyes:");
        emoteMap.put("Xp", ":stuck_out_tongue_closed_eyes:");
        emoteMap.put("XP", ":stuck_out_tongue_closed_eyes:");
        emoteMap.put(":O", ":open_mouth:");
        emoteMap.put(":o", ":open_mouth:");
        emoteMap.put("xO", ":dizzy_face:");
        emoteMap.put("XO", ":dizzy_face:");
        emoteMap.put(":|", ":neutral_face:");
        emoteMap.put("B)", ":sunglasses:");
        emoteMap.put(":*", ":kissing:");
        emoteMap.put(";.;", ":sob:");
        emoteMap.put(";_;", ":cry:");
        emoteMap.put("<3", ":heart:");
        emoteMap.put("</3", ":broken_heart:");
        emoteMap.put("(y)", ":thumbsup:");
        emoteMap.put("(Y)", ":thumbsup:");
        emoteMap.put("(yes)", ":thumbsup:");
        emoteMap.put("(n)", ":thumbsdown:");
        emoteMap.put("(N)", ":thumbsdown:");
        emoteMap.put("(no)", ":thumbsdown:");
        emoteMap.put("(ok)", ":ok_hand:");
    }

    private boolean relaySayCommand = true;
    private boolean useEmotes = true;

    public MinecraftChatMessageConfig(boolean enabled, String message) {
        super("chat", enabled, message);
    }

    public void handleCommandEvent(CommandEvent event) {
        if (!this.isEnabled() || !this.relaySayCommand) {
            return;
        }

        String message = Joiner.on(" ").join(event.getParameters());
        message = message.replaceAll("§.", "");

        TextComponentString chatComponent = new TextComponentString(message);

        sendMessage(
                event.getSender().getName(),
                chatComponent.getUnformattedText()
        );
    }

    public void handleChatEvent(ServerChatEvent event) {
        if (!this.isEnabled()) return;

        sendMessage(
                event.getUsername(),
                event.getMessage()
        );
    }

    private void sendMessage(String username, String message) {
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
