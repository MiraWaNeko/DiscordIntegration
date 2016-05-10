package chikachi.discord;

import chikachi.discord.config.Configuration;
import chikachi.discord.config.EnableMessageTuple;
import com.google.common.base.Joiner;
import net.dv8tion.jda.OnlineStatus;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.guild.GuildAvailableEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

class DiscordListener extends ListenerAdapter {
    private static final DecimalFormat timeFormatter = new DecimalFormat("########0.000");
    private static final Pattern everyonePattern = Pattern.compile("(^|\\W)@everyone\\b");
    private HashMap<String, FakePlayer> fakePlayers = new HashMap<>();

    private void userOnline(User user) {
        if (user == null) return;
        if (user.getOnlineStatus() == OnlineStatus.OFFLINE) return;
        if (!Configuration.isExperimentalFakePlayersEnabled()) return;

        DiscordFakePlayer discordFakePlayer = new DiscordFakePlayer(user);

        ServerConfigurationManager configurationManager = MinecraftServer.getServer().getConfigurationManager();

        configurationManager.playerEntityList.add(discordFakePlayer);

        fakePlayers.put(user.getUsername(), discordFakePlayer);
    }

    private void userOffline(User user) {
        if (user == null) return;
        if (user.getOnlineStatus() != OnlineStatus.OFFLINE) return;
        if (!Configuration.isExperimentalFakePlayersEnabled()) return;

        if (fakePlayers.containsKey(user.getUsername())) {
            FakePlayer fakePlayer = fakePlayers.get(user.getUsername());
            if (fakePlayer != null) {
                MinecraftServer.getServer().getConfigurationManager().playerEntityList.remove(fakePlayer);
            }
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        ChikachiDiscord.Log("Logged in as " + event.getJDA().getSelfInfo().getUsername());

        DiscordClient client = DiscordClient.getInstance();

        TextChannel channel = client.getChannel();
        if (channel == null) {
            return;
        }

        List<String> queue = client.queue;
        queue.forEach(client::sendMessage);
        client.queue.clear();

        if (Configuration.isExperimentalFakePlayersEnabled()) {
            List<User> users = channel.getUsers();
            users.forEach(this::userOnline);
        }
    }

    @Override
    public void onGuildAvailable(GuildAvailableEvent event) {
        ChikachiDiscord.Log("GuildAvailable - " + event.getGuild().getName());
    }

    @Override
    public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {
        if (!Configuration.isExperimentalFakePlayersEnabled()) return;

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

        TextChannel channel = event.getTextChannel();
        String content = event.getMessage().getContent().trim();
        MinecraftServer minecraftServer = MinecraftServer.getServer();

        if (content.startsWith("!")) {
            List<String> args = new ArrayList<>(Arrays.asList(content.substring(1).split(" ")));
            String cmd = args.remove(0);
            if (Configuration.isCommandOnlineEnabled() && cmd.equalsIgnoreCase("online") && Configuration.getCommandOnline().canExecute(event)) {
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
            } else if (Configuration.isCommandTpsEnabled() && cmd.equalsIgnoreCase("tps") && Configuration.getCommandTps().canExecute(event)) {
                List<String> tpsTimes = new ArrayList<>();

                for (Integer dimId : DimensionManager.getIDs()) {
                    double worldTickTime = this.mean(minecraftServer.worldTickTimes.get(dimId)) * 1.0E-6D;
                    double worldTPS = Math.min(1000.0 / worldTickTime, 20);
                    tpsTimes.add(
                            StatCollector.translateToLocalFormatted(
                                    "commands.forge.tps.summary",
                                    String.format("Dim %d", dimId),
                                    timeFormatter.format(worldTickTime),
                                    timeFormatter.format(worldTPS)
                            )
                    );
                }

                double meanTickTime = this.mean(minecraftServer.tickTimeArray) * 1.0E-6D;
                double meanTPS = Math.min(1000.0 / meanTickTime, 20);
                tpsTimes.add(
                        StatCollector.translateToLocalFormatted(
                                "commands.forge.tps.summary",
                                "Overall",
                                timeFormatter.format(meanTickTime),
                                timeFormatter.format(meanTPS)
                        )
                );

                DiscordClient.getInstance().sendMessage(
                        String.format(
                                "\n```\n%s\n```",
                                Joiner.on("\n").join(tpsTimes)
                        ).replace("\\:", ":")
                );
            } else if (cmd.equalsIgnoreCase("roles")) {
                List<Role> roles = new ArrayList<>(event.getGuild().getRolesForUser(event.getAuthor()));
                roles.sort((r1, r2) -> r2.getPosition() - r1.getPosition());

                DiscordClient.getInstance().sendMessage(
                        Joiner.on("\n").join(roles)
                );
            }
            return;
        }

        EnableMessageTuple setting = Configuration.getMinecraftChat();
        if (!setting.isEnabled()) return;

        if (Configuration.getMinecraftChatMaxLength() > 0 && content.length() > Configuration.getMinecraftChatMaxLength()) {
            content = content.substring(0, Configuration.getMinecraftChatMaxLength());
        }

        if (content.contains("@everyone") && channel.checkPermission(event.getAuthor(), Permission.MESSAGE_MENTION_EVERYONE)) {
            content = everyonePattern.matcher(content).replaceAll("$1" + EnumChatFormatting.BLUE + "@everyone" + EnumChatFormatting.RESET);
        }

        String messageText = setting.getMessage()
                .replace("%USER%", event.getAuthor().getUsername())
                .replace("%MESSAGE%", content);

        IChatComponent chatComponent = ForgeHooks.newChatWithLinks(messageText, false);

        List<EntityPlayerMP> players = minecraftServer.getConfigurationManager().getPlayerList();

        for (EntityPlayerMP player : players) {
            if (content.contains(player.getDisplayNameString())) {
                String playerName = player.getDisplayNameString();


                String playerMessageText = setting.getMessage()
                        .replace("%USER%", event.getAuthor().getUsername())
                        .replace("%MESSAGE%", content.replaceAll(
                                "\\b" + playerName + "\\b",
                                EnumChatFormatting.BLUE + playerName + EnumChatFormatting.RESET
                        ));

                player.addChatMessage(ForgeHooks.newChatWithLinks(playerMessageText, false));
                continue;
            }

            player.addChatMessage(chatComponent);
        }
    }

    private long mean(long[] values) {
        return LongStream.of(values).sum() / values.length;
    }
}
