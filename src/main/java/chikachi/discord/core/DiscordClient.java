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
import chikachi.discord.core.config.minecraft.MinecraftConfig;
import chikachi.discord.core.config.types.MessageConfig;
import com.google.gson.Gson;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.entities.TextChannel;
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
        CoreLogger.Log("!!! THIS IS AN ALPHA VERSION !!!", true);
        CoreLogger.Log("!!! YOU HAVE BEEN WARNED !!!");
        CoreLogger.Log("Logged in as " + getSelf().getName());

        this.isReady = true;

        MinecraftConfig minecraftConfig = Configuration.getConfig().minecraft;

        DiscordClient.getInstance().broadcast(
            new Message(minecraftConfig.messages.serverStart),
            minecraftConfig.dimensions.generic.relayServerStart.getChannels(
                minecraftConfig.dimensions.generic.discordChannel
            )
        );

        this.isReady = false;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (Configuration.getConfig().discord.ignoresBots && event.getAuthor().isBot()) {
            return;
        }

        if (Configuration.getConfig().discord.isIgnoringUser(event.getAuthor())) {
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

    public void broadcast(MessageConfig message, ArrayList<Long> channels) {
        if (channels == null || channels.size() == 0) return;

        broadcast(new Message(message), channels);
    }

    public void broadcast(Message message, ArrayList<Long> channels) {
        if (channels == null || channels.size() == 0) return;

        for (Long channelId : channels) {
            TextChannel channel = this.jda.getTextChannelById(channelId);
            if (channel != null) {
                if (Configuration.getConfig().discord.channels.channels.containsKey(channelId)) {
                    if (Configuration.getConfig().discord.channels.channels.get(channelId).webhook.trim().length() > 0) {
                        WebhookMessage webhookMessage = message.toWebhook(channel);
                        if (webhookMessage.queue(this.jda, channelId)) {
                            continue;
                        }
                    }
                }

                channel.sendMessage(message.getFormattedText(channel)).queue();
            }
        }
    }
}
