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
import com.google.common.base.Joiner;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

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
        boolean isDiscord = sender instanceof DiscordCommandSender;

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
                        isDiscord ? "Currently 1 player online: `%s`" : "Currently 1 player online: %s",
                        playerNames.get(0)
                    )
                )
            );
            return;
        }

        sender.sendMessage(
            new TextComponentString(
                String.format(
                    isDiscord ? "Currently %d players online:\n`%s`" : "Currently %d players online:\n%s",
                    playersOnline,
                    Joiner.on(isDiscord ? "`, `" : ", ").join(playerNames)
                )
            )
        );
    }
}
