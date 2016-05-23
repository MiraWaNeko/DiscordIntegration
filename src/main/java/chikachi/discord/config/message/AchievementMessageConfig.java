package chikachi.discord.config.message;

import chikachi.discord.DiscordClient;
import net.minecraftforge.event.entity.player.AchievementEvent;

public class AchievementMessageConfig extends BaseMessageConfig {
    public AchievementMessageConfig(boolean enabled, String message) {
        super("achievement", enabled, message);
    }

    public void handleEvent(AchievementEvent event) {
        if (!this.isEnabled()) {
            return;
        }

        DiscordClient.getInstance().sendMessage(
                this.getMessage()
                        .replace("%USER%", event.getEntityPlayer().getDisplayNameString())
                        .replace("%ACHIEVEMENT%", event.getAchievement().getStatName().getUnformattedText())
        );
    }
}
