/*
 * Copyright (C) 2018 Chikachi and other contributors
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

package net.discordintegration.command;

import com.google.common.base.Joiner;
import mcp.MethodsReturnNonnullByDefault;
import net.discordintegration.core.Proxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class SubCommandOnline extends CommandBase {
    @Override
    public String getName() {
        return "online";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return "/discord online";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] strings) throws CommandException {
        boolean isDiscord = false;//sender instanceof DiscordCommandSender;

        String[] playerNames = Proxy.getBridge().getMinecraft().getOnlinePlayerNames();
        int playersOnline = playerNames.length;

        String message = "No players online";

        if (playersOnline == 1) {
            message = String.format(
                isDiscord ? "Currently 1 player online: `%s`" : "Currently 1 player online: %s",
                playerNames[0]
            );
        } else if (playersOnline > 1) {
            message = String.format(
                isDiscord ? "Currently %d players online:\n`%s`" : "Currently %d players online:\n%s",
                playersOnline,
                Joiner.on(isDiscord ? "`, `" : ", ").join(playerNames)
            );
        }

        sender.sendMessage(new TextComponentString(message));
    }
}
