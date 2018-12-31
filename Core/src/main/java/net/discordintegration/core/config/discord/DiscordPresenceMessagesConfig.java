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

public class DiscordPresenceMessagesConfig {
    private transient static final String PRESENCE_NO_PLAYER_ONLINE = "alone.";
    private transient static final String PRESENCE_ONE_PLAYER_ONLINE = "with {USER}.";
    private transient static final String PRESENCE_MORE_PLAYERS_ONLINE = "with {COUNT} players.";

    @Since(3.0)
    public String noPlayerOnline = null;
    @Since(3.0)
    public String onePlayerOnline = null;
    @Since(3.0)
    public String morePlayersOnline = null;


    public void fillFields() {
        if (this.noPlayerOnline == null || this.noPlayerOnline.trim().length() == 0) {
            this.noPlayerOnline = PRESENCE_NO_PLAYER_ONLINE;
        }
        if (this.onePlayerOnline == null || this.onePlayerOnline.trim().length() == 0) {
            this.onePlayerOnline = PRESENCE_ONE_PLAYER_ONLINE;
        }
        if (this.morePlayersOnline == null || this.morePlayersOnline.trim().length() == 0) {
            this.morePlayersOnline = PRESENCE_MORE_PLAYERS_ONLINE;
        }
    }
}
