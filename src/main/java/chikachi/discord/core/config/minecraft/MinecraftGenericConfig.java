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

import java.util.regex.Pattern;

public class MinecraftGenericConfig extends MinecraftDimensionConfig {
    @Since(3.0)
    public boolean ignoreFakePlayerChat = true;
    @Since(3.0)
    public boolean relaySayCommand = true;
    @Since(3.0)
    public boolean relayMeCommand = true;
    @Since(3.0)
    public boolean canMentionEveryone = false;
    @Since(3.0)
    public boolean canMentionHere = false;
    @Since(3.0)
    public Pattern[] messageIgnoreRegex = new Pattern[0];
    @Since(3.0)
    public ChannelConfigType relayServerStart = new ChannelConfigType();
    @Since(3.0)
    public ChannelConfigType relayServerStop = new ChannelConfigType();
    @Since(3.0)
    public ChannelConfigType relayServerCrash = new ChannelConfigType();

    public void fillFields() {
        super.fillFields();

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

    public boolean isMessageIgnored(String message) {
        if (this.messageIgnoreRegex.length > 0) {
            Pattern[] ignoreRegex = this.messageIgnoreRegex;

            for (Pattern anIgnoreRegex : ignoreRegex) {
                if (anIgnoreRegex != null) {
                    if (anIgnoreRegex.matcher(message).find()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
