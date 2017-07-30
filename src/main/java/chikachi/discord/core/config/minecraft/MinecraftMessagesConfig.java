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

package chikachi.discord.core.config.minecraft;

import chikachi.discord.core.config.types.MessageConfig;
import com.google.gson.annotations.Since;

public class MinecraftMessagesConfig {
    private transient static final String CHAT_MESSAGE_NORMAL = "**[{USER}]** {MESSAGE}";
    private transient static final String CHAT_MESSAGE_WEBHOOK = "{MESSAGE}";

    private transient static final String COMMAND_NORMAL = "**[{USER}]** executed **{COMMAND} {ARGUMENTS}**";
    private transient static final String COMMAND_WEBHOOK = "*executed **{COMMAND} {ARGUMENTS}***";

    private transient static final String PLAYER_JOIN_NORMAL = "**{USER}** just joined the server!";
    private transient static final String PLAYER_JOIN_WEBHOOK = "*Joined the server!*";

    private transient static final String PLAYER_LEAVE_NORMAL = "**{USER}** just left the server!";
    private transient static final String PLAYER_LEAVE_WEBHOOK = "*Left the server!*";

    private transient static final String PLAYER_DEATH_NORMAL = "**{USER}** just died due to {REASON}!";
    private transient static final String PLAYER_DEATH_WEBHOOK = "*{REASON}*";

    private transient static final String ACHIEVEMENT_NORMAL = "**{USER}** just gained the achievement **{ACHIEVEMENT}**!\n*{DESCRIPTION}*";
    private transient static final String ACHIEVEMENT_WEBHOOK = "*Gained the achievement **{ACHIEVEMENT}**!\n{DESCRIPTION}*";

    private transient static final String SERVER_START = "Server started!";
    private transient static final String SERVER_STOP = "Server stopped!";
    private transient static final String SERVER_CRASH = "Server crash detected!";

    @Since(3.0)
    public MessageConfig chatMessage = null;
    @Since(3.0)
    public MessageConfig command = null;
    @Since(3.0)
    public MessageConfig playerJoin = null;
    @Since(3.0)
    public MessageConfig playerLeave = null;
    @Since(3.0)
    public MessageConfig playerDeath = null;
    @Since(3.0)
    public MessageConfig achievement = null;
    @Since(3.0)
    public MessageConfig serverStart = null;
    @Since(3.0)
    public MessageConfig serverStop = null;
    @Since(3.0)
    public MessageConfig serverCrash = null;

    public void fillFields() {
        if (this.chatMessage == null) {
            this.chatMessage = new MessageConfig(CHAT_MESSAGE_NORMAL, CHAT_MESSAGE_WEBHOOK);
        }
        if (this.chatMessage.normal == null || this.chatMessage.normal.trim().length() == 0) {
            this.chatMessage.normal = CHAT_MESSAGE_NORMAL;
        }
        if (this.chatMessage.webhook == null || this.chatMessage.webhook.trim().length() == 0) {
            this.chatMessage.webhook = CHAT_MESSAGE_WEBHOOK;
        }

        if (this.command == null) {
            this.command = new MessageConfig(COMMAND_NORMAL, COMMAND_WEBHOOK);
        }
        if (this.command.normal == null || this.chatMessage.normal.trim().length() == 0) {
            this.command.normal = COMMAND_NORMAL;
        }
        if (this.command.webhook == null || this.chatMessage.webhook.trim().length() == 0) {
            this.command.webhook = COMMAND_WEBHOOK;
        }

        if (this.playerJoin == null) {
            this.playerJoin = new MessageConfig(PLAYER_JOIN_NORMAL, PLAYER_JOIN_WEBHOOK);
        }
        if (this.playerJoin.normal == null || this.playerJoin.normal.trim().length() == 0) {
            this.playerJoin.normal = PLAYER_JOIN_NORMAL;
        }
        if (this.playerJoin.webhook == null || this.playerJoin.webhook.trim().length() == 0) {
            this.playerJoin.webhook = PLAYER_JOIN_WEBHOOK;
        }

        if (this.playerLeave == null) {
            this.playerLeave = new MessageConfig(PLAYER_LEAVE_NORMAL, PLAYER_LEAVE_WEBHOOK);
        }
        if (this.playerLeave.normal == null || this.playerLeave.normal.trim().length() == 0) {
            this.playerLeave.normal = PLAYER_LEAVE_NORMAL;
        }
        if (this.playerLeave.webhook == null || this.playerLeave.webhook.trim().length() == 0) {
            this.playerLeave.webhook = PLAYER_LEAVE_WEBHOOK;
        }

        if (this.playerDeath == null) {
            this.playerDeath = new MessageConfig(PLAYER_DEATH_NORMAL, PLAYER_DEATH_WEBHOOK);
        }
        if (this.playerDeath.normal == null || this.playerDeath.normal.trim().length() == 0) {
            this.playerDeath.normal = PLAYER_LEAVE_NORMAL;
        }
        if (this.playerDeath.webhook == null || this.playerDeath.webhook.trim().length() == 0) {
            this.playerDeath.webhook = PLAYER_LEAVE_WEBHOOK;
        }

        if (this.achievement == null) {
            this.achievement = new MessageConfig(ACHIEVEMENT_NORMAL, ACHIEVEMENT_WEBHOOK);
        }
        if (this.achievement.normal == null || this.achievement.normal.trim().length() == 0) {
            this.achievement.normal = ACHIEVEMENT_NORMAL;
        }
        if (this.achievement.webhook == null || this.achievement.webhook.trim().length() == 0) {
            this.achievement.webhook = ACHIEVEMENT_WEBHOOK;
        }

        if (this.serverStart == null) {
            this.serverStart = new MessageConfig(SERVER_START);
        }

        if (this.serverStop == null) {
            this.serverStop = new MessageConfig(SERVER_STOP);
        }

        if (this.serverCrash == null) {
            this.serverCrash = new MessageConfig(SERVER_CRASH);
        }
    }
}
