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
import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;

public class WebhookMessage {
    public String content;
    public String username;
    public String avatar_url;

    public boolean send(Long channelId) {
        try {
            URL url = new URL(Configuration.getConfig().discord.channels.channels.get(channelId).webhook.trim());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", CoreConstants.MODNAME + " " + CoreConstants.VERSION);
            connection.setDoOutput(true);

            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());

            Gson gson = new Gson();
            writer.writeBytes(gson.toJson(this));
            writer.flush();
            writer.close();

            int responseCode = connection.getResponseCode();
            return !(responseCode < 200 || 299 < responseCode);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            CoreLogger.Log(e.getMessage());
        }

        return false;
    }
}
