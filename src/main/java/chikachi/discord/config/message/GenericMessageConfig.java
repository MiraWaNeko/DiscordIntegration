package chikachi.discord.config.message;

import chikachi.discord.DiscordClient;

public class GenericMessageConfig extends BaseMessageConfig {
    public GenericMessageConfig(String name, boolean enabled, String message) {
        super(name, enabled, message);
    }

    private void doSendMessage(String message) {
        DiscordClient.getInstance().sendMessage(
                message
        );
    }

    public void sendMessage(String username) {
        if (!this.isEnabled()) return;

        doSendMessage(
                this.getMessage()
                        .replace("%USER%", username)
        );
    }

    public void sendMessage(String username, String message) {
        if (!this.isEnabled()) return;

        doSendMessage(
                this.getMessage()
                        .replace("%USER%", username)
                        .replace("%MESSAGE%", message)
        );
    }
}
