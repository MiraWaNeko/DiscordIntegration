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

package chikachi.discord.core.config.discord;

import com.google.gson.annotations.Since;

public class DiscordConfig {
    @Since(3.0)
    public String token = "";
    @Since(3.0)
    public Object ignoresBots = true;
    @Since(3.0)
    public DiscordMainChannelConfig channels = new DiscordMainChannelConfig();

    public void fillFields() {
        if (this.token == null) {
            this.token = "";
        }

        if (this.ignoresBots == null) {
            this.ignoresBots = true;
        }

        if (this.channels == null) {
            this.channels = new DiscordMainChannelConfig();
        }
        this.channels.fillFields();
    }

    public boolean isIgnoringBots() {
        return ignoresBots instanceof Boolean && (Boolean) ignoresBots;
    }
}
