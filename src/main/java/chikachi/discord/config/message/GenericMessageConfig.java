/**
 * Copyright (C) 2016 Chikachi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package chikachi.discord.config.message;

import chikachi.discord.DiscordClient;

public class GenericMessageConfig extends BaseMessageConfig {
    public GenericMessageConfig(String name, boolean enabled, String message) {
        super(name, enabled, message);
    }

    private void doSendMessage(String message) {
        DiscordClient.getInstance().sendMessage(
                message
        );
    }

    public void sendMessage() {
        if (!this.isEnabled()) return;

        doSendMessage(this.getMessage());
    }

    public void sendMessage(String username) {
        if (!this.isEnabled()) return;

        doSendMessage(
                this.getMessage()
                        .replace("%USER%", username)
        );
    }

    public void sendMessage(String username, String message) {
        if (!this.isEnabled()) return;

        doSendMessage(
                this.getMessage()
                        .replace("%USER%", username)
                        .replace("%MESSAGE%", message)
        );
    }
}
