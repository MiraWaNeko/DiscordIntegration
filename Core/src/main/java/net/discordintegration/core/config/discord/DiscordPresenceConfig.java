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

public class DiscordPresenceConfig {
    @Since(3.0)
    public boolean enabled = true;
    @Since(3.0)
    public DiscordPresenceMessagesConfig messages;


    public void fillFields() {
        if (messages == null) {
            messages = new DiscordPresenceMessagesConfig();
        }
        messages.fillFields();
    }
}
