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

import java.io.File;
import java.util.Date;

public class Proxy {
    private static boolean preInit = false;
    private static boolean serverStopping = false;
    private static long started;

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
        if (hours < 60) {
            return hours + " hour" + (hours == 1 ? "" : "s") + ", " + minutes + " minute" + (minutes == 1 ? "" : "s") + ", " + seconds + " second" + (seconds == 1 ? "" : "s");
        }
        int days = Math.floorDiv(hours, 24);
        hours -= days * 60;
        return days + " day" + (days == 1 ? "" : "s") + ", " + hours + " hour" + (hours == 1 ? "" : "s") + ", " + minutes + " minute" + (minutes == 1 ? "" : "s") + ", " + seconds + " second" + (seconds == 1 ? "" : "s");
    }

    public void onPreInit(File configurationPath) {
        if (preInit) {
            return;
        }

        Configuration.onPreInit(configurationPath.getAbsolutePath() + File.separator + "Chikachi");

        preInit = true;
    }

    public void onServerStarting() {
        DiscordClient.getInstance().connect();
        started = new Date().getTime();
    }

    public void onServerStarted() {

    }

    public void onServerStopping() {
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

        serverStopping = true;
    }

    public void onServerStopped() {
        if (!serverStopping) {
            MinecraftConfig minecraftConfig = Configuration.getConfig().minecraft;

            DiscordClient.getInstance().broadcast(
                minecraftConfig.dimensions.generic.messages.serverCrash,
                minecraftConfig.dimensions.generic.relayServerCrash.getChannels(
                    minecraftConfig.dimensions.generic.discordChannel
                )
            );
        }

        DiscordClient.getInstance().disconnect(true);
    }
}
