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

package net.discordintegration.core.config.linking;

import com.google.gson.annotations.Since;
import net.discordintegration.core.config.Configuration;

import java.util.*;

public class LinkingWrapper {
    @Since(3.0)
    private HashMap<UUID, Long> linked = new HashMap<>();
    @Since(3.0)
    private List<DiscordToMinecraftLinkingRequest> requests = new ArrayList<>();
    @Since(3.0)
    private List<MinecraftToDiscordLinkingRequest> requestsMinecraftToDiscord = new ArrayList<>();

    void addRequest(DiscordToMinecraftLinkingRequest request) {
        Configuration.getLinking().requests.add(request);
        Configuration.saveLinking();
    }

    void addRequest(MinecraftToDiscordLinkingRequest request) {
        Configuration.getLinking().requestsMinecraftToDiscord.add(request);
        Configuration.saveLinking();
    }

    void executeRequest(DiscordToMinecraftLinkingRequest request, UUID minecraftUUID) {
        LinkingWrapper linkingWrapper = Configuration.getLinking();
        linkingWrapper.linked.put(minecraftUUID, request.getDiscordId());
        linkingWrapper.requests.remove(request);
        Configuration.saveLinking();
    }

    void executeRequest(MinecraftToDiscordLinkingRequest request, long discordID) {
        LinkingWrapper linkingWrapper = Configuration.getLinking();
        linkingWrapper.linked.put(request.getPlayerUUID(), discordID);
        linkingWrapper.requestsMinecraftToDiscord.remove(request);
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

    public DiscordToMinecraftLinkingRequest getRequest(long discordUserId) {
        Optional<DiscordToMinecraftLinkingRequest> request = this.requests
            .stream()
            .filter(linkingRequest -> linkingRequest.getDiscordId() == discordUserId)
            .findFirst();

        return request.orElseGet(() -> DiscordToMinecraftLinkingRequest.create(discordUserId));
    }

    public MinecraftToDiscordLinkingRequest getRequest(UUID playerUUID) {
        Optional<MinecraftToDiscordLinkingRequest> request = this.requestsMinecraftToDiscord
            .stream()
            .filter(linkingRequest -> linkingRequest.getPlayerUUID().equals(playerUUID))
            .findFirst();

        return request.orElseGet(() -> MinecraftToDiscordLinkingRequest.create(playerUUID));
    }

    public Optional<DiscordToMinecraftLinkingRequest> getRequestByCode(String code) {
        return this.requests
            .stream()
            .filter(linkingRequest -> linkingRequest.getCode().equalsIgnoreCase(code))
            .findFirst();
    }

    public Optional<MinecraftToDiscordLinkingRequest> getMCRequestByCode(String code) {
        return this.requestsMinecraftToDiscord
            .stream()
            .filter(linkingRequest -> linkingRequest.getCode().equalsIgnoreCase(code))
            .findFirst();
    }

    public void removeLink(UUID minecraftUUID) {
        this.linked.remove(minecraftUUID);
        Configuration.saveLinking();
    }

    public void removeLink(long discordID) {
        this.linked.entrySet().stream()
            .filter(entry -> entry.getValue() == discordID)
            .forEach(entry -> this.linked.remove(entry.getKey()));
        Configuration.saveLinking();
    }
}
