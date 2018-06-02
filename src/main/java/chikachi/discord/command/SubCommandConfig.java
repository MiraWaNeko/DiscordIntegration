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

package chikachi.discord.command;

import chikachi.discord.core.DiscordClient;
import chikachi.discord.core.config.Configuration;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SubCommandConfig extends CommandBase {

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return "/discord config <reload|save|clean>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] strings) throws CommandException {

        if (strings.length == 0) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        String arg = strings[0];
        switch (arg) {
            case "load":
            case "reload":
                String oldToken = Configuration.getConfig().discord.token;

                Configuration.loadConfig();
                sender.sendMessage(new TextComponentString("Config reloaded"));

                if (!DiscordClient.getInstance().isConnected()) {
                    // Connect to Discord, if not already connected
                    DiscordClient.getInstance().connect();
                    sender.sendMessage(new TextComponentString("Connected Bot"));
                } else if (!oldToken.equals(Configuration.getConfig().discord.token)) {
                    // Connect with the new token
                    DiscordClient.getInstance().disconnect();
                    DiscordClient.getInstance().connect();
                    sender.sendMessage(new TextComponentString("Reconnected Bot"));
                }

                break;
            case "save":
                Configuration.saveConfig();

                sender.sendMessage(new TextComponentString("Config saved"));
                break;
            case "clean":
                Configuration.saveClean();

                sender.sendMessage(new TextComponentString("Clean config saved"));
                break;
            default:
                sender.sendMessage(new TextComponentString(getUsage(sender)));
                break;
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(4, getName());
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return getListOfStringsMatchingLastWord(args, "reload", "save", "clean");
    }
}
