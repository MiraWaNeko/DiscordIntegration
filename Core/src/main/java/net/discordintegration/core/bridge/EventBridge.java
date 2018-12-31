package net.discordintegration.core.bridge;

import net.discordintegration.core.DiscordClient;
import net.discordintegration.core.Message;
import net.discordintegration.core.Proxy;
import net.discordintegration.core.config.Configuration;
import net.discordintegration.core.config.minecraft.MinecraftConfig;
import net.discordintegration.core.config.minecraft.MinecraftDimensionConfig;
import net.discordintegration.core.config.types.MessageConfig;

import java.util.HashMap;
import java.util.UUID;

public class EventBridge {
    public void onChatMessage(UUID player, int dimension, String message) {
        HashMap<String, String> arguments = new HashMap<>();
        arguments.put("MESSAGE", message);

        MinecraftConfig minecraftConfig = Configuration.getConfig().minecraft;
        MinecraftDimensionConfig dimensionConfig = minecraftConfig.dimensions.getDimension(dimension);
        MinecraftDimensionConfig genericConfig = minecraftConfig.dimensions.generic;

        MessageConfig messageConfig = dimensionConfig.messages.chatMessage != null ? dimensionConfig.messages.chatMessage : genericConfig.messages.chatMessage;

        GetEventAuthorNameAndAvatar getEventAuthorNameAndAvatar = new GetEventAuthorNameAndAvatar(player).invoke();
        String authorName = getEventAuthorNameAndAvatar.getAuthorName();
        String avatarUrl = getEventAuthorNameAndAvatar.getAvatarUrl();

        DiscordClient.getInstance().broadcast(
                new Message()
                        .setAuthor(authorName)
                        .setAvatarUrl(avatarUrl)
                        .setMessage(messageConfig)
                        .setArguments(arguments)
                        .setPrefix(dimensionConfig.chatPrefix != null && dimensionConfig.chatPrefix.trim().length() > 0 ? dimensionConfig.chatPrefix : genericConfig.chatPrefix),
                dimensionConfig.relayChat.getChannels(
                        genericConfig.relayChat.getChannels(
                                dimensionConfig.discordChannel.getChannels(
                                        genericConfig.discordChannel
                                )
                        )
                )
        );
    }

    public void onPlayerAchievement(UUID player, int dimension, String title, String description) {
        HashMap<String, String> arguments = new HashMap<>();
        arguments.put("ACHIEVEMENT", title);
        //noinspection deprecation
        arguments.put("DESCRIPTION", Proxy.getBridge().getMinecraft().translate(description));

        MinecraftConfig minecraftConfig = Configuration.getConfig().minecraft;
        MinecraftDimensionConfig dimensionConfig = minecraftConfig.dimensions.getDimension(dimension);
        MinecraftDimensionConfig genericConfig = minecraftConfig.dimensions.generic;

        MessageConfig messageConfig = dimensionConfig.messages.achievement != null ? dimensionConfig.messages.achievement : genericConfig.messages.achievement;

        GetEventAuthorNameAndAvatar getEventAuthorNameAndAvatar = new GetEventAuthorNameAndAvatar(player).invoke();
        String authorName = getEventAuthorNameAndAvatar.getAuthorName();
        String avatarUrl = getEventAuthorNameAndAvatar.getAvatarUrl();

        DiscordClient.getInstance().broadcast(
                new Message()
                        .setAuthor(authorName)
                        .setAvatarUrl(avatarUrl)
                        .setMessage(messageConfig)
                        .setArguments(arguments),
                dimensionConfig.relayAchievements.getChannels(
                        genericConfig.relayAchievements.getChannels(
                                dimensionConfig.discordChannel.getChannels(
                                        genericConfig.discordChannel
                                )
                        )
                )
        );

    }
}
