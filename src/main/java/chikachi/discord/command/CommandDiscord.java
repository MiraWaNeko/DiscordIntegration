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
import chikachi.discord.core.MinecraftFormattingCodes;
import chikachi.discord.core.config.Configuration;
import chikachi.discord.core.config.linking.LinkingRequest;
import mcp.MethodsReturnNonnullByDefault;
import net.dv8tion.jda.core.entities.User;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.command.CommandTreeBase;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CommandDiscord extends CommandTreeBase {

    public CommandDiscord() {
        this.addSubcommand(new SubCommandConfig());
        this.addSubcommand(new SubCommandOnline());
        this.addSubcommand(new SubCommandTps());
        this.addSubcommand(new SubCommandUnstuck());
        this.addSubcommand(new SubCommandUptime());

        if (Configuration.getConfig().discord.allowLinking) {
            this.addSubcommand(new SubCommandLink());
            this.addSubcommand(new SubCommandUnlink());
        }
    }

    @Override
    public String getName() {
        return "discord";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/discord <config|online" + (Configuration.getConfig().discord.allowLinking ? "|link|unlink" : "") + "|tps|unstuck|uptime> [options]";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(4, getName());
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        int position = args.length;

        if (position == 1) {
            if (!Configuration.getConfig().discord.allowLinking) {
                return getListOfStringsMatchingLastWord(args, "config", /*"connect", "disconnect",*/ "link", "online", "tps", "unstuck", "uptime", "unlink");
            }
            return getListOfStringsMatchingLastWord(args, "config", /*"connect", "disconnect",*/ "link", "online", "tps", "unstuck", "uptime", "unlink");
        } else if (position == 2) {
            if (args[0].equalsIgnoreCase("config")) {
                return getListOfStringsMatchingLastWord(args, "load", "reload", "save");
            } else if (args[0].equalsIgnoreCase("tps")) {
                return getListOfStringsMatchingLastWord(args, "--color");
            } else if (args[0].equalsIgnoreCase("unstuck")) {
                return getListOfStringsMatchingLastWord(args, server.getPlayerList().getOnlinePlayerNames());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return args.length > 1 && args[0].equalsIgnoreCase("unstuck") && index == 1;
    }
}
