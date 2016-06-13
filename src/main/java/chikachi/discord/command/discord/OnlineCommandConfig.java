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

package chikachi.discord.command.discord;

import chikachi.discord.DiscordClient;
import com.google.common.base.Joiner;
import net.dv8tion.jda.entities.User;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class OnlineCommandConfig extends CommandConfig {
    public OnlineCommandConfig() {
        super("online");
    }

    @Override
    public void execute(User user, List<String> args) {
        List<String> playerNames = new ArrayList<>();

        String[] players = MinecraftServer.getServer().getConfigurationManager().getAllUsernames();

        for (String player : players) {
            if (player.startsWith("@")) {
                continue;
            }
            playerNames.add(player);
        }

        int playersOnline = playerNames.size();
        if (playersOnline == 0) {
            DiscordClient.getInstance().sendMessage("No players online");
            return;
        }

        if (playersOnline == 1) {
            DiscordClient.getInstance().sendMessage(
                    String.format(
                            "Currently 1 player online: `%s`",
                            Joiner.on(", ").join(playerNames)
                    )
            );
            return;
        }

        DiscordClient.getInstance().sendMessage(
                String.format(
                        "Currently %d players online:\n`%s`",
                        playersOnline,
                        Joiner.on("`, `").join(playerNames)
                )
        );
    }
}
