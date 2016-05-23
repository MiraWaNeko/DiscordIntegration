package chikachi.discord.config.message;

import chikachi.discord.DiscordClient;
import com.google.common.base.Joiner;
import net.minecraft.util.text.TextComponentString;
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

        String message = Joiner.on(" ").join(event.getParameters());
        message = message.replaceAll("§.", "");

        TextComponentString chatComponent = new TextComponentString(message);

        DiscordClient.getInstance().sendMessage(
                this.getMessage()
                        .replace("%USER%", event.getSender().getName())
                        .replace("%MESSAGE%", chatComponent.getUnformattedText())
        );
    }

    public void handleChatEvent(ServerChatEvent event) {
        if (!this.isEnabled()) return;

        String message = event.getMessage();
        message = message.replaceAll("§.", "");

        DiscordClient.getInstance().sendMessage(
                this.getMessage()
                        .replace("%USER%", event.getUsername())
                        .replace("%MESSAGE%", message)
        );
    }
}
