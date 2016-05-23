package chikachi.discord.config.listener;

import chikachi.discord.ChikachiDiscord;
import chikachi.discord.DiscordClient;
import chikachi.discord.config.Configuration;
import chikachi.discord.config.message.DiscordChatMessageConfig;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiscordListener extends ListenerAdapter {
    private final MinecraftServer minecraftServer;

    public DiscordListener(MinecraftServer minecraftServer) {
        this.minecraftServer = minecraftServer;
    }

    @Override
    public void onReady(ReadyEvent event) {
        ChikachiDiscord.Log("Logged in as " + event.getJDA().getSelfInfo().getUsername());

        DiscordClient client = DiscordClient.getInstance();

        List<String> queue = client.queue;
        queue.forEach(client::sendMessage);
        client.queue.clear();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignore bots
        if (event.getAuthor().isBot()) return;
        // Ignore private messages
        if (!(event.getChannel() instanceof TextChannel)) return;
        // Ignore other channels
        if (!event.getMessage().getChannelId().equals(Configuration.getChannel())) return;

        String content = event.getMessage().getContent().trim();

        if (content.startsWith("!")) {
            List<String> args = new ArrayList<>(Arrays.asList(content.substring(1).split(" ")));
            String cmd = args.remove(0);

            // Online
            if (Configuration.getCommandOnline().shouldExecute(cmd, event)) {
                Configuration.getCommandOnline().execute(this.minecraftServer, args);
                return;
            }

            // TPS
            if (Configuration.getCommandTps().shouldExecute(cmd, event)) {
                Configuration.getCommandTps().execute(this.minecraftServer, args);
                return;
            }

            // Unstuck
            if (Configuration.getCommandUnstuck().shouldExecute(cmd, event)) {
                Configuration.getCommandUnstuck().execute(this.minecraftServer, args);
                return;
            }

            return;
        }

        DiscordChatMessageConfig messageConfig = Configuration.getMinecraftChat();
        messageConfig.handleEvent(event);
    }
}
