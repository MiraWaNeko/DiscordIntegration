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
