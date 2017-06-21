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

import chikachi.discord.DiscordCommandSender;
import chikachi.discord.core.DiscordClient;
import chikachi.discord.core.config.Configuration;
import com.google.common.base.Joiner;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CommandDiscord extends CommandBase {
    @Override
    public String getName() {
        return "discord";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "";
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

        switch (args[0].toLowerCase()) {
            case "online":
                List<String> playerNames = new ArrayList<>();

                List<EntityPlayerMP> players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();

                for (EntityPlayerMP player : players) {
                    String playerName = player.getDisplayNameString();
                    if (playerName.startsWith("@")) {
                        continue;
                    }
                    playerNames.add(playerName);
                }

                int playersOnline = playerNames.size();
                if (playersOnline == 0) {
                    sender.sendMessage(
                        new TextComponentString("No players online")
                    );
                    return;
                }

                if (playersOnline == 1) {
                    sender.sendMessage(
                        new TextComponentString(
                            String.format(
                                "Currently 1 player online: `%s`",
                                Joiner.on(", ").join(playerNames)
                            )
                        )
                    );
                    return;
                }

                sender.sendMessage(
                    new TextComponentString(
                        String.format(
                            "Currently %d players online:\n`%s`",
                            playersOnline,
                            Joiner.on("`, `").join(playerNames)
                        )
                    )
                );
                break;
            case "tps":
                SubCommandTps.execute(sender);
                break;
            case "config":
                switch (args[1].toLowerCase()) {
                    case "load":
                    case "reload":
                        Configuration.load();
                        break;
                    case "save":
                        Configuration.save();
                        break;
                    default:
                        sender.sendMessage(new TextComponentString("Unknown command"));
                        break;
                }
                break;
            case "unstuck":
                break;
            default:
                sender.sendMessage(new TextComponentString("Unknown command"));
                break;
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(3, getName());
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        int position = args.length;

        if (position == 1) {
            return getListOfStringsMatchingLastWord(args, "online", "tps", "config", "unstuck");
        } else if (position == 2) {
            if (args[0].equalsIgnoreCase("config")) {
                return getListOfStringsMatchingLastWord(args, "load", "reload", "save");
            }
        }

        return new ArrayList<>();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(@NotNull ICommand o) {
        return 0;
    }
}
