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

package chikachi.discord;

import chikachi.discord.config.Configuration;
import chikachi.discord.config.experimental.ExperimentalDiscordListener;
import chikachi.discord.config.listener.DiscordListener;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.minecraft.server.MinecraftServer;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class DiscordClient {
    private static DiscordClient instance;
    private JDA jda;
    private TextChannel channel;
    public List<String> queue = new ArrayList<>();

    public static DiscordClient getInstance() {
        if (instance == null) {
            instance = new DiscordClient();
        }

        return instance;
    }

    private DiscordClient() {

    }

    public void connect(MinecraftServer minecraftServer) {
        if (this.jda != null) {
            ChikachiDiscord.Log("Is already connected", true);
            return;
        }

        String token = Configuration.getToken();

        if (token.isEmpty()) {
            ChikachiDiscord.Log("Missing token", true);
            return;
        }

        try {
            this.jda = new JDABuilder()
                    .setBotToken(token)
                    .addListener(new DiscordListener(minecraftServer))
                    .addListener(new ExperimentalDiscordListener(minecraftServer))
                    .buildAsync();
        } catch (LoginException e) {
            ChikachiDiscord.Log("Failed to connect to Discord", true);
            e.printStackTrace();
        }
    }

    void disconnect() {
        if (this.jda == null) {
            ChikachiDiscord.Log("Is already disconnected", true);
            return;
        }

        this.jda.shutdown();
        this.jda = null;
    }

    public TextChannel getChannel() {
        if (this.channel == null) {
            this.channel = this.jda.getTextChannelById(Configuration.getChannel());
            if (this.channel == null) {
                ChikachiDiscord.Log("Failed to find channel", true);
                return null;
            }
        }

        return this.channel;
    }

    public boolean sendMessage(String message) {
        if (this.jda == null) {
            this.queue.add(message);
            return true;
        }

        TextChannel channel = getChannel();
        if (channel == null) {
            this.queue.add(message);
            return false;
        }

        if (message.contains("@")) {
            message = " " + message + " ";

            List<User> users = new ArrayList<>(this.channel.getGuild().getUsers());
            users.sort((o1, o2) -> o2.getUsername().length() - o1.getUsername().length());

            for (User user : users) {
                if (message.toLowerCase().contains("@" + user.getUsername().toLowerCase())) {
                    message = message.replaceAll("(?i)@" + user.getUsername() + "(\\W)", user.getAsMention() + "$1");
                }
            }

            message = message.trim();
        }

        this.channel.sendMessageAsync(message, sentMessage -> {
        });
        return true;
    }
}
