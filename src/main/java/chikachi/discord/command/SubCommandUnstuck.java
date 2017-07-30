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

package chikachi.discord.command;

import chikachi.discord.DiscordFakePlayer;
import chikachi.discord.DiscordTeleporter;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;

public class SubCommandUnstuck {
    public static void execute(ICommandSender sender, ArrayList<String> args) {
        if (args.size() == 0) {
            sender.addChatMessage(new ChatComponentText("Missing user"));
            return;
        }

        String username = args.remove(0);
        if (username.length() == 0) {
            sender.addChatMessage(new ChatComponentText("Missing user"));
            return;
        }

        WorldServer overworld = DimensionManager.getWorld(0);
        ChunkCoordinates spawnpoint = overworld.getSpawnPoint();
        double y = spawnpoint.posY;

        while (overworld.getBlock(spawnpoint.posX, (int) y, spawnpoint.posZ).isOpaqueCube()) {
            y += 2;
        }

        double x = spawnpoint.posX + 0.5;
        double z = spawnpoint.posZ + 0.5;

        MinecraftServer minecraftServer = MinecraftServer.getServer();
        EntityPlayerMP player = minecraftServer.getConfigurationManager().func_152612_a(username);

        if (player != null) {
            int fromDimension = player.dimension;

            if (fromDimension != 0) {
                minecraftServer.getConfigurationManager().transferPlayerToDimension(player, 0, new DiscordTeleporter(overworld));

                if (fromDimension == 1 && player.isEntityAlive()) {
                    overworld.spawnEntityInWorld(player);
                    overworld.updateEntityWithOptionalForce(player, true);
                }
            }

            player.setPositionAndUpdate(x, y, z);
        } else {
            GameProfile playerProfile = minecraftServer.func_152358_ax().func_152655_a(username);

            if (playerProfile == null || !playerProfile.isComplete()) {
                sender.addChatMessage(new ChatComponentText("Player not found"));
                return;
            }

            DiscordFakePlayer fakePlayer = new DiscordFakePlayer(minecraftServer.worldServers[0], playerProfile);
            IPlayerFileData saveHandler = minecraftServer.worldServers[0].getSaveHandler().getSaveHandler();
            NBTTagCompound playerData = saveHandler.readPlayerData(fakePlayer);

            //noinspection ConstantConditions
            if (playerData == null) {
                sender.addChatMessage(new ChatComponentText("Player not found on server"));
                return;
            }

            fakePlayer.posX = x;
            fakePlayer.posY = y;
            fakePlayer.posZ = z;

            fakePlayer.dimension = 0;
            saveHandler.writePlayerData(fakePlayer);
        }

        sender.addChatMessage(new ChatComponentText("Player sent to spawn"));
    }
}
