/*
 * Copyright (C) 2017 Chikachi
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package chikachi.discord.listener;

import chikachi.discord.DiscordCommandSender;
import chikachi.discord.IMCHandler;
import chikachi.discord.core.DiscordClient;
import chikachi.discord.core.DiscordIntegrationLogger;
import chikachi.discord.core.Message;
import chikachi.discord.core.Patterns;
import chikachi.discord.core.config.ConfigWrapper;
import chikachi.discord.core.config.Configuration;
import chikachi.discord.core.config.discord.CommandConfig;
import chikachi.discord.core.config.discord.DiscordChannelGenericConfig;
import chikachi.discord.core.config.discord.DiscordConfig;
import chikachi.discord.core.config.linking.LinkingRequest;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.*;
import java.util.stream.Collectors;

public class DiscordListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        ConfigWrapper config = Configuration.getConfig();
        DiscordConfig discordConfig = config.discord;

        // Ignore bots
        if (discordConfig.ignoresBots && event.getAuthor().isBot()) {
            return;
        }

        // Ignore self
        if (event.getAuthor().getId().equals(DiscordClient.getInstance().getSelf().getId())) {
            return;
        }

        // Ignore specified users
        if (discordConfig.isIgnoringUser(event.getAuthor())) {
            return;
        }

        String content = event.getMessage().getContentDisplay().trim();

        if (event.getChannelType() == ChannelType.TEXT) {
            Long channelId = event.getChannel().getIdLong();

            DiscordChannelGenericConfig channelConfig;
            ArrayList<Integer> dimensions;
            boolean stripMinecraftCodes = discordConfig.channels.generic.stripMinecraftCodes;
            if (discordConfig.channels.channels.containsKey(channelId)) {
                channelConfig = discordConfig.channels.channels.get(channelId);
                dimensions = channelConfig.relayChat.getDimensions(discordConfig.channels.generic.relayChat);
                if (channelConfig.stripMinecraftCodes != null) {
                    stripMinecraftCodes = channelConfig.stripMinecraftCodes;
                }
            } else {
                // Don't relay messages from channels not configured
                return;
            }

            if (dimensions == null) {
                return;
            }

            if (IMCHandler.haveListeners()) {
                NBTTagCompound eventTagCompound = new NBTTagCompound();
                eventTagCompound.setString("type", "chat");

                NBTTagCompound userTagComponent = new NBTTagCompound();
                userTagComponent.setString("id", event.getAuthor().getId());
                userTagComponent.setString("username", event.getAuthor().getName());
                userTagComponent.setString("discriminator", event.getAuthor().getDiscriminator());

                eventTagCompound.setTag("user", userTagComponent);
                eventTagCompound.setString("message", content);

                IMCHandler.emitMessage("event", eventTagCompound);
            }

            String prefix = channelConfig.commandPrefix != null ? channelConfig.commandPrefix : discordConfig.channels.generic.commandPrefix;
            if (content.startsWith(prefix)) {
                List<String> args = new ArrayList<>(Arrays.asList(content.substring(prefix.length()).split(" ")));
                tryExecuteCommand(event, args);
                return;
            }

            MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
            List<EntityPlayerMP> players;
            if (dimensions.size() == 0) {
                players = minecraftServer.getPlayerList().getPlayers();
            } else {
                players = minecraftServer.getPlayerList().getPlayers()
                    .stream()
                    .filter(player -> dimensions.contains(player.dimension))
                    .collect(Collectors.toList());
            }

            if (stripMinecraftCodes) {
                content = Patterns.minecraftCodePattern.matcher(content).replaceAll("");
            }

            HashMap<String, String> arguments = new HashMap<>();
            arguments.put(
                "MESSAGE",
                content
            );

            Message message = new Message()
                .setAuthor(event.getMember().getEffectiveName())
                .setMessage(config.discord.channels.generic.messages.chatMessage)
                .setArguments(arguments);

            DiscordIntegrationLogger.Log(message.getFormattedTextMinecraft());
            for (EntityPlayerMP player : players) {
                player.sendMessage(new TextComponentString(message.getFormattedTextMinecraft()));
            }
        } else if (event.getChannelType() == ChannelType.PRIVATE && Configuration.getConfig().discord.channels.generic.allowDMCommands) {
            String prefix = discordConfig.channels.generic.commandPrefix;
            if (content.startsWith(prefix)) {
                List<String> args = new ArrayList<>(Arrays.asList(content.substring(prefix.length()).split(" ")));
                tryExecuteCommand(event, args);
            }
        }
    }

    private void tryExecuteCommand(MessageReceivedEvent event, List<String> args) {
        MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        String cmd = args.remove(0);

        if (Configuration.getConfig().discord.allowLinking) {
            if (cmd.equalsIgnoreCase("link")) {
                UUID minecraftUUID = Configuration.getLinking().getMinecraftId(event.getAuthor().getIdLong());
                if (minecraftUUID != null) {
                    GameProfile minecraftProfile = minecraftServer.getPlayerProfileCache().getProfileByUUID(minecraftUUID);
                    event.getAuthor().openPrivateChannel()
                        .queue(privateChannel -> privateChannel.sendMessage(
                            String.format(
                                "You are already linked to %s",
                                minecraftProfile == null ? "a Minecraft account" : minecraftProfile.getName()
                            )
                        ).queue());
                    return;
                }

                LinkingRequest request = Configuration.getLinking().getRequest(event.getAuthor().getIdLong());

                if (request.hasExpired()) {
                    request.generateCode();
                }

                event.getAuthor().openPrivateChannel()
                    .queue(privateChannel -> privateChannel.sendMessage(
                        String.format(
                            "Use `/discord link %s` on the Minecraft server to link your Discord user with your Minecraft user.\nThe code expires in %s!",
                            request.getCode(),
                            request.expiresIn()
                        )
                    ).queue());

                if (event.getMember().getPermissions(event.getTextChannel()).contains(Permission.MESSAGE_MANAGE)) {
                    event.getMessage().delete().queue();
                }

                Configuration.saveLinking();
                return;
            }
            if (cmd.equalsIgnoreCase("unlink")) {
                UUID minecraftUUID = Configuration.getLinking().getMinecraftId(event.getAuthor().getIdLong());
                if (minecraftUUID == null) {
                    event.getAuthor().openPrivateChannel()
                        .queue(privateChannel -> privateChannel.sendMessage(
                            "You aren't linked"
                        ).queue());
                } else {
                    Configuration.getLinking().removeLink(minecraftUUID);
                    event.getAuthor().openPrivateChannel()
                        .queue(privateChannel -> privateChannel.sendMessage(
                            "Unlinked"
                        ).queue());
                }

                if (event.getMember().getPermissions(event.getTextChannel()).contains(Permission.MESSAGE_MANAGE)) {
                    event.getMessage().delete().queue();
                }
                return;
            }
        }

        List<CommandConfig> commands = Configuration.getConfig().discord.getCommandConfigs();
        for (CommandConfig command : commands) {
            if (command.shouldExecute(cmd, event.getAuthor(), event.getChannel())) {
                FMLCommonHandler.instance().getMinecraftServerInstance().callFromMainThread(() -> {
                    FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(
                        new DiscordCommandSender(event.getChannel(), event.getAuthor(), command),
                        command.buildCommand(args)
                    );
                    return 0;
                });
                return;
            }
        }
    }
}
