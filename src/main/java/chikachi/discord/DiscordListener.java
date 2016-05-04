package chikachi.discord;

import com.google.common.base.Joiner;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.OnlineStatus;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

class DiscordListener extends ListenerAdapter {
    private HashMap<String, FakePlayer> fakePlayers = new HashMap<>();
    private MinecraftServer minecraftServer;

    private void userOnline(User user) {
        if (user == null) return;
        if (user.getOnlineStatus() == OnlineStatus.OFFLINE) return;

        DiscordFakePlayer discordFakePlayer = new DiscordFakePlayer(user);

        minecraftServer.getConfigurationManager().playerEntityList.add(discordFakePlayer);

        fakePlayers.put(user.getUsername(), discordFakePlayer);
    }

    private void userOffline(User user) {
        if (user == null) return;
        if (user.getOnlineStatus() != OnlineStatus.OFFLINE) return;

        if (fakePlayers.containsKey(user.getUsername())) {
            FakePlayer fakePlayer = fakePlayers.get(user.getUsername());
            if (fakePlayer != null) {
                minecraftServer.getConfigurationManager().playerEntityList.remove(fakePlayer);
            }
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        DiscordClient client = DiscordClient.getInstance();
        List<String> queue = client.queue;
        queue.forEach(client::sendMessage);
        client.queue.clear();

        minecraftServer = MinecraftServer.getServer();

        List<User> users = client.channel.getUsers();
        users.forEach(this::userOnline);
    }

    @Override
    public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {
        User user = event.getUser();
        OnlineStatus before = event.getPreviousOnlineStatus();
        OnlineStatus now = user.getOnlineStatus();

        if (before == OnlineStatus.OFFLINE && now != OnlineStatus.OFFLINE) {
            userOnline(user);
        } else if (before != OnlineStatus.OFFLINE && now == OnlineStatus.OFFLINE) {
            userOffline(user);
        }
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
            if (Configuration.isCommandOnlineEnabled() && cmd.equalsIgnoreCase("online")) {
                List<String> playerNames = new ArrayList<>();

                List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().getPlayerList();
                int playersOnline = players.size();
                if (playersOnline == 0) {
                    DiscordClient.getInstance().sendMessage("No players online");
                    return;
                }

                playerNames.addAll(players.stream().map(EntityPlayerMP::getDisplayNameString).collect(Collectors.toList()));

                if (playersOnline == 1) {
                    DiscordClient.getInstance().sendMessage(
                            String.format(
                                    "Current player online: %s",
                                    Joiner.on(", ").join(playerNames)
                            )
                    );
                    return;
                }

                DiscordClient.getInstance().sendMessage(
                        String.format(
                                "Current players online (%d): %s",
                                playersOnline,
                                Joiner.on(", ").join(playerNames)
                        )
                );
            }
            return;
        }

        EnableMessageTuple setting = Configuration.getMinecraftChat();
        if (!setting.isEnabled()) return;

        String messageText = String.format(
                setting.getMessage(),
                event.getAuthor().getUsername(),
                content
        );

        IChatComponent chatComponent = ForgeHooks.newChatWithLinks(messageText, false);

        if (MinecraftServer.getServer() != null && MinecraftServer.getServer().getConfigurationManager() != null && !MinecraftServer.getServer().isSinglePlayer()) {
            MinecraftServer.getServer().getConfigurationManager().sendChatMsg(chatComponent);
        } else {
            if (Minecraft.getMinecraft().thePlayer != null) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(chatComponent);
            }
        }
    }
}
