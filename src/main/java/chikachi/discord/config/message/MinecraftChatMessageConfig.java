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
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;

public class MinecraftChatMessageConfig extends BaseMessageConfig {
    public MinecraftChatMessageConfig(boolean enabled, String message) {
        super("chat", enabled, message);
    }

    public void handleCommandEvent(CommandEvent event) {
        if (!this.isEnabled()) {
            return;
        }

        String message = Joiner.on(" ").join(event.getParameters());
        message = message.replaceAll("§.", "");

        TextComponentString chatComponent = new TextComponentString(message);

        DiscordClient.getInstance().sendMessage(
                this.getMessage()
                        .replace("%USER%", event.getSender().getName())
                        .replace("%MESSAGE%", chatComponent.getUnformattedText())
        );
    }

    public void handleChatEvent(ServerChatEvent event) {
        if (!this.isEnabled()) return;

        String message = event.getMessage();
        message = message.replaceAll("§.", "");

        DiscordClient.getInstance().sendMessage(
                this.getMessage()
                        .replace("%USER%", event.getUsername())
                        .replace("%MESSAGE%", message)
        );
    }
}
