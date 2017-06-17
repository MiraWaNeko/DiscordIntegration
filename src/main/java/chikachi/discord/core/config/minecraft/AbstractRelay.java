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

import chikachi.discord.core.config.types.ChannelConfigType;
import com.google.gson.annotations.Since;

public abstract class AbstractRelay {
    @Since(3.0)
    public ChannelConfigType relayAchievements = new ChannelConfigType();
    @Since(3.0)
    public ChannelConfigType relayChat = new ChannelConfigType();
    @Since(3.0)
    public ChannelConfigType relayCommands = new ChannelConfigType();
    @Since(3.0)
    public ChannelConfigType relayPlayerJoin = new ChannelConfigType();
    @Since(3.0)
    public ChannelConfigType relayPlayerLeave = new ChannelConfigType();
    @Since(3.0)
    public ChannelConfigType relayPlayerDeath = new ChannelConfigType();

    public void fillFields() {
        if (this.relayAchievements == null) {
            this.relayAchievements = new ChannelConfigType();
        }

        if (this.relayChat == null) {
            this.relayChat = new ChannelConfigType();
        }

        if (this.relayCommands == null) {
            this.relayCommands = new ChannelConfigType();
        }

        if (this.relayPlayerJoin == null) {
            this.relayPlayerJoin = new ChannelConfigType();
        }

        if (this.relayPlayerLeave == null) {
            this.relayPlayerLeave = new ChannelConfigType();
        }

        if (this.relayPlayerDeath == null) {
            this.relayPlayerDeath = new ChannelConfigType();
        }
    }
}
