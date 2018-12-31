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

import java.util.ArrayList;

public class DiscordChannelConfig extends DiscordChannelGenericConfig {
    @Since(3.0)
    public String webhook;
    @Since(3.0)
    public Boolean updateDescription;
    @Since(3.0)
    public ArrayList<String> descriptions = new ArrayList<>();

    @Override
    public void fillFields() {
        if (this.webhook == null) {
            this.webhook = "";
        }

        if (this.updateDescription == null) {
            this.updateDescription = false;
        }

        if (this.descriptions == null) {
            descriptions = new ArrayList<>();
        }
        super.fillFields();
    }
}
