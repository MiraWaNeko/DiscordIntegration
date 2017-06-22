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

package chikachi.discord.core.config;

import chikachi.discord.core.CoreConstants;
import chikachi.discord.core.CoreLogger;
import chikachi.discord.core.config.types.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Configuration {
    private static File configFile;
    private static ConfigWrapper config;

    public static void onPreInit(String directoryPath) {
        File directory = new File(directoryPath);

        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();

        configFile = new File(directory, CoreConstants.MODID + ".json");

        load();
    }

    public static void load() {
        if (configFile == null) {
            return;
        }

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(ChannelConfigType.class, new ChannelConfigTypeAdapter())
            .registerTypeAdapter(DimensionConfigType.class, new DimensionConfigTypeAdapter())
            .registerTypeAdapter(MessageConfig.class, new MessageConfigAdapter())
            .setVersion(3.0)
            .serializeNulls()
            .setPrettyPrinting()
            .create();

        if (!configFile.exists()) {
            config = new ConfigWrapper();
            config.fillFields();
            save();
        } else {
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(configFile);
                config = gson.fromJson(fileReader, ConfigWrapper.class);
                if (config == null) {
                    config = new ConfigWrapper();
                }
                config.fillFields();
            } catch (Exception e) {
                if (e instanceof JsonSyntaxException) {
                    CoreLogger.Log("Config had invalid syntax - Please check it using a JSON tool ( https://jsonlint.com/ ) or make sure it have the right content", true);
                }

                e.printStackTrace();

                if (config == null) {
                    config = new ConfigWrapper();
                    config.fillFields();
                }
            } finally {
                if (fileReader != null) {
                    try {
                        fileReader.close();
                    } catch (IOException ignored) {}
                }
            }
        }

        CoreLogger.Log(gson.toJson(config));
    }

    public static void save() {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(ChannelConfigType.class, new ChannelConfigTypeAdapter())
            .registerTypeAdapter(DimensionConfigType.class, new DimensionConfigTypeAdapter())
            .registerTypeAdapter(MessageConfig.class, new MessageConfigAdapter())
            .setVersion(3.0)
            .setPrettyPrinting()
            .create();

        try {
            FileWriter writer = new FileWriter(configFile);
            writer.write(gson.toJson(config));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConfigWrapper getConfig() {
        return config;
    }
}
