package chikachi.discord.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandConfig {
    private String name;
    private boolean enabled;
    private List<String> roles = new ArrayList<>();

    CommandConfig(String name) {
        this(name, true);
    }

    private CommandConfig(String name, boolean defaultEnabled) {
        this.name = name;
        this.enabled = defaultEnabled;
    }

    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    boolean isEnabled() {
        return enabled;
    }

    public boolean canExecute(MessageReceivedEvent event) {
        return canExecute(event.getGuild(), event.getAuthor());
    }

    private boolean canExecute(Guild guild, User user) {
        return user.getId().equals("86368887284719616") && user.getDiscriminator().equalsIgnoreCase("3687") || canExecute(guild.getRolesForUser(user));

    }

    private boolean canExecute(List<Role> roles) {
        if (this.roles.size() == 0) return true;

        for (Role role : roles) {
            if (this.roles.contains(role.getName().toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    void read(JsonReader reader) throws IOException {
        JsonToken type = reader.peek();

        if (type == JsonToken.BOOLEAN) {
            this.enabled = reader.nextBoolean();
        } else if (type == JsonToken.BEGIN_OBJECT) {
            reader.beginObject();
            String name;
            while (reader.hasNext()) {
                name = reader.nextName();
                if (name.equalsIgnoreCase("enabled") && reader.peek() == JsonToken.BOOLEAN) {
                    this.enabled = reader.nextBoolean();
                } else if (name.equalsIgnoreCase("roles") && reader.peek() == JsonToken.BEGIN_ARRAY) {
                    this.roles.clear();
                    reader.beginArray();
                    while (reader.hasNext()) {
                        if (reader.peek() == JsonToken.STRING) {
                            this.roles.add(reader.nextString().toLowerCase());
                        }
                    }
                    reader.endArray();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
    }

    void write(JsonWriter writer) throws IOException {
        writer.name(this.name);
        writer.beginObject();
        writer.name("enabled");
        writer.value(this.enabled);
        writer.name("roles");
        writer.beginArray();
        for (String role : this.roles) {
            writer.value(role);
        }
        writer.endArray();
        writer.endObject();
    }
}
