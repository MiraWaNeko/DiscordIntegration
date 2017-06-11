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

package chikachi.discord.core;

import chikachi.discord.core.config.Configuration;
import chikachi.discord.core.config.MessageConfig;
import chikachi.discord.core.config.minecraft.MinecraftConfig;
import com.google.gson.Gson;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class DiscordClient extends ListenerAdapter {
    private static DiscordClient instance;
    private boolean isReady = false;
    private JDA jda;

    private DiscordClient() {
    }

    public static DiscordClient getInstance() {
        if (instance == null) {
            instance = new DiscordClient();
        }

        return instance;
    }

    @Override
    public void onReady(ReadyEvent event) {
        CoreLogger.Log("Logged in as " + getSelf().getName());
        this.isReady = true;

        MinecraftConfig minecraftConfig = Configuration.getConfig().minecraft;

        DiscordClient.getInstance().broadcast(
            new Message(
                new MessageConfig(minecraftConfig.messages.serverStart, minecraftConfig.messages.serverStart)
            ),
            minecraftConfig.dimensions.generic.relayServerStart.getChannels(
                minecraftConfig.dimensions.generic.discordChannel
            )
        );

        this.isReady = false;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (Configuration.getConfig().discord.isIgnoringBots() && event.getAuthor().isBot()) {
            return;
        }
    }

    void connect() {
        if (this.jda != null) {
            CoreLogger.Log("Is already connected", true);
            return;
        }

        String token = Configuration.getConfig().discord.token;

        if (token == null || token.isEmpty()) {
            CoreLogger.Log("Missing token", true);
            return;
        }

        try {
            this.jda = new JDABuilder(AccountType.BOT)
                .setToken(token)
                .setAudioEnabled(false)
                .setBulkDeleteSplittingEnabled(false)
                .addEventListener(this)
                .buildAsync();
        } catch (Exception e) {
            CoreLogger.Log("Failed to connect to Discord", true);
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return this.jda != null && (this.isReady || this.jda.getStatus() == JDA.Status.CONNECTED);
    }

    void disconnect() {
        disconnect(false);
    }

    void disconnect(boolean noMessage) {
        if (this.jda == null) {
            if (!noMessage) {
                CoreLogger.Log("Is already disconnected", true);
            }
            return;
        }

        this.jda.shutdown();
        if (!noMessage) {
            CoreLogger.Log("Disconnected from Discord", true);
        }
        this.jda = null;
    }

    public SelfUser getSelf() {
        if (this.jda == null) {
            return null;
        }

        return this.jda.getSelfUser();
    }

    public void broadcast(String message, int dimensionId) {

    }

    public void broadcast(String message, ArrayList<Long> channels) {
        if (channels == null || channels.size() == 0) return;

        for (Long channelId : channels) {
            if (Configuration.getConfig().discord.channels.channels.containsKey(channelId)) {
                if (Configuration.getConfig().discord.channels.channels.get(channelId).webhook.trim().length() > 0) {
                    try {
                        URL url = new URL(Configuration.getConfig().discord.channels.channels.get(channelId).webhook.trim());
                        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("User-Agent", CoreConstants.MODNAME + " " + CoreConstants.VERSION);
                        connection.setDoOutput(true);

                        DataOutputStream writer = new DataOutputStream(connection.getOutputStream());

                        WebhookMessage webhookMessage = new WebhookMessage();
                        webhookMessage.content = message;

                        Gson gson = new Gson();
                        writer.writeBytes(gson.toJson(webhookMessage));
                        writer.flush();
                        writer.close();

                        int responseCode = connection.getResponseCode();
                        if (responseCode < 200 || 299 < responseCode) {
                            throw new Exception("Error sending webhook");
                        }
                        continue;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        CoreLogger.Log(e.getMessage());
                    }
                }
            }

            MessageChannel channel = this.jda.getTextChannelById(channelId);
            if (channel != null) {
                channel.sendMessage(message).queue();
            }
        }
    }

    public void broadcast(MessageConfig message, ArrayList<Long> channels) {
        if (channels == null || channels.size() == 0) return;

        for (Long channelId : channels) {
            if (Configuration.getConfig().discord.channels.channels.containsKey(channelId)) {
                if (Configuration.getConfig().discord.channels.channels.get(channelId).webhook.trim().length() > 0) {
                    WebhookMessage webhookMessage = new WebhookMessage();
                    webhookMessage.content = message.webhook;
                    if (webhookMessage.send(channelId)) {
                        continue;
                    }
                }
            }

            MessageChannel channel = this.jda.getTextChannelById(channelId);
            if (channel != null) {
                channel.sendMessage(message.normal).queue();
            }
        }
    }

    public void broadcast(Message message, ArrayList<Long> channels) {
        if (channels == null || channels.size() == 0) return;

        for (Long channelId : channels) {
            if (Configuration.getConfig().discord.channels.channels.containsKey(channelId)) {
                if (Configuration.getConfig().discord.channels.channels.get(channelId).webhook.trim().length() > 0) {
                    WebhookMessage webhookMessage = message.toWebhook();
                    if (webhookMessage.send(channelId)) {
                        continue;
                    }
                }
            }

            MessageChannel channel = this.jda.getTextChannelById(channelId);
            if (channel != null) {
                channel.sendMessage(message.getFormattedText()).queue();
            }
        }
    }
}
