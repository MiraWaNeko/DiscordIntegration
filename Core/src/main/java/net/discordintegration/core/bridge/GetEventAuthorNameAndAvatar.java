package net.discordintegration.core.bridge;

import net.discordintegration.core.CoreUtils;
import net.discordintegration.core.DiscordClient;
import net.discordintegration.core.Proxy;
import net.discordintegration.core.config.Configuration;
import net.dv8tion.jda.core.entities.User;

import java.util.UUID;

class GetEventAuthorNameAndAvatar {
    private Long discordId;
    private String authorName;
    private String avatarUrl;


    GetEventAuthorNameAndAvatar(UUID playerID) {
        if (playerID == null) {
            return;
        }
        this.discordId = Configuration.getLinking().getDiscordId(playerID);
        this.authorName = Proxy.getBridge().getMinecraft().getPlayerNameByUUID(playerID);
        this.avatarUrl = CoreUtils.getAvatarUrl(this.authorName);
    }

    String getAuthorName() {
        return this.authorName;
    }

    String getAvatarUrl() {
        return this.avatarUrl;
    }

    GetEventAuthorNameAndAvatar invoke() {
        if (this.discordId != null) {
            User discordUser = DiscordClient.getInstance().getUser(this.discordId);
            if (discordUser != null) {
                this.authorName = discordUser.getName();
                this.avatarUrl = discordUser.getAvatarUrl();
            }
        }
        return this;
    }
}