package net.discordintegration;

import net.discordintegration.command.CommandDiscord;
import net.discordintegration.core.CoreConstants;
import net.discordintegration.core.CoreUtils;
import net.discordintegration.core.DiscordUpdaterThread;
import net.discordintegration.core.Proxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;

@Mod(
        modid = CoreConstants.MODID,
        name = CoreConstants.MODNAME,
        version = CoreConstants.VERSION,
        serverSideOnly = true,
        acceptableRemoteVersions = "*"
)
public class DiscordIntegration {
    private static Thread updateThread;

    @Mod.Instance
    static DiscordIntegration instance;

    public DiscordIntegration() {
        // Register the MinecraftMethods-Interface
        Proxy.getBridge().setMinecraft(new MinecraftMethods());
    }

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        Proxy.onPreInit(event.getModConfigurationDirectory());
        CoreUtils.addPatterns();
        MinecraftForge.EVENT_BUS.register(new MinecraftListener());
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        event.buildSoftDependProxy("dynmap", "chikachi.discord.integration.DynmapIntegration");
    }

    @Mod.EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        Proxy.onServerStarting();

        //DiscordClient.getInstance().addEventListener(new DiscordListener());

        event.registerServerCommand(new CommandDiscord());

        updateThread = new Thread(new DiscordUpdaterThread());
        updateThread.start();
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        Proxy.onServerStarted();
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        updateThread.interrupt();
        Proxy.onServerStopping();
    }

    @Mod.EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        Proxy.onServerStopped();
    }

    @Mod.EventHandler
    public void imcReceived(FMLInterModComms.IMCEvent event) {
        /*for (FMLInterModComms.IMCMessage imcMessage : event.getMessages()) {
            IMCHandler.onMessageReceived(imcMessage);
        }*/
    }
}