package chikachi.discord.config.message;

import chikachi.discord.ChikachiDiscord;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.io.IOException;

public abstract class BaseMessageConfig {
    private final String name;
    private boolean enabled;
    protected String message;

    BaseMessageConfig(String name, boolean enabled, String message) {
        this.name = name;
        this.enabled = enabled;
        this.message = message;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getMessage() {
        return this.message;
    }

    public void read(JsonReader reader) throws IOException {
        String name;

        JsonToken type = reader.peek();

        if (type == JsonToken.BEGIN_OBJECT) {
            reader.beginObject();
            while (reader.hasNext()) {
                name = reader.nextName();
                if (name.equalsIgnoreCase("enabled") && reader.peek() == JsonToken.BOOLEAN) {
                    this.enabled = reader.nextBoolean();
                } else if (name.equalsIgnoreCase("message") && reader.peek() == JsonToken.STRING) {
                    this.message = reader.nextString();
                } else {
                    this.readExtra(reader, name);
                }
            }
            reader.endObject();
        } else if (type == JsonToken.STRING) {
            this.message = reader.nextString();
            this.enabled = !this.message.equals("");
        } else {
            ChikachiDiscord.Log(String.format("Invalid value of message config for %s", this.name), true);
            reader.skipValue();
        }
    }

    public void write(JsonWriter writer) throws IOException {
        writer.name(this.name);
        writer.beginObject();
        writer.name("enabled");
        writer.value(this.enabled);
        writer.name("message");
        writer.value(this.message);
        this.writeExtra(writer);
        writer.endObject();
    }

    protected void readExtra(JsonReader reader, String name) throws IOException {
        reader.skipValue();
    }

    protected void writeExtra(JsonWriter writer) throws IOException {
    }
}
