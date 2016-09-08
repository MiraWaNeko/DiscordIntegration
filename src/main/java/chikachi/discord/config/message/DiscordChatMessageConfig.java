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

package chikachi.discord.config.message;

import chikachi.discord.Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.ForgeHooks;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordChatMessageConfig extends BaseMessageConfig {
    private int maxChatLength = -1;
    private EnumChatFormatting usernameColor = EnumChatFormatting.RED;
    private boolean customFormatting = false;

    private static Map<String, String> emoteMap;

    static {
        emoteMap = new HashMap<>();
        emoteMap.put(":slight_smile:", ":)");
        emoteMap.put(":smile:", ":D");
        emoteMap.put(":wink:", ";)");
        emoteMap.put(":wink:", ";D");
        emoteMap.put(":laughing:", "xD");
        emoteMap.put(":laughing:", "XD");
        emoteMap.put(":stuck_out_tongue:", ":p");
        emoteMap.put(":stuck_out_tongue:", ":P");
        emoteMap.put(":stuck_out_tongue_winking_eye:", ";p");
        emoteMap.put(":stuck_out_tongue_winking_eye:", ";P");
        emoteMap.put(":stuck_out_tongue_closed_eyes:", "xp");
        emoteMap.put(":stuck_out_tongue_closed_eyes:", "xP");
        emoteMap.put(":stuck_out_tongue_closed_eyes:", "Xp");
        emoteMap.put(":stuck_out_tongue_closed_eyes:", "XP");
        emoteMap.put(":open_mouth:", ":O");
        emoteMap.put(":open_mouth:", ":o");
        emoteMap.put(":dizzy_face:", "xO");
        emoteMap.put(":dizzy_face:", "XO");
        emoteMap.put(":neutral_face:", ":|");
        emoteMap.put(":sunglasses:", "B)");
        emoteMap.put(":kissing:", ":*");
        emoteMap.put(":sob:", ";.;");
        emoteMap.put(":cry:", ";_;");
        emoteMap.put(":heart:", "<3");
        emoteMap.put(":broken_heart:", "</3");
        emoteMap.put(":thumbsup:", "(y)");
        emoteMap.put(":thumbsup:", "(Y)");
        emoteMap.put(":thumbsup:", "(yes)");
        emoteMap.put(":thumbsdown:", "(n)");
        emoteMap.put(":thumbsdown:", "(N)");
        emoteMap.put(":thumbsdown:", "(no)");
        emoteMap.put(":ok_hand:", "(ok)");
    }

    public DiscordChatMessageConfig(boolean enabled, String message) {
        super("chat", enabled, message);
    }

    private IChatComponent attachmentToTextComponent(Message.Attachment attachment) {
        HoverEvent attachmentHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Open link to attachment"));
        ClickEvent attachmentClickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, attachment.getUrl());

        IChatComponent chatComponent = new ChatComponentText("[" + attachment.getFileName() + "]");
        chatComponent.getChatStyle()
                .setColor(EnumChatFormatting.AQUA)
                .setChatClickEvent(attachmentClickEvent)
                .setChatHoverEvent(attachmentHoverEvent);

        return chatComponent;
    }

    public void handleEvent(MessageReceivedEvent event) {
        if (!this.isEnabled()) {
            return;
        }

        String content = event.getMessage().getContent();
        if (this.maxChatLength > -1 && content.length() > this.maxChatLength) {
            content = content.substring(0, this.maxChatLength);
        }

        TextChannel channel = event.getTextChannel();
        ServerConfigurationManager serverConfigurationManager = MinecraftServer.getServer().getConfigurationManager();
        String[] playerNames = serverConfigurationManager.getAllUsernames();
        if (playerNames.length == 0) {
            return;
        }

        if (content.contains("@everyone") && channel.checkPermission(event.getAuthor(), Permission.MESSAGE_MENTION_EVERYONE)) {
            content = Patterns.everyonePattern.matcher(content).replaceAll("$1" + EnumChatFormatting.BLUE + "@everyone" + EnumChatFormatting.RESET);
        }

        content = Patterns.boldPattern.matcher(content).replaceAll(EnumChatFormatting.BOLD + "$1" + EnumChatFormatting.RESET);
        content = Patterns.italicPattern.matcher(content).replaceAll(EnumChatFormatting.ITALIC + "$1" + EnumChatFormatting.RESET);
        content = Patterns.italicMePattern.matcher(content).replaceAll(EnumChatFormatting.ITALIC + "$1" + EnumChatFormatting.RESET);
        content = Patterns.underlinePattern.matcher(content).replaceAll(EnumChatFormatting.UNDERLINE + "$1" + EnumChatFormatting.RESET);
        content = Patterns.lineThroughPattern.matcher(content).replaceAll("$1");
        content = Patterns.multiCodePattern.matcher(content).replaceAll("$1");
        content = Patterns.singleCodePattern.matcher(content).replaceAll("$1");

        content = EmojiParser.parseToAliases(content, EmojiParser.FitzpatrickAction.REMOVE);

        content = Utils.Replace(emoteMap, content);

        if (!this.customFormatting) {
            String[] messageParts = this.message
                    .replace("%MESSAGE%", content)
                    .split("%USER%");

            IChatComponent usernameComponent = new ChatComponentText(event.getAuthor().getUsername());

            HoverEvent usernameHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Mention user (Clears current message)"));
            ClickEvent usernameClickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "@" + event.getAuthor().getUsername() + " ");

            usernameComponent.getChatStyle()
                    .setColor(this.usernameColor)
                    .setChatClickEvent(usernameClickEvent)
                    .setChatHoverEvent(usernameHoverEvent);

            IChatComponent chatComponent = new ChatComponentText(messageParts[0]);
            List<Message.Attachment> attachments = event.getMessage().getAttachments();

            for (int i = 1, j = messageParts.length; i < j; i++) {
                chatComponent.appendSibling(
                        usernameComponent
                ).appendText(
                        messageParts[i]
                );
            }

            for (Message.Attachment attachment : attachments) {
                chatComponent.appendText(" ");
                chatComponent.appendSibling(this.attachmentToTextComponent(attachment));
            }

            for (String playerName : playerNames) {
                EntityPlayer player = serverConfigurationManager.func_152612_a(playerName);

                if (player == null) {
                    continue;
                }

                if (content.contains(playerName)) {
                    String[] playerMessageParts = this.message
                            .replace("%MESSAGE%",
                                    ForgeHooks.newChatWithLinks(
                                            content.replaceAll(
                                                    "\\b" + playerName + "\\b",
                                                    EnumChatFormatting.BLUE + playerName + EnumChatFormatting.RESET
                                            )
                                    ).getFormattedText()
                            )
                            .split("%USER%");

                    IChatComponent playerChatComponent = new ChatComponentText(playerMessageParts[0]);
                    for (int i = 1, j = playerMessageParts.length; i < j; i++) {
                        playerChatComponent.appendSibling(
                                usernameComponent
                        ).appendText(
                                playerMessageParts[i]
                        );
                    }

                    for (Message.Attachment attachment : attachments) {
                        playerChatComponent.appendText(" ");
                        playerChatComponent.appendSibling(this.attachmentToTextComponent(attachment));
                    }

                    player.addChatMessage(playerChatComponent);
                    continue;
                }

                player.addChatMessage(chatComponent);
            }
        } else {
            IChatComponent message = ForgeHooks.newChatWithLinks(
                    this.message
                            .replace("%MESSAGE%", content)
                            .replace("%USER%", event.getAuthorName())
            );

            List<Message.Attachment> attachments = event.getMessage().getAttachments();

            for (Message.Attachment attachment : attachments) {
                message.appendText(" ");
                message.appendSibling(this.attachmentToTextComponent(attachment));
            }

            for (String playerName : playerNames) {
                EntityPlayer player = serverConfigurationManager.func_152612_a(playerName);

                if (player == null) {
                    continue;
                }

                player.addChatMessage(message);
            }
        }
    }

    @Override
    protected void readExtra(JsonReader reader, String name) throws IOException {
        switch (name) {
            case "maxLength":
                if (reader.peek() == JsonToken.NUMBER) {
                    this.maxChatLength = reader.nextInt();
                    return;
                }
                break;
            case "usernameColor":
                if (reader.peek() == JsonToken.STRING) {
                    this.usernameColor = EnumChatFormatting.getValueByName(reader.nextString());
                    return;
                }
            case "customFormatting":
                if (reader.peek() == JsonToken.BOOLEAN) {
                    this.customFormatting = reader.nextBoolean();
                    return;
                }
        }

        reader.skipValue();
    }

    @Override
    protected void writeExtra(JsonWriter writer) throws IOException {
        writer.name("maxLength");
        writer.value(this.maxChatLength);
        writer.name("usernameColor");
        writer.value(this.usernameColor.getFriendlyName());
        writer.name("customFormatting");
        writer.value(this.customFormatting);
    }
}
