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

package chikachi.discord.config;

import chikachi.discord.DiscordIntegration;
import chikachi.discord.Constants;
import chikachi.discord.command.discord.CustomCommandConfig;
import chikachi.discord.command.discord.OnlineCommandConfig;
import chikachi.discord.command.discord.TpsCommandConfig;
import chikachi.discord.command.discord.UnstuckCommandConfig;
import chikachi.discord.config.message.AchievementMessageConfig;
import chikachi.discord.config.message.DiscordChatMessageConfig;
import chikachi.discord.config.message.GenericMessageConfig;
import chikachi.discord.config.message.MinecraftChatMessageConfig;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private static File config;
    private static String token = "";
    private static String channel = "";
    private static String commandPrefix = "!";

    private static OnlineCommandConfig commandOnline = new OnlineCommandConfig();
    private static TpsCommandConfig commandTps = new TpsCommandConfig();
    private static UnstuckCommandConfig commandUnstuck = new UnstuckCommandConfig();
    private static List<CustomCommandConfig> customCommands = new ArrayList<>();

    private static boolean ignoringBots = false;
    private static boolean experimentalFakePlayers = false;

    private static MinecraftChatMessageConfig discordChat = new MinecraftChatMessageConfig(true, "<__%USER%__> %MESSAGE%");
    private static GenericMessageConfig discordDeath = new GenericMessageConfig("death", true, "__%USER%__ %MESSAGE%");
    private static AchievementMessageConfig discordAchievement = new AchievementMessageConfig(true, "Congrats to __%USER%__ for earning the achievement **%ACHIEVEMENT%** (%DESCRIPTION%)");
    private static GenericMessageConfig discordJoin = new GenericMessageConfig("join", true, "__%USER%__ has joined the server!");
    private static GenericMessageConfig discordLeave = new GenericMessageConfig("leave", true, "__%USER%__ left the server!");
    private static GenericMessageConfig discordStartup = new GenericMessageConfig("startup", false, "**Server started**");
    private static GenericMessageConfig discordShutdown = new GenericMessageConfig("shutdown", false, "**Server shutdown**");
    private static GenericMessageConfig discordCrash = new GenericMessageConfig("crash", true, "**Server crash detected**");

    private static DiscordChatMessageConfig minecraftChat = new DiscordChatMessageConfig(true, "<__%USER%__> %MESSAGE%");

    public static void onPreInit(FMLPreInitializationEvent event) {
        File directory = new File(event.getModConfigurationDirectory().getAbsolutePath() + File.separator + "Chikachi");
        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();

        config = new File(directory, Constants.MODID + ".json");

        File oldConfig = new File(directory, "ChikachiDiscord.json");
        if (oldConfig.exists()) {
            //noinspection ResultOfMethodCallIgnored
            oldConfig.renameTo(config);
        }

        load();
    }

    public static void load() {
        if (config == null) {
            return;
        }

        if (!config.exists()) {
            try {
                JsonWriter writer = new JsonWriter(new FileWriter(config));

                writer.beginObject();

                writer.name("discord");
                writer.beginObject();
                writer.name("token");
                writer.value(token);
                writer.name("channel");
                writer.value(channel);

                writer.name("ignoreBots");
                writer.value(ignoringBots);

                writer.name("commands");
                writer.beginObject();
                writer.name("prefix");
                writer.value(commandPrefix);
                commandOnline.write(writer);
                commandTps.write(writer);
                commandUnstuck.write(writer);
                writer.name("custom");
                writer.beginArray();
                writer.endArray();
                writer.endObject();
                writer.endObject();

                writer.name("messages");
                writer.beginObject();
                writer.name("discord");
                writer.beginObject();
                discordChat.write(writer);
                discordDeath.write(writer);
                discordAchievement.write(writer);
                discordJoin.write(writer);
                discordLeave.write(writer);
                discordStartup.write(writer);
                discordShutdown.write(writer);
                writer.endObject();

                writer.name("minecraft");
                writer.beginObject();
                minecraftChat.write(writer);
                writer.endObject();
                writer.endObject();

                writer.name("experimental");
                writer.beginObject();
                writer.name("fakePlayers");
                writer.value(experimentalFakePlayers);
                writer.endObject();

                writer.endObject();

                writer.close();
            } catch (IOException e) {
                DiscordIntegration.Log("Error generating default config file", true);
                e.printStackTrace();
            }
        } else {
            try {
                JsonReader reader = new JsonReader(new FileReader(config));
                String name;

                reader.beginObject();
                while (reader.hasNext()) {
                    name = reader.nextName();
                    if (name.equalsIgnoreCase("discord") && reader.peek() == JsonToken.BEGIN_OBJECT) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            name = reader.nextName();
                            if (name.equalsIgnoreCase("token") && reader.peek() == JsonToken.STRING) {
                                token = reader.nextString();
                            } else if (name.equalsIgnoreCase("channel") && reader.peek() == JsonToken.STRING) {
                                channel = reader.nextString();
                            } else if (name.equalsIgnoreCase("ignoreBots") && reader.peek() == JsonToken.BOOLEAN) {
                                ignoringBots = reader.nextBoolean();
                            } else if (name.equalsIgnoreCase("commands") && reader.peek() == JsonToken.BEGIN_OBJECT) {
                                reader.beginObject();
                                while (reader.hasNext()) {
                                    name = reader.nextName();
                                    if (name.equalsIgnoreCase("prefix") && reader.peek() == JsonToken.STRING) {
                                        commandPrefix = reader.nextString();
                                    } else if (name.equalsIgnoreCase(commandOnline.getName())) {
                                        commandOnline.read(reader);
                                    } else if (name.equalsIgnoreCase(commandTps.getName())) {
                                        commandTps.read(reader);
                                    } else if (name.equalsIgnoreCase(commandUnstuck.getName())) {
                                        commandUnstuck.read(reader);
                                    } else if (name.equalsIgnoreCase("custom")) {
                                        JsonToken peek = reader.peek();
                                        if (peek == JsonToken.BEGIN_ARRAY) {
                                            reader.beginArray();
                                            while (reader.hasNext()) {
                                                CustomCommandConfig customCommand = CustomCommandConfig.createFromConfig(reader);
                                                if (customCommand != null) {
                                                    customCommands.add(customCommand);
                                                }
                                            }
                                            reader.endArray();
                                        } else if (peek == JsonToken.BEGIN_OBJECT) {
                                            CustomCommandConfig customCommand = CustomCommandConfig.createFromConfig(reader);
                                            if (customCommand != null) {
                                                customCommands.add(customCommand);
                                            }
                                        }
                                    } else {
                                        reader.skipValue();
                                    }
                                }
                                reader.endObject();
                            } else {
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    } else if (name.equalsIgnoreCase("messages") && reader.peek() == JsonToken.BEGIN_OBJECT) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            name = reader.nextName();
                            if (name.equalsIgnoreCase("discord") && reader.peek() == JsonToken.BEGIN_OBJECT) {
                                reader.beginObject();
                                while (reader.hasNext()) {
                                    name = reader.nextName();
                                    if (name.equalsIgnoreCase("chat")) {
                                        discordChat.read(reader);
                                    } else if (name.equalsIgnoreCase("death")) {
                                        discordDeath.read(reader);
                                    } else if (name.equalsIgnoreCase("achievement")) {
                                        discordAchievement.read(reader);
                                    } else if (name.equalsIgnoreCase("join")) {
                                        discordJoin.read(reader);
                                    } else if (name.equalsIgnoreCase("leave")) {
                                        discordLeave.read(reader);
                                    } else if (name.equalsIgnoreCase("startup")) {
                                        discordStartup.read(reader);
                                    } else if (name.equalsIgnoreCase("shutdown")) {
                                        discordShutdown.read(reader);
                                    } else {
                                        reader.skipValue();
                                    }
                                }
                                reader.endObject();
                            } else if (name.equalsIgnoreCase("minecraft") && reader.peek() == JsonToken.BEGIN_OBJECT) {
                                reader.beginObject();
                                while (reader.hasNext()) {
                                    name = reader.nextName();
                                    if (name.equalsIgnoreCase("chat")) {
                                        minecraftChat.read(reader);
                                    } else {
                                        reader.skipValue();
                                    }
                                }
                                reader.endObject();
                            } else {
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    } else if (name.equalsIgnoreCase("experimental") && reader.peek() == JsonToken.BEGIN_OBJECT) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            name = reader.nextName();
                            if (name.equalsIgnoreCase("fakePlayers") && reader.peek() == JsonToken.BOOLEAN) {
                                experimentalFakePlayers = reader.nextBoolean();
                            } else {
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getToken() {
        return token;
    }

    public static String getChannel() {
        return channel;
    }

    public static String getCommandPrefix() {
        return commandPrefix;
    }

    public static OnlineCommandConfig getCommandOnline() {
        return commandOnline;
    }

    public static TpsCommandConfig getCommandTps() {
        return commandTps;
    }

    public static UnstuckCommandConfig getCommandUnstuck() {
        return commandUnstuck;
    }

    public static List<CustomCommandConfig> getCustomCommands() {
        return customCommands;
    }

    public static boolean isIgnoringBots() {
        return ignoringBots;
    }

    public static boolean isExperimentalFakePlayersEnabled() {
        return experimentalFakePlayers;
    }

    public static MinecraftChatMessageConfig getDiscordChat() {
        return discordChat;
    }

    public static GenericMessageConfig getDiscordDeath() {
        return discordDeath;
    }

    public static AchievementMessageConfig getDiscordAchievement() {
        return discordAchievement;
    }

    public static GenericMessageConfig getDiscordJoin() {
        return discordJoin;
    }

    public static GenericMessageConfig getDiscordLeave() {
        return discordLeave;
    }

    public static GenericMessageConfig getDiscordStartup() {
        return discordStartup;
    }

    public static GenericMessageConfig getDiscordShutdown() {
        return discordShutdown;
    }

    public static DiscordChatMessageConfig getMinecraftChat() {
        return minecraftChat;
    }

    public static GenericMessageConfig getDiscordCrash() {
        return discordCrash;
    }
}
