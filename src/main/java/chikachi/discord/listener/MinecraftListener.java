/**
 * Copyright (C) 2016 Chikachi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package chikachi.discord.listener;

import chikachi.discord.config.Configuration;
import chikachi.discord.config.message.AchievementMessageConfig;
import chikachi.discord.config.message.GenericMessageConfig;
import chikachi.discord.config.message.MinecraftChatMessageConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class MinecraftListener {
    @SubscribeEvent
    public void onCommand(CommandEvent event) {
        String commandName = event.getCommand().getCommandName();

        if (commandName.equalsIgnoreCase("say")) {
            MinecraftChatMessageConfig messageConfig = Configuration.getDiscordChat();
            messageConfig.handleCommandEvent(event);
        }
    }

    @SubscribeEvent
    public void onChatMessage(ServerChatEvent event) {
        if (event.getPlayer() == null) return;

        MinecraftChatMessageConfig messageConfig = Configuration.getDiscordChat();
        messageConfig.handleChatEvent(event);
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        EntityLivingBase entityLiving = event.getEntityLiving();

        if (entityLiving == null) return;

        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) entityLiving;

            GenericMessageConfig messageConfig = Configuration.getDiscordDeath();
            messageConfig.sendMessage(
                    entityPlayer.getDisplayNameString(),
                    entityPlayer.getCombatTracker().getDeathMessage().getUnformattedText().replace(entityPlayer.getDisplayNameString(), "").trim()
            );
        }
    }

    @SubscribeEvent
    public void onPlayerAchievement(AchievementEvent event) {
        EntityPlayer entityPlayer = event.getEntityPlayer();

        if (entityPlayer == null) return;

        if (entityPlayer instanceof EntityPlayerMP) {
            StatisticsManagerServer playerStats = ((EntityPlayerMP) entityPlayer).getStatFile();

            if (playerStats.hasAchievementUnlocked(event.getAchievement()) || !playerStats.canUnlockAchievement(event.getAchievement())) {
                return;
            }

            AchievementMessageConfig messageConfig = Configuration.getDiscordAchievement();
            messageConfig.handleEvent(event);
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player == null) return;

        GenericMessageConfig messageConfig = Configuration.getDiscordJoin();
        messageConfig.sendMessage(event.player.getDisplayNameString());
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player == null) return;

        GenericMessageConfig messageConfig = Configuration.getDiscordLeave();
        messageConfig.sendMessage(event.player.getDisplayNameString());
    }
}
