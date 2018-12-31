package net.discordintegration;

import net.discordintegration.core.Proxy;
import net.discordintegration.core.config.Configuration;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MinecraftListener {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChatMessage(ServerChatEvent event) {
        if (event.isCanceled() || event.getPlayer() == null) return;
        if (Configuration.getConfig().minecraft.dimensions.generic.ignoreFakePlayerChat && event.getPlayer() instanceof FakePlayer) {
            return;
        }

        Proxy.getBridge().event.onChatMessage(
            event.getPlayer().getUniqueID(),
            event.getPlayer().dimension, event.getMessage()
        );
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerAchievement(AchievementEvent event) {
        Achievement achievement = event.getAchievement();

        if (event.isCanceled() || achievement == null) return;

        Proxy.getBridge().event.onPlayerAchievement(
            event.getEntityPlayer().getUniqueID(),
            event.getEntityPlayer().dimension,
            achievement.getStatName().getUnformattedText(),
            achievement.getDescription()
        );
    }
}
