/*
 * Copyright (C) 2018 Chikachi and other contributors
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

package net.discordintegration.core;

import com.vdurmont.emoji.EmojiParser;
import net.discordintegration.core.config.Configuration;
import net.discordintegration.core.config.types.MessageConfig;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Matcher;

public class Message {
    private String author = null;
    private String avatarUrl = null;
    private String prefix = null;
    private MessageConfig message = null;
    private HashMap<String, String> arguments = null;
    private boolean parsing = true;

    public Message() {
    }

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

    public Message(String author, String avatarUrl, MessageConfig message) {
        this(author, avatarUrl, message, new HashMap<>());
    }

    public Message(String author, String avatarUrl, MessageConfig message, HashMap<String, String> arguments) {
        this.author = author;
        this.avatarUrl = avatarUrl;
        this.message = message;

        if (arguments == null) {
            this.arguments = new HashMap<>();
        } else {
            this.arguments = arguments;
        }
        this.arguments.put("USER", getAuthor());
    }

    WebhookMessage toWebhook(TextChannel channel) {
        return new WebhookMessage(
            formatText(message.webhook, channel),
            this.author,
            this.avatarUrl
        );
    }

    public Message setMessage(MessageConfig message) {
        this.message = message;
        return this;
    }

    public Message setAvatarUrl(String avatar_url) {
        this.avatarUrl = avatar_url;
        return this;
    }

    public Message setArguments(HashMap<String, String> arguments) {
        this.arguments = arguments;
        return this;
    }

    public Message setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    private String getAuthor() {
        return this.author;
    }

    public Message setAuthor(String author) {
        this.author = author;
        return this;
    }

    private boolean isParsing() {
        return this.parsing;
    }

    public Message setParsing(boolean parsing) {
        this.parsing = parsing;
        return this;
    }

    private String formatText(String text, Channel channel) {
        return formatText(text, channel, true);
    }

    private String formatText(String text, Channel channel, boolean isDiscord) {
        String message = text;

        if (this.arguments == null) {
            this.arguments = new HashMap<>();
        }
        this.arguments.put("USER", getAuthor());

        message = new TextFormatter(arguments).format(message);

        if (channel != null) {
            if (message.contains("@")) {
                Matcher m = Patterns.tagPattern.matcher(message);
                StringBuffer sb = new StringBuffer();
                while (m.find()) {
                    String name = m.group(2);
                    String hash = m.group(3);

                    if (name.equalsIgnoreCase("everyone") && hash == null) {
                        if (Configuration.getConfig().minecraft.dimensions.generic.canMentionEveryone) {
                            return "@everyone";
                        } else {
                            return name;
                        }
                    }

                    if (name.equalsIgnoreCase("here") && hash == null) {
                        if (Configuration.getConfig().minecraft.dimensions.generic.canMentionHere) {
                            return "@here";
                        } else {
                            return name;
                        }
                    }

                    if (Configuration.getConfig().minecraft.dimensions.generic.canMentionUsers) {
                        Optional<Member> theMember = channel.getGuild().getMembersByName(name, true)
                            .stream()
                            .filter(member -> hash == null || member.getUser().getDiscriminator().equalsIgnoreCase(hash))
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
                                .filter(Role::isMentionable)
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

        if (this.isParsing()) {
            if (isDiscord) {
                message = CoreUtils.replace(CoreConstants.minecraftToDiscordEmotes, message);
                message = EmojiParser.parseToUnicode(message);
                message = Patterns.minecraftToDiscord(message);
            } else {
                message = EmojiParser.parseToAliases(message, EmojiParser.FitzpatrickAction.REMOVE);
                message = CoreUtils.replace(CoreConstants.discordToMinecraftEmotes, message);
                message = Patterns.discordToMinecraft(message);
            }

        }

        return String.format(
            "%s%s",
            this.prefix != null && this.prefix.trim().length() > 0 ? this.prefix.trim() + " " : "",
            message
        );
    }

    private String getUnformattedText() {
        return this.message != null ? this.message.normal : "";
    }

    String getFormattedTextDiscord(Channel channel) {
        return formatText(getUnformattedText(), channel, true);
    }

    public String getFormattedTextMinecraft() {
        return formatText(getUnformattedText(), null, false);
    }
}
