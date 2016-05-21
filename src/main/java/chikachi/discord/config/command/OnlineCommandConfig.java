package chikachi.discord.config.command;

import chikachi.discord.DiscordClient;
import com.google.common.base.Joiner;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class OnlineCommandConfig extends CommandConfig {
    public OnlineCommandConfig() {
        super("online");
    }

    @Override
    public void execute(List<String> args) {
        List<String> playerNames = new ArrayList<>();

        List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().getPlayerList();

        for (EntityPlayerMP player : players) {
            String playerName = player.getDisplayNameString();
            if (playerName.startsWith("@")) {
                continue;
            }
            playerNames.add(playerName);
        }

        int playersOnline = playerNames.size();
        if (playersOnline == 0) {
            DiscordClient.getInstance().sendMessage("No players online");
            return;
        }

        if (playersOnline == 1) {
            DiscordClient.getInstance().sendMessage(
                    String.format(
                            "Currently 1 player online: `%s`",
                            Joiner.on(", ").join(playerNames)
                    )
            );
            return;
        }

        DiscordClient.getInstance().sendMessage(
                String.format(
                        "Currently %d players online:\n`%s`",
                        playersOnline,
                        Joiner.on("`, `").join(playerNames)
                )
        );
    }
}
