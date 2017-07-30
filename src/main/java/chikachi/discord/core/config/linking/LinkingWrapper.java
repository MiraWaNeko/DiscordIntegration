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

import java.util.*;

public class LinkingWrapper {
    @Since(3.0)
    private HashMap<UUID, Long> linked = new HashMap<>();
    @Since(3.0)
    private List<LinkingRequest> requests = new ArrayList<>();

    void addRequest(LinkingRequest request) {
        Configuration.getLinking().requests.add(request);
        Configuration.saveLinking();
    }

    void executeRequest(LinkingRequest request, UUID minecraftUUID) {
        LinkingWrapper linkingWrapper = Configuration.getLinking();
        linkingWrapper.linked.put(minecraftUUID, request.getDiscordId());
        linkingWrapper.requests.remove(request);
        Configuration.saveLinking();
    }

    public boolean isLinked(UUID minecraftId, long discordId) {
        return this.linked.containsKey(minecraftId) && this.linked.get(minecraftId) == discordId;
    }

    public UUID getMinecraftId(long discordId) {
        if (this.linked.containsValue(discordId)) {
            Optional<Map.Entry<UUID, Long>> link = this.linked
                .entrySet()
                .stream()
                .filter(uuidLongEntry -> uuidLongEntry.getValue() == discordId)
                .findFirst();

            if (link.isPresent()) {
                return link.get().getKey();
            }
        }

        return null;
    }

    public Long getDiscordId(UUID minecraftId) {
        if (this.linked.containsKey(minecraftId)) {
            return this.linked.get(minecraftId);
        }

        return null;
    }

    public LinkingRequest getRequest(long discordUserId) {
        Optional<LinkingRequest> request = this.requests
            .stream()
            .filter(linkingRequest -> linkingRequest.getDiscordId() == discordUserId)
            .findFirst();

        return request.orElseGet(() -> LinkingRequest.create(discordUserId));
    }

    public Optional<LinkingRequest> getRequestByCode(String code) {
        return this.requests
            .stream()
            .filter(linkingRequest -> linkingRequest.getCode().equalsIgnoreCase(code))
            .findFirst();
    }

    public void removeLink(UUID minecraftUUID) {
        this.linked.remove(minecraftUUID);
        Configuration.saveLinking();
    }
}
