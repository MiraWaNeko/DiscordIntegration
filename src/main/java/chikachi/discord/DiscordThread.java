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
import chikachi.discord.core.config.discord.DiscordChannelConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

public class DiscordThread implements Runnable {
    private HashMap<Long, Integer> channelDescriptionIndexMap = new HashMap<>();

    @Override
    public void run() {
        DiscordIntegrationLogger.Log("Started update thread");
        while (!Thread.interrupted()) {
            updatePlayerCountInPresence();
            updateChannelDescriptions();

            try {
                sleep(1000 * 60);
            } catch (InterruptedException e) {
                // Ignore this as we now stop the thread
            }
        }
        DiscordIntegrationLogger.Log("Stopped update thread");
    }

    private void updateChannelDescriptions() {
        if (!DiscordClient.getInstance().isConnected())
            return;

        TextFormatter tf = new TextFormatter()
            .addArgument("PLAYERCOUNT", MinecraftInformationHandler.getOnlineRealPlayerCount())
            .addArgument("UNIQUEPLAYERCOUNT", MinecraftInformationHandler.getUniquePlayerCount())
            .addArgument("MAXPLAYERCOUNT", MinecraftInformationHandler.getMaxPlayerCount())
            .addArgument("TPS", MinecraftInformationHandler.getAverageTickCount())
            .addArgument("TICKCOUNT", MinecraftInformationHandler.getAverageTPS());

        HashMap<Long, DiscordChannelConfig> channels = Configuration.getConfig().discord.channels.channels;
        for (Map.Entry<Long, DiscordChannelConfig> channelEntry : channels.entrySet()) {
            DiscordChannelConfig channel = channelEntry.getValue();
            Long channelID = channelEntry.getKey();

            if (channel != null && channel.updateDescription && channel.descriptions.size() > 0) {
                ArrayList<String> descriptions = channel.descriptions;

                int newMessageIndex = channelDescriptionIndexMap.getOrDefault(channelID, -1) + 1;
                if (newMessageIndex >= descriptions.size())
                    newMessageIndex = 0;
                channelDescriptionIndexMap.put(channelID, newMessageIndex);

                String actualMessage = tf.format(descriptions.get(newMessageIndex));

                DiscordClient.getInstance().updateChannelDescription(channelID, actualMessage);
            }
        }
    }

    private void updatePlayerCountInPresence() {
        if (!DiscordClient.getInstance().isConnected())
            return;
        if (!Configuration.getConfig().discord.presence.enabled)
            return;

        DiscordClient.getInstance().setDiscordPresencePlayerCount(MinecraftInformationHandler.getOnlineRealPlayerNames());
    }
}
