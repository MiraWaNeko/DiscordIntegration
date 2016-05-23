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

package chikachi.discord.command;

import chikachi.discord.DiscordClient;
import chikachi.discord.config.Configuration;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;

class CommandProcessor {
    static void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "reload":
                    boolean shouldTryConnect = Configuration.getToken().length() == 0;

                    Configuration.load();
                    sender.addChatMessage(new TextComponentString("Config reloaded"));

                    if (shouldTryConnect && Configuration.getToken().length() > 0) {
                        DiscordClient.getInstance().connect(sender.getServer());
                    }
                    return;
            }
        }

        sender.addChatMessage(new TextComponentString("Unknown command - Available commands: reload"));
    }
}
