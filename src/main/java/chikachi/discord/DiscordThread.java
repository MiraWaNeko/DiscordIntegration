package chikachi.discord;

import chikachi.discord.core.DiscordClient;
import chikachi.discord.core.DiscordIntegrationLogger;
import chikachi.discord.core.TextFormatter;
import chikachi.discord.core.config.Configuration;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

import static java.lang.Thread.sleep;

public class DiscordThread implements Runnable {

    @Override
    public void run() {
        DiscordIntegrationLogger.Log("Started update thread");
        while (!Thread.interrupted()) {
            updatePlayerCountInPresence();

            try {
                sleep(1000 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        DiscordIntegrationLogger.Log("Stopped update thread");
    }

    private void updatePlayerCountInPresence() {
        if (!DiscordClient.getInstance().isConnected())
            return;
        if (!Configuration.getConfig().discord.presence.enabled)
            return;

        Object[] players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()
            .stream().filter(player -> !player.getDisplayNameString().startsWith("@")).toArray();
        long count = players.length;
        String message = "";

        if (count == 0) {
            message = Configuration.getConfig().discord.presence.messages.noPlayerOnline;
        } else if (count == 1) {
            Object player = players[0];
            String name = "unknown";
            if (player instanceof EntityPlayerMP) {
                name = ((EntityPlayerMP) player).getDisplayNameString();
            }
            message = new TextFormatter()
                .addArgument("USER", name)
                .addArgument("COUNT", "1")
                .format(Configuration.getConfig().discord.presence.messages.onePlayerOnline);

        } else {
            message = new TextFormatter()
                .addArgument("COUNT", String.format("%d", count))
                .format(Configuration.getConfig().discord.presence.messages.onePlayerOnline);
        }
        DiscordClient.getInstance().setDiscordPresencePlaying(message);
    }
}
