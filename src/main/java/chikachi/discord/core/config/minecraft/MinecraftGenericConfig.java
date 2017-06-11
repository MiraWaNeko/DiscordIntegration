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

public class MinecraftGenericConfig extends AbstractRelay {
    @Since(3.0)
    public String chatPrefix = "";
    @Since(3.0)
    public ChannelConfigType discordChannel = new ChannelConfigType();
    @Since(3.0)
    public boolean ignoreFakePlayerChat = true;
    @Since(3.0)
    public ChannelConfigType relayServerStart = new ChannelConfigType();
    @Since(3.0)
    public ChannelConfigType relayServerStop = new ChannelConfigType();
    @Since(3.0)
    public ChannelConfigType relayServerCrash = new ChannelConfigType();

    public void fillFields() {
        super.fillFields();

        if (this.chatPrefix == null) {
            this.chatPrefix = "";
        }

        if (this.relayServerStart == null) {
            this.relayServerStart = new ChannelConfigType();
        }

        if (this.relayServerStop == null) {
            this.relayServerStop = new ChannelConfigType();
        }

        if (this.relayServerCrash == null) {
            this.relayServerCrash = new ChannelConfigType();
        }
    }
}
