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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

import static java.lang.Thread.sleep;

public class DiscordThread implements Runnable {

    @Override
    public void run() {
        DiscordIntegrationLogger.Log("Started update thread");
        while (!Thread.interrupted()) {
            updatePlayerCountInPresence();

            try {
                sleep(1000 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        DiscordIntegrationLogger.Log("Stopped update thread");
    }

    private void updatePlayerCountInPresence() {
        if (!DiscordClient.getInstance().isConnected())
            return;
        if (!Configuration.getConfig().discord.presence.enabled)
            return;

        Object[] players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()
            .stream().filter(player -> !player.getDisplayNameString().startsWith("@")).toArray();
        long count = players.length;
        String message;

        if (count == 0) {
            message = Configuration.getConfig().discord.presence.messages.noPlayerOnline;
        } else if (count == 1) {
            Object player = players[0];
            String name = "unknown";
            if (player instanceof EntityPlayerMP) {
                name = ((EntityPlayerMP) player).getDisplayNameString();
            }
            message = new TextFormatter()
                .addArgument("USER", name)
                .addArgument("COUNT", "1")
                .format(Configuration.getConfig().discord.presence.messages.onePlayerOnline);

        } else {
            message = new TextFormatter()
                .addArgument("COUNT", String.format("%d", count))
                .format(Configuration.getConfig().discord.presence.messages.onePlayerOnline);
        }
        DiscordClient.getInstance().setDiscordPresencePlaying(message);
    }
}
