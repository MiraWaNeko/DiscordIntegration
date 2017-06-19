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

package chikachi.discord.core;

import chikachi.discord.core.config.Configuration;
import chikachi.discord.core.config.types.MessageConfig;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

public class Message {
    private final String author;
    private final String avatar_url;
    private final MessageConfig message;
    private final HashMap<String, String> arguments;

    public Message(MessageConfig message) {
        this(message, new HashMap<>());
    }

    public Message(MessageConfig message, HashMap<String, String> arguments) {
        this(null, message, arguments);
    }

    public Message(String author, MessageConfig message) {
        this(author, message, new HashMap<>());
    }

    public Message(String author, MessageConfig message, HashMap<String, String> arguments) {
        this(author, null, message, arguments);
    }

    public Message(String author, String avatar_url, MessageConfig message) {
        this(author, avatar_url, message, new HashMap<>());
    }

    public Message(String author, String avatar_url, MessageConfig message, HashMap<String, String> arguments) {
        this.author = author;
        this.avatar_url = avatar_url;
        this.message = message;

        if (arguments == null) {
            this.arguments = new HashMap<>();
        } else {
            this.arguments = arguments;
        }
        this.arguments.put("USER", getAuthor());
    }

    public WebhookMessage toWebhook(TextChannel channel) {
        WebhookMessage webhookMessage = new WebhookMessage();
        webhookMessage.content = formatText(message.webhook, channel);
        webhookMessage.username = this.author;
        webhookMessage.avatar_url = this.avatar_url;
        return webhookMessage;
    }

    public String getAuthor() {
        return this.author;
    }

    private String formatText(String text) {
        return formatText(text, null);
    }

    private String formatText(String text, Channel channel) {
        String message = text;
        for (Map.Entry<String, String> entry : this.arguments.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        if (channel != null) {
            if (message.contains("@")) {
                Matcher m = Patterns.tagPattern.matcher(message);
                StringBuffer sb = new StringBuffer();
                while (m.find()) {
                    String name = m.group(1);

                    if (Configuration.getConfig().minecraft.dimensions.generic.canMentionUsers) {
                        Optional<Member> theMember = channel.getGuild().getMembersByName(name, true)
                            .stream()
                            .filter(member -> member.hasPermission(channel, Permission.MESSAGE_READ))
                            .findAny();

                        if (theMember.isPresent()) {
                            m.appendReplacement(sb, theMember.get().getAsMention());
                            continue;
                        }
                    }

                    if (Configuration.getConfig().minecraft.dimensions.generic.canMentionRoles) {
                        Optional<Role> theRole =
                            channel
                                .getGuild()
                                .getRolesByName(name, true)
                                .stream()
                                .filter(role -> role.hasPermission(channel, Permission.MESSAGE_READ))
                                .findAny();

                        if (theRole.isPresent()) {
                            m.appendReplacement(sb, theRole.get().getAsMention());
                            continue;
                        }
                    }

                    m.appendReplacement(sb, name);
                }
                m.appendTail(sb);
                message = sb.toString();
            }
        }

        return message;
    }

    public String getUnformattedText() {
        return this.message.normal;
    }

    public String getFormattedText() {
        return getFormattedText(null);
    }

    public String getFormattedText(Channel channel) {
        return formatText(getUnformattedText(), channel);
    }
}
