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
import chikachi.discord.config.listener.MinecraftListener;
import chikachi.discord.config.message.GenericMessageConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

class Proxy {
    void onPreInit(FMLPreInitializationEvent event) {
        Configuration.onPreInit(event);

        MinecraftForge.EVENT_BUS.register(new MinecraftListener());
    }

    void onServerAboutToStart() {
        DiscordClient.getInstance().connect();
    }

    void onServerStarting() {
    }

    void onServerStarted() {
        GenericMessageConfig setting = Configuration.getDiscordStartup();

        setting.sendMessage();
    }

    void onServerShutdown() {
        GenericMessageConfig setting = Configuration.getDiscordShutdown();

        setting.sendMessage();

        DiscordClient.getInstance().disconnect();
    }

    void onServerCrash() {
        GenericMessageConfig setting = Configuration.getDiscordCrash();

        setting.sendMessage();

        DiscordClient.getInstance().disconnect();
    }
}
