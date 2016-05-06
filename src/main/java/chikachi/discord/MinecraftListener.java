package chikachi.discord;

import com.google.common.base.Joiner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatisticsFile;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.Arrays;

@SuppressWarnings("unused")
class MinecraftListener {
    @SubscribeEvent
    public void onCommand(CommandEvent event) {
        String commandName = event.command.getCommandName();

        if (commandName.equalsIgnoreCase("say")) {
            EnableMessageTuple setting = Configuration.getDiscordChat();

            if (!setting.isEnabled()) return;

            String message = Joiner.on(" ").join(event.parameters);

            DiscordClient.getInstance().sendMessage(
                    String.format(
                            setting.getMessage(),
                            event.sender.getName(),
                            message
                    )
            );
        }
    }

    @SubscribeEvent
    public void onChatMessage(ServerChatEvent event) {
        if (event.player == null) return;

        EnableMessageTuple setting = Configuration.getDiscordChat();

        if (!setting.isEnabled()) return;

        DiscordClient.getInstance().sendMessage(
                String.format(
                        setting.getMessage(),
                        event.username,
                        event.message
                )
        );
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.entityLiving == null) return;

        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) event.entityLiving;

            EnableMessageTuple setting = Configuration.getDiscordDeath();

            if (!setting.isEnabled()) return;

            DiscordClient.getInstance().sendMessage(
                    String.format(
                            setting.getMessage(),
                            entityPlayer.getDisplayNameString(),
                            entityPlayer.getCombatTracker().getDeathMessage().getUnformattedText().replace(entityPlayer.getDisplayNameString(), "").trim()
                    )
            );
        }
    }

    @SubscribeEvent
    public void onPlayerAchievement(AchievementEvent event) {
        if (event.entityPlayer == null) return;

        if (event.entityPlayer instanceof EntityPlayerMP) {
            StatisticsFile playerStats = ((EntityPlayerMP) event.entityPlayer).getStatFile();

            if (playerStats.hasAchievementUnlocked(event.achievement) || !playerStats.canUnlockAchievement(event.achievement)) {
                return;
            }

            EnableMessageTuple setting = Configuration.getDiscordAchievement();

            if (!setting.isEnabled()) return;


            DiscordClient.getInstance().sendMessage(
                    String.format(
                            setting.getMessage(),
                            event.entityPlayer.getDisplayNameString(),
                            event.achievement.getStatName().getUnformattedText()
                    )
            );
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player == null) return;

        EnableMessageTuple setting = Configuration.getDiscordJoin();

        if (!setting.isEnabled()) return;

        DiscordClient.getInstance().sendMessage(
                String.format(
                        setting.getMessage(),
                        event.player.getDisplayNameString()
                )
        );
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player == null) return;

        EnableMessageTuple setting = Configuration.getDiscordLeave();

        if (!setting.isEnabled()) return;

        DiscordClient.getInstance().sendMessage(
                String.format(
                        setting.getMessage(),
                        event.player.getDisplayNameString()
                )
        );
    }
}
