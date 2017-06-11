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

import chikachi.discord.core.config.MessageConfig;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private final String author;
    private final String avatar_url;
    private final MessageConfig message;
    private final HashMap<String, String> arguments;

    public Message(MessageConfig message) {
        this(message, new HashMap<>());
    }

    public Message(MessageConfig message, HashMap<String, String> arguments) {
        this(null, message, arguments);
    }

    public Message(String author, MessageConfig message) {
        this(author, message, new HashMap<>());
    }

    public Message(String author, MessageConfig message, HashMap<String, String> arguments) {
        this(author, null, message, arguments);
    }

    public Message(String author, String avatar_url, MessageConfig message) {
        this(author, avatar_url, message, new HashMap<>());
    }

    public Message(String author, String avatar_url, MessageConfig message, HashMap<String, String> arguments) {
        this.author = author;
        this.avatar_url = avatar_url;
        this.message = message;

        if (arguments == null) {
            this.arguments = new HashMap<>();
        } else {
            this.arguments = arguments;
        }
        this.arguments.put("USER", getAuthor());
    }

    public WebhookMessage toWebhook() {
        WebhookMessage webhookMessage = new WebhookMessage();
        webhookMessage.content = formatText(message.webhook);
        webhookMessage.username = this.author;
        webhookMessage.avatar_url = this.avatar_url;
        return webhookMessage;
    }

    public String getAuthor() {
        return this.author != null ? this.author : "Server";
    }

    private String formatText(String text) {
        String message = text;
        for (Map.Entry<String, String> entry : this.arguments.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }

    public String getUnformattedText() {
        return this.message.normal;
    }

    public String getFormattedText() {
        return formatText(message.normal);
    }
}
