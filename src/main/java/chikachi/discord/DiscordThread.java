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

package chikachi.discord;

import chikachi.discord.core.DiscordClient;
import chikachi.discord.core.DiscordIntegrationLogger;
import chikachi.discord.core.TextFormatter;
import chikachi.discord.core.config.Configuration;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class DiscordThread implements Runnable {
    private HashMap<Long, Integer> channelDescriptionIndexMap;

    @Override
    public void run() {
        DiscordIntegrationLogger.Log("Started update thread");
        while (!Thread.interrupted()) {
            updatePlayerCountInPresence();
            updateChannelDescriptions();

            try {
                sleep(1000 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        DiscordIntegrationLogger.Log("Stopped update thread");
    }

    private void updateChannelDescriptions() {
        if (!DiscordClient.getInstance().isConnected())
            return;

        TextFormatter tf = new TextFormatter()
            .addArgument("PLAYERCOUNT", MinecraftInformationHandler.getOnlineRealPlayerCount())
            .addArgument("TPS", MinecraftInformationHandler.getAverageTickCount())
            .addArgument("TICKCOUNT", MinecraftInformationHandler.getAverageTPS());

        Configuration.getConfig().discord.channels.channels.
            entrySet().stream()
            .filter(channel -> channel.getValue().updateDescription)
            .filter(channel -> channel.getValue().descriptions.size() > 0)
            .forEach(channel -> {
                ArrayList<String> descriptions = channel.getValue().descriptions;

                int newMessageIndex = channelDescriptionIndexMap.getOrDefault(channel.getKey(), -1) + 1;
                if (newMessageIndex >= descriptions.size())
                    newMessageIndex = 0;
                channelDescriptionIndexMap.put(channel.getKey(), newMessageIndex);

                String actualMessage = tf.format(descriptions.get(newMessageIndex));

                DiscordClient.getInstance().updateChannelDescription(channel.getKey(), actualMessage);
            });
    }

    private void updatePlayerCountInPresence() {
        if (!DiscordClient.getInstance().isConnected())
            return;
        if (!Configuration.getConfig().discord.presence.enabled)
            return;

        DiscordClient.getInstance().setDiscordPresencePlayerCount(MinecraftInformationHandler.getOnlineRealPlayerNames());
    }
}
