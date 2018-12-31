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

package net.discordintegration.core.config.imc;

import com.google.gson.annotations.Since;

import java.util.ArrayList;
import java.util.List;

public class IMCConfig {
    @Since(3.0)
    public boolean enabled = true;
    @Since(3.0)
    public String mode = "whitelist";
    @Since(3.0)
    public List<String> list = new ArrayList<>();

    public void fillFields() {
        if (this.mode == null) {
            this.mode = "whitelist";
        }

        if (this.mode.equalsIgnoreCase("b") || this.mode.equalsIgnoreCase("bl") || this.mode.equalsIgnoreCase("black") || this.mode.equalsIgnoreCase("blacklist")) {
            this.mode = "blacklist";
        } else {
            this.mode = "whitelist";
        }

        if (this.list == null) {
            this.list = new ArrayList<>();
        }
    }

    public boolean isAllowed(String modId) {
        if (this.mode.equalsIgnoreCase("whitelist")) {
            return this.list.contains(modId);
        } else {
            return !this.list.contains(modId);
        }
    }

    public boolean isWhitelist() {
        return this.mode.equalsIgnoreCase("whitelist");
    }

    public boolean isBlacklist() {
        return this.mode.equalsIgnoreCase("blacklist");
    }
}
