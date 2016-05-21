package chikachi.discord;

import chikachi.discord.command.NonLibCommandHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mod(modid = Constants.MODID, name = Constants.MODNAME, version = Constants.VERSION, serverSideOnly = true, acceptableRemoteVersions = "*")
public class ChikachiDiscord {
    private static Proxy proxy = new Proxy();

    private static final Logger logger = LogManager.getLogger(Constants.MODID);

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        proxy.onPreInit(event);
    }

    @Mod.EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        proxy.onServerAboutToStart(event);
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        proxy.onServerStarting(event);

        if (Loader.isModLoaded("ChikachiLib")) {
            Log("Trying to hook into ChikachiLib", false);
            try {
                Class subCommandHandlerClass = Class.forName("chikachi.discord.command.LibCommandHandler");
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
        proxy.onServerStarted(event);
    }

    @Mod.EventHandler
    public void onServerShutdown(FMLServerStoppingEvent event) {
        proxy.onServerShutdown(event);
    }

    @SuppressWarnings("WeakerAccess")
    public static void Log(String message) {
        Log(message, false);
    }

    public static void Log(String message, boolean warning) {
        logger.log(warning ? Level.WARN : Level.INFO, String.format("[%s] %s", Constants.VERSION, message));
    }
}
