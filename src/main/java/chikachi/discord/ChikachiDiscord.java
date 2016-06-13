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

import chikachi.discord.command.mc.NonLibCommandHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mod(
        modid = Constants.MODID,
        name = Constants.MODNAME,
        version = Constants.VERSION,
        serverSideOnly = true,
        dependencies = "after:ChikachiLib",
        acceptableRemoteVersions = "*"
)
public class ChikachiDiscord {
    @Mod.Instance
    public static ChikachiDiscord instance;

    private static Proxy proxy = new Proxy();

    private static final Logger logger = LogManager.getLogger(Constants.MODID);

    private static boolean receivedStoppingEvent = false;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        proxy.onPreInit(event);
    }

    @Mod.EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        proxy.onServerAboutToStart();
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        proxy.onServerStarting();

        if (Loader.isModLoaded("ChikachiLib")) {
            Log("Trying to hook into ChikachiLib", false);
            try {
                Class subCommandHandlerClass = Class.forName("chikachi.discord.command.mc.LibCommandHandler");
                Class libClass = Class.forName("chikachi.lib.ChikachiLib");
                Class commandClass = Class.forName("chikachi.lib.common.command.sub.CommandChikachiBase");
                if (subCommandHandlerClass != null && libClass != null && commandClass != null) {
                    Field commandHandlerField = libClass.getField("commandHandler");
                    if (commandHandlerField != null) {
                        Object obj = commandHandlerField.get(null);
                        if (obj != null) {
                            Method registerCommandHandlerMethod = obj.getClass().getMethod("RegisterSubCommandHandler", commandClass);
                            if (registerCommandHandlerMethod != null) {
                                registerCommandHandlerMethod.invoke(obj, subCommandHandlerClass.newInstance());
                                Log("Hooked into ChikachiLib", false);
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                Log("Failed to hook into ChikachiLib", false);
                event.registerServerCommand(new NonLibCommandHandler());
            }
        } else {
            event.registerServerCommand(new NonLibCommandHandler());
        }
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        proxy.onServerStarted();
    }

    @Mod.EventHandler
    public void onServerShutdown(FMLServerStoppingEvent event) {
        proxy.onServerShutdown();
        receivedStoppingEvent = true;
    }

    @Mod.EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        if (!receivedStoppingEvent) {
            proxy.onServerCrash();
        }

        DiscordClient.getInstance().disconnect();
    }

    @Mod.EventHandler
    public void imcReceived(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage imcMessage : event.getMessages()) {
            IMCHandler.onMessageReceived(imcMessage);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static void Log(String message) {
        Log(message, false);
    }

    public static void Log(String message, boolean warning) {
        logger.log(warning ? Level.WARN : Level.INFO, String.format("[%s] %s", Constants.VERSION, message));
    }
}
