package chikachi.discord.config.message;

import chikachi.discord.DiscordClient;
import com.google.common.base.Joiner;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;

public class MinecraftChatMessageConfig extends BaseMessageConfig {
    public MinecraftChatMessageConfig(boolean enabled, String message) {
        super("chat", enabled, message);
    }

    public void handleCommandEvent(CommandEvent event) {
        if (!this.isEnabled()) {
            return;
        }

        String message = Joiner.on(" ").join(event.parameters);
        message = message.replaceAll("§.", "");

        ChatComponentText chatComponent = new ChatComponentText(message);

        DiscordClient.getInstance().sendMessage(
                this.getMessage()
                        .replace("%USER%", event.sender.getName())
                        .replace("%MESSAGE%", chatComponent.getUnformattedText())
        );
    }

    public void handleChatEvent(ServerChatEvent event) {
        if (!this.isEnabled()) return;

        String message = event.message;
        message = message.replaceAll("§.", "");

        DiscordClient.getInstance().sendMessage(
                this.getMessage()
                        .replace("%USER%", event.username)
                        .replace("%MESSAGE%", message)
        );
    }
}
