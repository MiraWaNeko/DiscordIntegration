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
import chikachi.discord.core.DiscordIntegrationLogger;
import chikachi.discord.core.config.linking.LinkingWrapper;
import chikachi.discord.core.config.types.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class Configuration {
    private static File directory;

    private static File configFile;
    private static File linkingFile;

    private static ConfigWrapper config;
    private static LinkingWrapper linking;

    public static void onPreInit(String directoryPath) {
        directory = new File(directoryPath);

        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();

        configFile = new File(directory, CoreConstants.MODID + ".json");
        linkingFile = new File(directory, CoreConstants.MODID + "_links.json");

        loadConfig();
        loadLinking();
    }

    private static Gson createGson() {
        return new GsonBuilder()
            .registerTypeAdapter(ChannelConfigType.class, new ChannelConfigTypeAdapter())
            .registerTypeAdapter(DimensionConfigType.class, new DimensionConfigTypeAdapter())
            .registerTypeAdapter(MessageConfig.class, new MessageConfigAdapter())
            .registerTypeAdapter(Pattern.class, new PatternAdapter())
            .setVersion(3.0)
            .serializeNulls()
            .setPrettyPrinting()
            .create();
    }

    public static void loadConfig() {
        if (configFile == null) {
            return;
        }

        Gson gson = createGson();

        if (!configFile.exists()) {
            config = new ConfigWrapper();
            config.fillFields();
            saveConfig();
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
                    DiscordIntegrationLogger.Log("Config had invalid syntax - Please check it using a JSON tool ( https://jsonlint.com/ ) or make sure it have the right content", true);
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
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    public static void saveConfig() {
        saveToFile(configFile, config);
    }

    public static void loadLinking() {
        if (linkingFile == null) {
            return;
        }

        Gson gson = createGson();

        if (!linkingFile.exists()) {
            linking = new LinkingWrapper();
            saveLinking();
        } else {
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(linkingFile);
                linking = gson.fromJson(fileReader, LinkingWrapper.class);
                if (linking == null) {
                    linking = new LinkingWrapper();
                }
            } catch (Exception e) {
                if (e instanceof JsonSyntaxException) {
                    DiscordIntegrationLogger.Log("Linking file is corrupt", true);
                }

                e.printStackTrace();

                if (linking == null) {
                    linking = new LinkingWrapper();
                }
            } finally {
                if (fileReader != null) {
                    try {
                        fileReader.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    public static void saveLinking() {
        saveToFile(linkingFile, linking);
    }

    private static void saveToFile(File file, Object data) {
        Gson gson = createGson();

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(data));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveClean() {
        Gson gson = createGson();

        try {
            FileWriter writer = new FileWriter(new File(directory, CoreConstants.MODID + "_clean.json"));
            ConfigWrapper cleanConfig = new ConfigWrapper();
            cleanConfig.fillFields();
            writer.write(gson.toJson(cleanConfig));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConfigWrapper getConfig() {
        return config;
    }

    public static LinkingWrapper getLinking() {
        return linking;
    }
}
