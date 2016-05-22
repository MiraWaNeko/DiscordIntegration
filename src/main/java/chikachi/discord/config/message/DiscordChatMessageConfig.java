package chikachi.discord.config.message;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.ForgeHooks;

import java.io.IOException;
import java.util.List;

public class DiscordChatMessageConfig extends BaseMessageConfig {
    private int maxChatLength = -1;
    private EnumChatFormatting usernameColor = EnumChatFormatting.RED;

    public DiscordChatMessageConfig(boolean enabled, String message) {
        super("chat", enabled, message);
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
        MinecraftServer minecraftServer = MinecraftServer.getServer();
        List<EntityPlayerMP> players = minecraftServer.getConfigurationManager().getPlayerList();
        if (players.size() == 0) {
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

        String[] messageParts = this.message
                .replace("%MESSAGE%",
                        ForgeHooks.newChatWithLinks(
                                content
                        ).getFormattedText()
                )
                .split("%USER%");

        IChatComponent usernameComponent = new ChatComponentText(event.getAuthor().getUsername());

        HoverEvent usernameHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Mention user (Clears current message)"));
        ClickEvent usernameClickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "@" + event.getAuthor().getUsername() + " ");

        usernameComponent.getChatStyle()
                .setColor(this.usernameColor)
                .setChatClickEvent(usernameClickEvent)
                .setChatHoverEvent(usernameHoverEvent);

        IChatComponent chatComponent = new ChatComponentText(messageParts[0]);
        for (int i = 1, j = messageParts.length; i < j; i++) {
            chatComponent.appendSibling(
                    usernameComponent
            ).appendText(
                    messageParts[i]
            );
        }

        for (EntityPlayerMP player : players) {
            if (content.contains(player.getDisplayNameString())) {
                String playerName = player.getDisplayNameString();

                String[] playerMessageParts = this.message
                        .replace("%MESSAGE%",
                                ForgeHooks.newChatWithLinks(
                                        content.replaceAll(
                                                "\\b" + playerName + "\\b",
                                                EnumChatFormatting.BLUE + playerName + EnumChatFormatting.RESET
                                        ), false
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

                player.addChatMessage(playerChatComponent);
                continue;
            }

            player.addChatMessage(chatComponent);
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
        }

        reader.skipValue();
    }

    @Override
    protected void writeExtra(JsonWriter writer) throws IOException {
        writer.name("maxLength");
        writer.value(this.maxChatLength);
        writer.name("usernameColor");
        writer.value(this.usernameColor.getFriendlyName());
    }
}
