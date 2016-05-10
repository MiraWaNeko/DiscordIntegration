package chikachi.discord.config;

import chikachi.discord.ChikachiDiscord;
import chikachi.discord.Constants;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Configuration {
    private static File config;
    private static String token = "";
    private static String channel = "";

    private static CommandConfig commandOnline = new CommandConfig("online");
    private static CommandConfig commandTps = new CommandConfig("tps");

    private static boolean experimentalFakePlayers = false;

    private static EnableMessageTuple discordChat = new EnableMessageTuple(true, "<__%USER%__> %MESSAGE%");
    private static EnableMessageTuple discordDeath = new EnableMessageTuple(true, "__%USER%__ %MESSAGE%");
    private static EnableMessageTuple discordAchievement = new EnableMessageTuple(true, "Congrats to __%USER%__ for earning the achievement **[%ACHIEVEMENT%]**");
    private static EnableMessageTuple discordJoin = new EnableMessageTuple(true, "__%USER%__ has joined the server!");
    private static EnableMessageTuple discordLeave = new EnableMessageTuple(true, "__%USER%__ left the server!");
    private static EnableMessageTuple discordStartup = new EnableMessageTuple(false, "**Server started**");
    private static EnableMessageTuple discordShutdown = new EnableMessageTuple(false, "**Server shutdown**");

    private static EnableMessageTuple minecraftChat = new EnableMessageTuple(true, "<__%USER%__> %MESSAGE%");
    private static int minecraftChatMaxLength = -1;

    public static void onPreInit(FMLPreInitializationEvent event) {
        File directory = new File(event.getModConfigurationDirectory().getAbsolutePath() + File.separator + "Chikachi");
        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();

        config = new File(directory, Constants.MODID + ".json");

        load();
    }

    private static EnableMessageTuple readEnableMessageCombo(JsonReader reader) throws IOException {
        String name;

        boolean enabled = false;
        String message = "";

        JsonToken type = reader.peek();

        if (type == JsonToken.BEGIN_OBJECT) {
            reader.beginObject();
            while (reader.hasNext()) {
                name = reader.nextName();
                if (name.equalsIgnoreCase("enabled") && reader.peek() == JsonToken.BOOLEAN) {
                    enabled = reader.nextBoolean();
                } else if (name.equalsIgnoreCase("message") && reader.peek() == JsonToken.STRING) {
                    message = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } else if (type == JsonToken.STRING) {
            message = reader.nextString();
            enabled = !message.equals("");
        } else {
            reader.skipValue();
        }

        return new EnableMessageTuple(enabled, message);
    }

    private static void writeEnableMessageCombo(JsonWriter writer, String key, String message, boolean enabled) throws IOException {
        writer.name(key);
        writer.beginObject();
        writer.name("enabled");
        writer.value(enabled);
        writer.name("message");
        writer.value(message);
        writer.endObject();
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

                        writer.name("commands");
                        writer.beginObject();
                            commandOnline.write(writer);
                            commandTps.write(writer);
                        writer.endObject();
                    writer.endObject();

                    writer.name("messages");
                    writer.beginObject();
                        writer.name("discord");
                        writer.beginObject();
                            writeEnableMessageCombo(writer, "chat", discordChat.getMessage(), discordChat.isEnabled());
                            writeEnableMessageCombo(writer, "death", discordDeath.getMessage(), discordDeath.isEnabled());
                            writeEnableMessageCombo(writer, "achievement", discordAchievement.getMessage(), discordAchievement.isEnabled());
                            writeEnableMessageCombo(writer, "join", discordJoin.getMessage(), discordJoin.isEnabled());
                            writeEnableMessageCombo(writer, "leave", discordLeave.getMessage(), discordLeave.isEnabled());
                            writeEnableMessageCombo(writer, "startup", discordStartup.getMessage(), discordStartup.isEnabled());
                            writeEnableMessageCombo(writer, "shutdown", discordShutdown.getMessage(), discordShutdown.isEnabled());
                        writer.endObject();

                        writer.name("minecraft");
                        writer.beginObject();
                            writer.name("chat");
                            writer.beginObject();
                                writer.name("enabled");
                                writer.value(minecraftChat.isEnabled());
                                writer.name("message");
                                writer.value(minecraftChat.getMessage());
                                writer.name("maxLength");
                                writer.value(minecraftChatMaxLength);
                            writer.endObject();
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
                ChikachiDiscord.Log("Error generating default config file", true);
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
                            } else if (name.equalsIgnoreCase("commands") && reader.peek() == JsonToken.BEGIN_OBJECT) {
                                reader.beginObject();
                                while (reader.hasNext()) {
                                    name = reader.nextName();
                                    if (name.equalsIgnoreCase("online")) {
                                        commandOnline.read(reader);
                                    } else if (name.equalsIgnoreCase("tps")) {
                                        commandTps.read(reader);
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
                                        discordChat = readEnableMessageCombo(reader);
                                    } else if (name.equalsIgnoreCase("death")) {
                                        discordDeath = readEnableMessageCombo(reader);
                                    } else if (name.equalsIgnoreCase("achievement")) {
                                        discordAchievement = readEnableMessageCombo(reader);
                                    } else if (name.equalsIgnoreCase("join")) {
                                        discordJoin = readEnableMessageCombo(reader);
                                    } else if (name.equalsIgnoreCase("leave")) {
                                        discordLeave = readEnableMessageCombo(reader);
                                    } else if (name.equalsIgnoreCase("startup")) {
                                        discordStartup = readEnableMessageCombo(reader);
                                    } else if (name.equalsIgnoreCase("shutdown")) {
                                        discordShutdown = readEnableMessageCombo(reader);
                                    } else {
                                        reader.skipValue();
                                    }
                                }
                                reader.endObject();
                            } else if (name.equalsIgnoreCase("minecraft") && reader.peek() == JsonToken.BEGIN_OBJECT) {
                                reader.beginObject();
                                while (reader.hasNext()) {
                                    name = reader.nextName();
                                    if (name.equalsIgnoreCase("chat") && reader.peek() == JsonToken.BEGIN_OBJECT) {
                                        boolean enabled = minecraftChat.isEnabled();
                                        String message = minecraftChat.getMessage();

                                        reader.beginObject();
                                        while (reader.hasNext()) {
                                            name = reader.nextName();
                                            if (name.equalsIgnoreCase("enabled") && reader.peek() == JsonToken.BOOLEAN) {
                                                enabled = reader.nextBoolean();
                                            } else if (name.equalsIgnoreCase("message") && reader.peek() == JsonToken.STRING) {
                                                message = reader.nextString();
                                            } else if (name.equalsIgnoreCase("maxlength") && reader.peek() == JsonToken.NUMBER) {
                                                minecraftChatMaxLength = reader.nextInt();
                                            } else {
                                                reader.skipValue();
                                            }
                                        }
                                        reader.endObject();

                                        minecraftChat = new EnableMessageTuple(enabled, message);
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

    public static CommandConfig getCommandOnline() {
        return commandOnline;
    }

    public static CommandConfig getCommandTps() {
        return commandTps;
    }

    public static boolean isCommandOnlineEnabled() {
        return commandOnline.isEnabled();
    }

    public static boolean isCommandTpsEnabled() {
        return commandTps.isEnabled();
    }

    public static boolean isExperimentalFakePlayersEnabled() {
        return experimentalFakePlayers;
    }

    public static EnableMessageTuple getDiscordChat() {
        return discordChat;
    }

    public static EnableMessageTuple getDiscordDeath() {
        return discordDeath;
    }

    public static EnableMessageTuple getDiscordAchievement() {
        return discordAchievement;
    }

    public static EnableMessageTuple getDiscordJoin() {
        return discordJoin;
    }

    public static EnableMessageTuple getDiscordLeave() {
        return discordLeave;
    }

    public static EnableMessageTuple getDiscordStartup() {
        return discordStartup;
    }

    public static EnableMessageTuple getDiscordShutdown() {
        return discordShutdown;
    }

    public static EnableMessageTuple getMinecraftChat() {
        return minecraftChat;
    }

    public static int getMinecraftChatMaxLength() {
        return minecraftChatMaxLength;
    }
}
