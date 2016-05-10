package chikachi.discord;

import chikachi.discord.config.Configuration;
import chikachi.discord.config.EnableMessageTuple;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.*;

@SuppressWarnings("UnusedParameters")
class Proxy {
    void onPreInit(FMLPreInitializationEvent event) {
        Configuration.onPreInit(event);

        MinecraftForge.EVENT_BUS.register(new MinecraftListener());
    }

    void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        DiscordClient.getInstance().connect();
    }

    void onServerStarting(FMLServerStartingEvent event) {
    }

    void onServerStarted(FMLServerStartedEvent event) {
        EnableMessageTuple setting = Configuration.getDiscordStartup();

        if (setting.isEnabled()) {
            DiscordClient.getInstance().sendMessage(setting.getMessage());
        }
    }

    void onServerShutdown(FMLServerStoppingEvent event) {
        EnableMessageTuple setting = Configuration.getDiscordShutdown();

        if (setting.isEnabled()) {
            DiscordClient.getInstance().sendMessage(setting.getMessage());
        }

        DiscordClient.getInstance().disconnect();
    }
}
