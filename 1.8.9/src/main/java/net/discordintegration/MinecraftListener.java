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
        if (event.isCanceled() || event.player == null) return;
        if (Configuration.getConfig().minecraft.dimensions.generic.ignoreFakePlayerChat && event.player instanceof FakePlayer) {
            return;
        }

        Proxy.getBridge().event.onChatMessage(
            event.player.getUniqueID(),
            event.player.dimension, event.message
        );
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerAchievement(AchievementEvent event) {
        Achievement achievement = event.achievement;

        if (event.isCanceled() || achievement == null) return;

        Proxy.getBridge().event.onPlayerAchievement(
            event.entityPlayer.getUniqueID(),
            event.entityPlayer.dimension,
            achievement.getStatName().getUnformattedText(),
            achievement.getDescription()
        );
    }
}
