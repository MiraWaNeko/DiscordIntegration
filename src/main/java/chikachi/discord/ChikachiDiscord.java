package chikachi.discord;

import chikachi.discord.command.ChikachiDiscordCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("WeakerAccess")
@Mod(modid = Constants.MODID, name = Constants.MODNAME, version = Constants.VERSION, serverSideOnly = true, acceptableRemoteVersions = "*")
public class ChikachiDiscord {
    private static Proxy proxy = new Proxy();

    private static final Logger logger = LogManager.getLogger(Constants.MODID);

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        proxy.onPreInit(event);
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        proxy.onServerAboutToStart(event);
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        proxy.onServerStarting(event);

        if (Loader.isModLoaded("ChikachiLib")) {
            Log("Trying to hook into ChikachiLib");
            try {
                Class subCommandHandlerClass = Class.forName("chikachi.discord.command.ChikachiLibIntegration");
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
                                Log("Hooked into ChikachiLib");
                                return;
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                Log("Failed to hook into ChikachiLib");
            }
        }

        event.registerServerCommand(new ChikachiDiscordCommand());
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        proxy.onServerStarted(event);
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void onServerShutdown(FMLServerStoppingEvent event) {
        proxy.onServerShutdown(event);
    }

    public static void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "reload":
                    Configuration.load();
                    DiscordClient.getInstance().disconnect();
                    DiscordClient.getInstance().connect();
                    sender.addChatMessage(new ChatComponentText("Config reloaded"));
                    return;
                case "start":
                    DiscordClient.getInstance().connect();
                    sender.addChatMessage(new ChatComponentText("Connected"));
                    return;
                case "stop":
                    DiscordClient.getInstance().disconnect();
                    sender.addChatMessage(new ChatComponentText("Disconnected"));
                    return;
            }
        }

        sender.addChatMessage(new ChatComponentText("Unknown command - Available commands: reload, start, stop"));
    }

    @SuppressWarnings("unused")
    public static void Log(String message) {
        Log(message, false);
    }

    static void Log(String message, boolean warning) {
        logger.log(warning ? Level.WARN : Level.INFO, String.format("[%s] %s", Constants.VERSION, message));
    }
}
