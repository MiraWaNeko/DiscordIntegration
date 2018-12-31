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

package net.discordintegration.core.config.discord;

import com.google.gson.annotations.Since;
import net.discordintegration.core.config.types.MessageConfig;

public class DiscordMessagesConfig {
    private transient static final String CHAT_MESSAGE_NORMAL = "[{USER}] {MESSAGE}";

    @Since(3.0)
    public MessageConfig chatMessage = null;

    public void fillFields() {
        if (this.chatMessage == null) {
            this.chatMessage = new MessageConfig(CHAT_MESSAGE_NORMAL);
        }
        if (this.chatMessage.normal == null || this.chatMessage.normal.trim().length() == 0) {
            this.chatMessage.normal = CHAT_MESSAGE_NORMAL;
        }
    }
}
