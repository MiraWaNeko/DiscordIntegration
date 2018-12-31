package net.discordintegration.core;

import net.discordintegration.core.bridge.minecraft.IMinecraftMethods;
import net.discordintegration.core.config.Configuration;
import net.discordintegration.core.config.discord.DiscordChannelConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

@SuppressWarnings("unused")
public class DiscordUpdaterThread implements Runnable {
    private HashMap<Long, Integer> channelDescriptionIndexMap = new HashMap<>();
    private HashMap<Long, String> channelLastDescription = new HashMap<>();

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

        IMinecraftMethods minecraftMethods = Proxy.getBridge().getMinecraft();

        long currentPlayerCount = minecraftMethods.getOnlinePlayerCount();

        TextFormatter tf = new TextFormatter()
            .addArgument("PLAYERCOUNT", currentPlayerCount)
            .addArgument("UNIQUEPLAYERCOUNT", minecraftMethods.getUniquePlayerCount())
            .addArgument("MAXPLAYERCOUNT", minecraftMethods.getMaxPlayerCount())
            .addArgument("TICKCOUNT", minecraftMethods.getAverageTickCount())
            .addArgument("TPS", minecraftMethods.getAverageTPS());

        HashMap<Long, DiscordChannelConfig> channels = Configuration.getConfig().discord.channels.channels;
        for (Map.Entry<Long, DiscordChannelConfig> channelEntry : channels.entrySet()) {
            if (currentThread().isInterrupted())
                return;

            DiscordChannelConfig channel = channelEntry.getValue();
            Long channelID = channelEntry.getKey();

            if (channel != null && channel.updateDescription && channel.descriptions.size() > 0) {
                ArrayList<String> descriptions = channel.descriptions;

                int newMessageIndex = channelDescriptionIndexMap.getOrDefault(channelID, -1) + 1;
                if (newMessageIndex >= descriptions.size()) {
                    newMessageIndex = 0;
                }
                channelDescriptionIndexMap.put(channelID, newMessageIndex);

                String actualMessage = tf.format(descriptions.get(newMessageIndex));

                // Only update channel description if it have changed
                String lastDescription = channelLastDescription.get(channelID);
                if (lastDescription != null && lastDescription.equals(actualMessage)) {
                    continue;
                }
                channelLastDescription.put(channelID, actualMessage);

                DiscordClient.getInstance().updateChannelDescription(channelID, actualMessage);
            }
        }
    }

    private void updatePlayerCountInPresence() {
        if (!DiscordClient.getInstance().isConnected())
            return;
        if (!Configuration.getConfig().discord.presence.enabled)
            return;

        DiscordClient.getInstance().setDiscordPresencePlayerCount(Proxy.getBridge().getMinecraft().getOnlinePlayerNames());
    }
}
