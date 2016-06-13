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

package chikachi.discord.command.discord;

import chikachi.discord.DiscordClient;
import chikachi.discord.DiscordTeleporter;
import chikachi.discord.experimental.DiscordFakePlayer;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.entities.User;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraftforge.common.DimensionManager;

import java.util.List;

public class UnstuckCommandConfig extends CommandConfig {
    public UnstuckCommandConfig() {
        super("unstuck", false, "AdminRoleHere");
    }

    @Override
    public void execute(User user, List<String> args) {
        if (args.size() == 0) {
            DiscordClient.getInstance().sendMessage("Missing player");
            return;
        }
        String playerName = args.remove(0);
        if (playerName.length() == 0) {
            DiscordClient.getInstance().sendMessage("Missing player");
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

        MinecraftServer server = MinecraftServer.getServer();
        ServerConfigurationManager configurationManager = server.getConfigurationManager();

        EntityPlayerMP playerEntity = configurationManager.func_152612_a(playerName);

        if (playerEntity != null) {
            int fromDimId = playerEntity.dimension;

            if (fromDimId != 0) {
                configurationManager.transferPlayerToDimension(playerEntity, 0, new DiscordTeleporter(overworld));

                if (fromDimId == 1 && playerEntity.isEntityAlive()) {
                    overworld.spawnEntityInWorld(playerEntity);
                    overworld.updateEntityWithOptionalForce(playerEntity, false);
                }
            }

            playerEntity.setPositionAndUpdate(x, y, z);
            playerEntity.playerNetServerHandler.kickPlayerFromServer("You are getting sent to spawn, please connect again!");
        } else {
            GameProfile playerProfile = server.func_152358_ax().func_152655_a(playerName);

            if (playerProfile == null || !playerProfile.isComplete()) {
                DiscordClient.getInstance().sendMessage("Player not found");
                return;
            }

            DiscordFakePlayer fakePlayer = new DiscordFakePlayer(server.worldServers[0], playerProfile);
            IPlayerFileData saveHandler = server.worldServers[0].getSaveHandler().getSaveHandler();
            NBTTagCompound playerData = saveHandler.readPlayerData(fakePlayer);

            if (playerData == null) {
                DiscordClient.getInstance().sendMessage("Player not found on server");
                return;
            }

            fakePlayer.posX = x;
            fakePlayer.posY = y;
            fakePlayer.posZ = z;

            fakePlayer.dimension = 0;

            saveHandler.writePlayerData(fakePlayer);
        }

        DiscordClient.getInstance().sendMessage("Player sent to spawn");
    }
}
