package chikachi.discord;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    public void onServerStarting(FMLServerStartingEvent event) {
        proxy.onServerStarting(event);
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

    @SuppressWarnings("unused")
    public static void Log(String message) {
        Log(message, false);
    }

    static void Log(String message, boolean warning) {
        logger.log(warning ? Level.WARN : Level.INFO, String.format("[%s] %s", Constants.VERSION, message));
    }
}
