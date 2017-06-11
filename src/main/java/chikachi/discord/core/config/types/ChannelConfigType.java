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

package chikachi.discord.core.config.types;

import java.util.ArrayList;

public class ChannelConfigType {
    private ArrayList<Long> channels;
    private boolean isDefault;
    private boolean isDisabled;

    public ChannelConfigType() {
        this(false);
    }

    public ChannelConfigType(boolean isDisabled) {
        this(!isDisabled, isDisabled);
    }

    public ChannelConfigType(boolean isDefault, boolean isDisabled) {
        this(new ArrayList<>(), isDefault, isDisabled);
    }

    public ChannelConfigType(ArrayList<Long> channels, boolean isDefault, boolean isDisabled) {
        this.channels = channels;
        this.isDefault = isDefault;
        this.isDisabled = isDisabled;
    }

    public ChannelConfigType addChannel(Long channel) {
        this.channels.add(channel);
        return this;
    }

    public ArrayList<Long> getChannels() {
        return isDisabled() ? null : (isDefault() ? null : channels);
    }

    public ChannelConfigType setChannels(ArrayList<Long> channels) {
        this.channels = channels;
        return this;
    }

    public ArrayList<Long> getChannels(ArrayList<Long> defaultChannels) {
        return isDisabled() ? null : (isDefault() ? defaultChannels : channels);
    }

    public ArrayList<Long> getChannels(ChannelConfigType defaultChannels) {
        return getChannels(defaultChannels.channels);
    }

    public boolean isDefault() {
        return isDefault;
    }

    public ChannelConfigType setDefault(boolean aDefault) {
        isDefault = aDefault;
        return this;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public ChannelConfigType setDisabled(boolean disabled) {
        isDisabled = disabled;
        return this;
    }
}
