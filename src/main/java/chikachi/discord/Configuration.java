package chikachi.discord;

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

    private static boolean commandOnline = true;
    private static boolean commandTps = true;

    private static boolean experimentalFakePlayers = false;

    private static EnableMessageTuple discordChat = new EnableMessageTuple(true, "<__%s__> %s");
    private static EnableMessageTuple discordDeath = new EnableMessageTuple(true, "__%s__ %s");
    private static EnableMessageTuple discordAchievement = new EnableMessageTuple(true, "Congrats to __%s__ for earning the achievement **[%s]**");
    private static EnableMessageTuple discordJoin = new EnableMessageTuple(true, "__%s__ has joined the LoveClan server!  We hope you enjoy your stay :)");
    private static EnableMessageTuple discordLeave = new EnableMessageTuple(true, "__%s__ left the LoveClan server!  We hope to see you back again soon!");
    private static EnableMessageTuple discordStartup = new EnableMessageTuple(false, "**Server started**");
    private static EnableMessageTuple discordShutdown = new EnableMessageTuple(false, "**Server shutdown**");

    private static EnableMessageTuple minecraftChat = new EnableMessageTuple(true, "<__%s__> %s");

    static void onPreInit(FMLPreInitializationEvent event) {
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
                if (name.equalsIgnoreCase("enabled")) {
                    enabled = reader.nextBoolean();
                } else if (name.equalsIgnoreCase("message")) {
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

    private static void writeEnableMessageCombo(JsonWriter writer, String key, String message) throws IOException {
        writeEnableMessageCombo(writer, key, message, true);
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
                writer.name("online");
                writer.value(commandOnline);
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
                writeEnableMessageCombo(writer, "chat", minecraftChat.getMessage(), minecraftChat.isEnabled());
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
                                    if (name.equalsIgnoreCase("online") && reader.peek() == JsonToken.BOOLEAN) {
                                        commandOnline = reader.nextBoolean();
                                    } else if (name.equalsIgnoreCase("tps") && reader.peek() == JsonToken.BOOLEAN) {
                                        commandTps = reader.nextBoolean();
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
                                    if (name.equalsIgnoreCase("chat")) {
                                        minecraftChat = readEnableMessageCombo(reader);
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

    static String getToken() {
        return token;
    }

    static String getChannel() {
        return channel;
    }

    static boolean isCommandTpsEnabled() {
        return commandTps;
    }

    static boolean isCommandOnlineEnabled() {
        return commandOnline;
    }

    static boolean isExperimentalFakePlayersEnabled() {
        return experimentalFakePlayers;
    }

    static EnableMessageTuple getDiscordChat() {
        return discordChat;
    }

    static EnableMessageTuple getDiscordDeath() {
        return discordDeath;
    }

    static EnableMessageTuple getDiscordAchievement() {
        return discordAchievement;
    }

    static EnableMessageTuple getDiscordJoin() {
        return discordJoin;
    }

    static EnableMessageTuple getDiscordLeave() {
        return discordLeave;
    }

    static EnableMessageTuple getDiscordStartup() {
        return discordStartup;
    }

    static EnableMessageTuple getDiscordShutdown() {
        return discordShutdown;
    }

    static EnableMessageTuple getMinecraftChat() {
        return minecraftChat;
    }
}
