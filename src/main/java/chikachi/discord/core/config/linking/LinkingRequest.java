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

package chikachi.discord.core.config.linking;

import chikachi.discord.core.config.Configuration;
import com.google.gson.annotations.Since;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class LinkingRequest {
    private static Random rand = new Random();

    @Since(3.0)
    private long discordId;
    @Since(3.0)
    private String code;
    @Since(3.0)
    private long expires;

    private LinkingRequest() {
    }

    static LinkingRequest create(long discordId) {
        LinkingRequest request = new LinkingRequest();
        request.discordId = discordId;
        request.generateCode();
        Configuration.getLinking().addRequest(request);
        return request;
    }

    public void generateCode() {
        this.code = String.format("%04d", rand.nextInt(10000));
        this.expires = new Date(System.currentTimeMillis() + 5 * 60 * 1000).getTime();
    }

    public long getDiscordId() {
        return discordId;
    }

    public String getCode() {
        return code;
    }

    public boolean hasExpired() {
        return this.expires <= new Date().getTime();
    }

    public String expiresIn() {
        int seconds = (int) Math.max(0, Math.floorDiv(this.expires - new Date().getTime(), (int) 1e3));
        int minutes = Math.floorDiv(seconds, 60);
        seconds -= minutes * 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void executeLinking(UUID minecraftUUID) {
        Configuration.getLinking().executeRequest(this, minecraftUUID);
    }
}
