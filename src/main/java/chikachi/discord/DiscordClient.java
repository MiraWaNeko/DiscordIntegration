package chikachi.discord;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

class DiscordClient {
    private static DiscordClient instance;
    private JDA jda;
    private DiscordListener listener = new DiscordListener();
    TextChannel channel;
    List<String> queue = new ArrayList<>();

    static DiscordClient getInstance() {
        if (instance == null) {
            instance = new DiscordClient();
        }

        return instance;
    }

    private DiscordClient() {

    }

    void connect() {
        String token = Configuration.getToken();

        if (token.isEmpty()) {
            ChikachiDiscord.Log("Missing token", true);
            return;
        }

        try {
            this.jda = new JDABuilder().setBotToken(token).addListener(this.listener).buildAsync();
        } catch (LoginException e) {
            ChikachiDiscord.Log("Failed to connect to Discord", true);
            e.printStackTrace();
        }
    }

    boolean sendMessage(String message) {
        if (this.jda == null) {
            this.queue.add(message);
            return true;
        }

        if (this.channel == null) {
            this.channel = this.jda.getTextChannelById(Configuration.getChannel());
            if (this.channel == null) {
                ChikachiDiscord.Log("Failed to find channel", true);
                return false;
            }
        }

        if (message.contains("@")) {
            message = " " + message + " ";

            List<User> users = this.channel.getGuild().getUsers();

            for (User user : users) {
                if (message.contains("@" + user.getUsername() + " ")) {
                    message = message.replace("@" + user.getUsername() + " ", user.getAsMention() + " ");
                }
            }

            message = message.trim();
        }

        this.channel.sendMessage(message);
        return true;
    }
}
