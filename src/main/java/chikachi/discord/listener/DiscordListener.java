/**
 * Copyright (C) 2016 Chikachi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package chikachi.discord.listener;

import chikachi.discord.ChikachiDiscord;
import chikachi.discord.DiscordClient;
import chikachi.discord.IMCHandler;
import chikachi.discord.Utils;
import chikachi.discord.command.discord.CustomCommandConfig;
import chikachi.discord.config.Configuration;
import chikachi.discord.config.message.DiscordChatMessageConfig;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiscordListener extends ListenerAdapter {

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
        User author = event.getAuthor();

        // Ignore bots
        if (Configuration.isIgnoringBots() && author.isBot()) return;
        // Ignore self
        if (author.getId().equals(DiscordClient.getInstance().getSelf().getId())) return;
        // Ignore private messages
        if (!(event.getChannel() instanceof TextChannel)) return;
        // Ignore other channels
        if (!event.getMessage().getChannelId().equals(Configuration.getChannel())) return;

        String content = event.getMessage().getContent().trim();

        List<String> listenerMods = IMCHandler.getRegisteredIMCMods();
        if (listenerMods.size() > 0) {
            NBTTagCompound eventTagCompound = new NBTTagCompound();
            eventTagCompound.setString("type", "chat");

            eventTagCompound.setTag("user", Utils.UserToNBT(event.getAuthor()));
            eventTagCompound.setString("message", content);

            for (String listenerMod : listenerMods) {
                FMLInterModComms.sendRuntimeMessage(ChikachiDiscord.instance, listenerMod, "event", eventTagCompound);
            }
        }

        if (content.startsWith("!")) {
            List<String> args = new ArrayList<>(Arrays.asList(content.substring(1).split(" ")));
            String cmd = args.remove(0);

            // Online
            if (Configuration.getCommandOnline().shouldExecute(cmd, event)) {
                Configuration.getCommandOnline().execute(author, args);
                return;
            }

            // TPS
            if (Configuration.getCommandTps().shouldExecute(cmd, event)) {
                Configuration.getCommandTps().execute(author, args);
                return;
            }

            // Unstuck
            if (Configuration.getCommandUnstuck().shouldExecute(cmd, event)) {
                Configuration.getCommandUnstuck().execute(author, args);
                return;
            }

            // Custom
            List<CustomCommandConfig> customCommands = Configuration.getCustomCommands();
            for (CustomCommandConfig customCommand : customCommands) {
                if (customCommand.shouldExecute(cmd, event)) {
                    customCommand.execute(author, args);
                    return;
                }
            }

            return;
        }

        DiscordChatMessageConfig messageConfig = Configuration.getMinecraftChat();
        messageConfig.handleEvent(event);
    }
}
