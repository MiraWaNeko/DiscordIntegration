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

package chikachi.discord.core;

import chikachi.discord.core.config.Configuration;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.requests.Request;
import net.dv8tion.jda.core.requests.Response;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.Route;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebhookMessage {
    public String content;
    public String username;
    public String avatar_url;

    boolean queue(JDA jda, Long channelId) {
        if (this.content == null || this.content.trim().length() == 0) {
            return false;
        }

        String webhook = Configuration.getConfig().discord.channels.channels.get(channelId).webhook.trim();
        Matcher matcher = Pattern.compile("https://(ptb\\.|)discordapp\\.com/api/webhooks/([0-9]+)/([a-zA-Z0-9\\-_]+)").matcher(webhook);
        if (matcher.matches()) {
            String webhookId = matcher.group(2);
            String webhookToken = matcher.group(3);

            Route.CompiledRoute route = Route.Webhooks.EXECUTE_WEBHOOK.compile(webhookId, webhookToken);

            JSONObject json = new JSONObject();
            if (this.username != null) {
                json.put("username", this.username);
            }
            if (this.avatar_url != null) {
                json.put("avatar_url", this.avatar_url);
            }
            json.put("content", Patterns.minecraftToDiscord(this.content));

            new RestAction<Void>(jda, route, json) {
                protected void handleResponse(Response response, Request<Void> request) {
                    try {
                        if (response.isOk()) {
                            request.onSuccess(null);
                        } else {
                            request.onFailure(response);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }.queue();
            return true;
        }
        return false;
    }
}
