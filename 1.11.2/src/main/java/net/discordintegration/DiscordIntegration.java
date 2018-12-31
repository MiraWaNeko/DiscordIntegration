package net.discordintegration;

import net.discordintegration.core.CoreConstants;
import net.discordintegration.core.Proxy;
import net.minecraftforge.fml.common.Mod;

@Mod(
        modid = CoreConstants.MODID,
        name = CoreConstants.MODNAME,
        version = CoreConstants.VERSION,
        serverSideOnly = true,
        acceptableRemoteVersions = "*"
)
public class DiscordIntegration {
    @Mod.Instance
    static DiscordIntegration instance;

    public DiscordIntegration() {
        // Register the MinecraftMethods-Interface
        Proxy.getBridge().setMinecraft(new MinecraftMethods());
    }
}