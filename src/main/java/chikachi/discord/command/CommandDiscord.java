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
import chikachi.discord.core.config.linking.LinkingRequest;
import mcp.MethodsReturnNonnullByDefault;
import net.dv8tion.jda.core.entities.User;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CommandDiscord extends CommandBase {
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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            return;
        }

        ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
        String commandName = argsList.remove(0).toLowerCase();
        UUID minecraftUUID;

        switch (commandName) {
            case "config":
                SubCommandConfig.execute(sender, argsList);
                break;
            /*case "connect":
                DiscordClient.getInstance().connect(true);
                break;
            case "disconnect":
                DiscordClient.getInstance().disconnect(true);
                break;*/
            case "online":
                SubCommandOnline.execute(sender);
                break;
            case "link":
                if (!Configuration.getConfig().discord.allowLinking) {
                    sender.sendMessage(new TextComponentString("\u00A74Linking is not enabled"));
                    break;
                }

                if (!(sender instanceof EntityPlayer)) {
                    sender.sendMessage(new TextComponentString("\u00A74You need to be a player"));
                    break;
                }

                minecraftUUID = ((EntityPlayer) sender).getGameProfile().getId();
                Long discordId = Configuration.getLinking().getDiscordId(minecraftUUID);

                if (discordId != null) {
                    User discordUser = DiscordClient.getInstance().getJda().getUserById(discordId);
                    sender.sendMessage(
                        new TextComponentString(
                            String.format(
                                "\u00A7eYou're already linked to %s#%s",
                                discordUser.getName(),
                                discordUser.getDiscriminator()
                            )
                        )
                    );
                    break;
                }

                if (argsList.size() == 0) {
                    sender.sendMessage(new TextComponentString("\u00A74Missing code"));
                    break;
                }

                Optional<LinkingRequest> requestOptional = Configuration.getLinking().getRequestByCode(argsList.remove(0));
                if (requestOptional.isPresent()) {
                    LinkingRequest request = requestOptional.get();

                    if (request.hasExpired()) {
                        sender.sendMessage(new TextComponentString("\u00A74Linking request has expired"));
                        break;
                    }

                    request.executeLinking(minecraftUUID);
                    sender.sendMessage(new TextComponentString("\u00A7aLinked"));
                } else {
                    sender.sendMessage(new TextComponentString("\u00A74Linking request not found"));
                    break;
                }
                break;
            case "unlink":
                if (!Configuration.getConfig().discord.allowLinking) {
                    sender.sendMessage(new TextComponentString("\u00A74Linking is not enabled"));
                    break;
                }

                if (!(sender instanceof EntityPlayer)) {
                    sender.sendMessage(new TextComponentString("\u00A74You need to be a player"));
                    break;
                }

                minecraftUUID = ((EntityPlayer) sender).getGameProfile().getId();

                if (Configuration.getLinking().getDiscordId(minecraftUUID) == null) {
                    sender.sendMessage(new TextComponentString("\u00A74You aren't linked"));
                    break;
                }

                Configuration.getLinking().removeLink(minecraftUUID);
                sender.sendMessage(new TextComponentString("\u00A7aUnlinked"));
                break;
            case "tps":
                SubCommandTps.execute(sender, argsList);
                break;
            case "unstuck":
                SubCommandUnstuck.execute(sender, argsList);
                break;
            case "uptime":
                SubCommandUptime.execute(sender);
                break;
            default:
                sender.sendMessage(new TextComponentString("Unknown command"));
                break;
        }
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

    @Override
    public int compareTo(@NotNull ICommand o) {
        return super.compareTo(o);
    }
}
