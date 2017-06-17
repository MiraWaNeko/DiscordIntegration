/*
 * Copyright (C) 2017 Chikachi
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package chikachi.discord.listener;

import chikachi.discord.DiscordIntegrationLogger;
import chikachi.discord.core.DiscordClient;
import chikachi.discord.core.Message;
import chikachi.discord.core.config.Configuration;
import com.google.common.base.Joiner;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.HashMap;

public class MinecraftListener {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCommand(CommandEvent event) {
        if (event.isCanceled()) return;

        String commandName = event.getCommand().getName();

        if (commandName.equalsIgnoreCase("say")) {
            ICommandSender sender = event.getSender();

            if (sender != null && Configuration.getConfig().minecraft.dimensions.generic.ignoreFakePlayerChat && sender instanceof FakePlayer)
                return;

            HashMap<String, String> arguments = new HashMap<>();
            arguments.put("MESSAGE", Joiner.on(" ").join(event.getParameters()));

            DiscordClient.getInstance().broadcast(
                new Message(
                    sender != null ? sender.getName() : null,
                    sender != null && sender instanceof EntityPlayer ? "https://minotar.net/avatar/" + sender.getName() + "/128.png" : null,
                    Configuration.getConfig().minecraft.messages.chatMessage,
                    arguments
                ),
                Configuration.getConfig().minecraft.dimensions.generic.discordChannel.getChannels()
            );
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChatMessage(ServerChatEvent event) {
        if (event.isCanceled() || event.getPlayer() == null) return;

        if (Configuration.getConfig().minecraft.dimensions.generic.ignoreFakePlayerChat && event.getPlayer() instanceof FakePlayer)
            return;

        HashMap<String, String> arguments = new HashMap<>();
        arguments.put("MESSAGE", event.getMessage());

        // TODO: Change to use dimension
        DiscordClient.getInstance().broadcast(
            new Message(
                event.getUsername(),
                "https://minotar.net/avatar/" + event.getUsername() + "/128.png",
                Configuration.getConfig().minecraft.messages.chatMessage,
                arguments
            ),
            Configuration.getConfig().minecraft.dimensions.generic.discordChannel.getChannels()
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerAchievement(AchievementEvent event) {
        if (event.isCanceled()) return;

        EntityPlayer entityPlayer = event.getEntityPlayer();

        if (entityPlayer != null && entityPlayer instanceof EntityPlayerMP) {
            StatisticsManagerServer playerStats = ((EntityPlayerMP) entityPlayer).getStatFile();

            if (playerStats.hasAchievementUnlocked(event.getAchievement()) || !playerStats.canUnlockAchievement(event.getAchievement())) {
                return;
            }

            Achievement achievement = event.getAchievement();

            HashMap<String, String> arguments = new HashMap<>();
            arguments.put("ACHIEVEMENT", achievement.getStatName().getUnformattedText());
            // TODO: Change to use dimension
            // TODO: Figure out what to do with I18n - Might remove the description...
            //noinspection deprecation
            arguments.put("DESCRIPTION", I18n.translateToLocalFormatted(achievement.achievementDescription, "KEY"));

            DiscordClient.getInstance().broadcast(
                new Message(
                    entityPlayer.getDisplayNameString(),
                    "https://minotar.net/avatar/" + entityPlayer.getName() + "/128.png",
                    Configuration.getConfig().minecraft.messages.achievement,
                    arguments
                ),
                Configuration.getConfig().minecraft.dimensions.generic.discordChannel.getChannels()
            );
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.isCanceled() || event.player == null) return;

        DiscordClient.getInstance().broadcast(
            new Message(
                event.player.getDisplayNameString(),
                "https://minotar.net/avatar/" + event.player.getName() + "/128.png",
                Configuration.getConfig().minecraft.messages.playerJoin
            ),
            Configuration.getConfig().minecraft.dimensions.generic.discordChannel.getChannels()
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.isCanceled() || event.player == null) return;

        DiscordClient.getInstance().broadcast(
            new Message(
                event.player.getDisplayNameString(),
                "https://minotar.net/avatar/" + event.player.getName() + "/128.png",
                Configuration.getConfig().minecraft.messages.playerLeave
            ),
            Configuration.getConfig().minecraft.dimensions.generic.discordChannel.getChannels()
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onPlayerDeath(LivingDeathEvent event) {
        EntityLivingBase entityLiving = event.getEntityLiving();

        if (entityLiving == null) return;

        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) entityLiving;

            HashMap<String, String> arguments = new HashMap<>();
            arguments.put("REASON", entityPlayer.getCombatTracker().getDeathMessage().getUnformattedText().replace(entityPlayer.getDisplayNameString(), "").trim());

            DiscordClient.getInstance().broadcast(
                new Message(
                    entityPlayer.getDisplayNameString(),
                    "https://minotar.net/avatar/" + entityPlayer.getName() + "/128.png",
                    Configuration.getConfig().minecraft.messages.playerDeath,
                    arguments
                ),
                Configuration.getConfig().minecraft.dimensions.generic.discordChannel.getChannels()
            );
        }
    }
}
