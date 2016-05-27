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

package chikachi.discord.experimental;

import chikachi.discord.DiscordClient;
import chikachi.discord.config.Configuration;
import net.dv8tion.jda.OnlineStatus;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraftforge.common.util.FakePlayer;

import java.util.HashMap;
import java.util.List;

public class ExperimentalDiscordListener extends ListenerAdapter {
    private HashMap<String, FakePlayer> fakePlayers = new HashMap<>();

    private void userOnline(User user) {
        if (user == null) return;
        if (user.getOnlineStatus() == OnlineStatus.OFFLINE) return;
        if (!Configuration.isExperimentalFakePlayersEnabled()) return;

        DiscordFakePlayer discordFakePlayer = new DiscordFakePlayer(user);

        ServerConfigurationManager configurationManager = MinecraftServer.getServer().getConfigurationManager();

        configurationManager.playerEntityList.add(discordFakePlayer);

        fakePlayers.put(user.getUsername(), discordFakePlayer);
    }

    private void userOffline(User user) {
        if (user == null) return;
        if (user.getOnlineStatus() != OnlineStatus.OFFLINE) return;
        if (!Configuration.isExperimentalFakePlayersEnabled()) return;

        if (fakePlayers.containsKey(user.getUsername())) {
            FakePlayer fakePlayer = fakePlayers.get(user.getUsername());
            if (fakePlayer != null) {
                MinecraftServer.getServer().getConfigurationManager().playerEntityList.remove(fakePlayer);
            }
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        DiscordClient client = DiscordClient.getInstance();

        TextChannel channel = client.getChannel();
        if (channel == null) {
            return;
        }

        if (Configuration.isExperimentalFakePlayersEnabled()) {
            List<User> users = channel.getUsers();
            users.forEach(this::userOnline);
        }
    }

    @Override
    public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {
        if (!Configuration.isExperimentalFakePlayersEnabled()) return;

        User user = event.getUser();
        OnlineStatus before = event.getPreviousOnlineStatus();
        OnlineStatus now = user.getOnlineStatus();

        if (before == OnlineStatus.OFFLINE && now != OnlineStatus.OFFLINE) {
            userOnline(user);
        } else if (before != OnlineStatus.OFFLINE && now == OnlineStatus.OFFLINE) {
            userOffline(user);
        }
    }
}
