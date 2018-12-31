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

package net.discordintegration.core;

import net.discordintegration.core.bridge.Bridge;
import net.discordintegration.core.config.Configuration;
import net.discordintegration.core.config.minecraft.MinecraftConfig;

import java.io.File;
import java.util.Date;

@SuppressWarnings("unused")
public class Proxy {
    private static boolean preInit = false;
    private static boolean serverStopping = false;
    private static long started;
    private static Bridge bridge = new Bridge();

    public static Bridge getBridge() {
        return bridge;
    }

    public static String getUptime() {
        if (started == 0) {
            return "UNKNOWN";
        }

        long diff = new Date().getTime() - started;

        int seconds = (int) Math.floorDiv(diff, 1000);
        if (seconds < 60) {
            return seconds + " second" + (seconds == 1 ? "" : "s");
        }
        int minutes = Math.floorDiv(seconds, 60);
        seconds -= minutes * 60;
        if (minutes < 60) {
            return minutes + " minute" + (minutes == 1 ? "" : "s") + ", " + seconds + " second" + (seconds == 1 ? "" : "s");
        }
        int hours = Math.floorDiv(minutes, 60);
        minutes -= hours * 60;
        if (hours < 24) {
            return hours + " hour" + (hours == 1 ? "" : "s") + ", " + minutes + " minute" + (minutes == 1 ? "" : "s") + ", " + seconds + " second" + (seconds == 1 ? "" : "s");
        }
        int days = Math.floorDiv(hours, 24);
        hours -= days * 24;
        return days + " day" + (days == 1 ? "" : "s") + ", " + hours + " hour" + (hours == 1 ? "" : "s") + ", " + minutes + " minute" + (minutes == 1 ? "" : "s") + ", " + seconds + " second" + (seconds == 1 ? "" : "s");
    }

    public static void onPreInit(File configurationPath) {
        if (preInit) {
            return;
        }

        Configuration.onPreInit(configurationPath.getAbsolutePath() + File.separator + "DiscordIntegration");
        Configuration.validateConfig();

        preInit = true;
    }

    public static void onServerStarting() {
        DiscordClient.getInstance().connect();
        started = new Date().getTime();
    }

    public static void onServerStarted() {

    }

    public static void onServerStopping() {
        if (serverStopping) {
            return;
        }

        MinecraftConfig minecraftConfig = Configuration.getConfig().minecraft;

        DiscordClient.getInstance().broadcast(
            minecraftConfig.dimensions.generic.messages.serverStop,
            minecraftConfig.dimensions.generic.relayServerStop.getChannels(
                minecraftConfig.dimensions.generic.discordChannel
            )
        );

        // Also set all (available) channel descriptions to "Server is offline.".
        Configuration.getConfig().discord.channels.channels.
            entrySet().stream()
            .filter(channel -> channel.getValue().updateDescription)
            .filter(channel -> channel.getValue().descriptions.size() > 0)
            .forEach(channel -> DiscordClient.getInstance().updateChannelDescription(channel.getKey(), "Server is offline."));

        serverStopping = true;
    }

    public static void onServerStopped() {
        if (!serverStopping) {
            MinecraftConfig minecraftConfig = Configuration.getConfig().minecraft;

            DiscordClient.getInstance().broadcast(
                minecraftConfig.dimensions.generic.messages.serverCrash,
                minecraftConfig.dimensions.generic.relayServerCrash.getChannels(
                    minecraftConfig.dimensions.generic.discordChannel
                )
            );

            // Also set all (available) channel descriptions to "Server crashed.".
            Configuration.getConfig().discord.channels.channels.
                entrySet().stream()
                .filter(channel -> channel.getValue().updateDescription)
                .filter(channel -> channel.getValue().descriptions.size() > 0)
                .forEach(channel -> DiscordClient.getInstance().updateChannelDescription(channel.getKey(), "Server crashed."));
        }

        DiscordClient.getInstance().disconnect(true);
    }
}
