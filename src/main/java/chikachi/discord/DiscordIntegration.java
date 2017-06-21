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

package chikachi.discord;

import chikachi.discord.command.CommandDiscord;
import chikachi.discord.core.CoreConstants;
import chikachi.discord.core.CoreProxy;
import chikachi.discord.core.DiscordClient;
import chikachi.discord.listener.DiscordListener;
import chikachi.discord.listener.MinecraftListener;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;

@Mod(modid = CoreConstants.MODID, name = CoreConstants.MODNAME, version = CoreConstants.VERSION, serverSideOnly = true, acceptableRemoteVersions = "*")
public class DiscordIntegration {
    @Mod.Instance
    public static DiscordIntegration instance;
    static MinecraftServer minecraftServer;

    private static CoreProxy coreProxy = new CoreProxy();

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        coreProxy.onPreInit(event.getModConfigurationDirectory());

        MinecraftForge.EVENT_BUS.register(new MinecraftListener());
    }

    @Mod.EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        minecraftServer = event.getServer();
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        coreProxy.onServerStarting();
        DiscordClient.getInstance().addEventListner(new DiscordListener());

        event.registerServerCommand(new CommandDiscord());
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        coreProxy.onServerStarted();
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        coreProxy.onServerStopping();
    }

    @Mod.EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        coreProxy.onServerStopped();
    }
}
