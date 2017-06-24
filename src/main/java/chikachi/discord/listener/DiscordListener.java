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
import chikachi.discord.core.Message;
import chikachi.discord.core.Patterns;
import chikachi.discord.core.config.ConfigWrapper;
import chikachi.discord.core.config.Configuration;
import chikachi.discord.core.config.discord.CommandConfig;
import chikachi.discord.core.config.discord.DiscordChannelGenericConfig;
import chikachi.discord.core.config.discord.DiscordConfig;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

        Long channelId = event.getChannel().getIdLong();

        DiscordChannelGenericConfig channelConfig;
        ArrayList<Integer> dimensions;
        if (discordConfig.channels.channels.containsKey(channelId)) {
            channelConfig = discordConfig.channels.channels.get(channelId);
            dimensions = channelConfig.relayChat.getDimensions(discordConfig.channels.generic.relayChat);
        } else {
            channelConfig = discordConfig.channels.generic;
            dimensions = discordConfig.channels.generic.relayChat.getDimensions();
        }

        if (dimensions == null) {
            return;
        }

        String content = event.getMessage().getContent().trim();

        if (IMCHandler.haveListeners()) {
            NBTTagCompound eventTagCompound = new NBTTagCompound();
            eventTagCompound.setString("type", "chat");

            NBTTagCompound userTagComponent = new NBTTagCompound();
            userTagComponent.setString("id", event.getAuthor().getId());
            userTagComponent.setString("username", event.getAuthor().getName());

            eventTagCompound.setTag("user", userTagComponent);
            eventTagCompound.setString("message", content);

            IMCHandler.emitMessage("event", eventTagCompound);
        }

        String prefix = channelConfig.commandPrefix != null ? channelConfig.commandPrefix : discordConfig.channels.generic.commandPrefix;

        if (content.startsWith(prefix)) {
            List<String> args = new ArrayList<>(Arrays.asList(content.substring(prefix.length()).split(" ")));
            String cmd = args.remove(0);

            List<CommandConfig> commands = Configuration.getConfig().discord.getCommandConfigs();
            for (CommandConfig command : commands) {
                if (command.shouldExecute(cmd, event.getAuthor(), event.getChannel())) {
                    FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(
                        new DiscordCommandSender(event.getChannel(), event.getAuthor()),
                        command.buildCommand(args)
                    );
                    return;
                }
            }

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

        HashMap<String, String> arguments = new HashMap<>();
        arguments.put(
            "MESSAGE",
            Patterns.discordToMinecraft(
                event.getMessage().getContent()
            )
        );

        Message message = new Message(
            event.getAuthor().getName(),
            config.discord.channels.generic.messages.chatMessage,
            arguments
        );
        for (EntityPlayerMP player : players) {
            player.sendMessage(new TextComponentString(message.getFormattedText()));
        }
    }
}
