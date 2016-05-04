package chikachi.discord;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@SuppressWarnings("UnusedParameters")
class Proxy {
    void onPreInit(FMLPreInitializationEvent event) {
        Configuration.onPreInit(event);

        MinecraftForge.EVENT_BUS.register(new MinecraftListener());
    }

    void onServerStarting(FMLServerStartingEvent event) {
        DiscordClient.getInstance().connect();
    }

    void onServerStarted(FMLServerStartedEvent event) {
        EnableMessageTuple setting = Configuration.getDiscordStartup();

        if (setting.isEnabled()) {
            DiscordClient.getInstance().queue.add(setting.getMessage());
        }
    }

    void onServerShutdown(FMLServerStoppingEvent event) {
        EnableMessageTuple setting = Configuration.getDiscordShutdown();

        if (setting.isEnabled()) {
            DiscordClient.getInstance().sendMessage(setting.getMessage());
        }
    }
}
