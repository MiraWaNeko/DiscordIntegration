package chikachi.discord.config.command;

import chikachi.discord.DiscordClient;
import chikachi.discord.DiscordTeleporter;
import chikachi.discord.config.experimental.DiscordFakePlayer;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.List;

public class UnstuckCommandConfig extends CommandConfig {
    public UnstuckCommandConfig() {
        super("unstuck", false, "AdminRoleHere");
    }

    @Override
    public void execute(List<String> args) {
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
        BlockPos spawnpoint = overworld.getSpawnPoint();

        while (overworld.getBlockState(spawnpoint).getBlock().isOpaqueCube()) {
            spawnpoint = spawnpoint.up(2);
        }

        double x = spawnpoint.getX() + 0.5;
        double y = spawnpoint.getY();
        double z = spawnpoint.getZ() + 0.5;

        MinecraftServer server = MinecraftServer.getServer();
        ServerConfigurationManager configurationManager = server.getConfigurationManager();

        EntityPlayerMP playerEntity = configurationManager.getPlayerByUsername(playerName);

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
            GameProfile playerProfile = server.getPlayerProfileCache().getGameProfileForUsername(playerName);

            if (playerProfile == null || !playerProfile.isComplete()) {
                DiscordClient.getInstance().sendMessage("Player not found");
                return;
            }

            DiscordFakePlayer fakePlayer = new DiscordFakePlayer(server.worldServers[0], playerProfile);
            NBTTagCompound playerData = configurationManager.readPlayerDataFromFile(fakePlayer);

            if (playerData == null) {
                DiscordClient.getInstance().sendMessage("Player not found on server");
                return;
            }

            fakePlayer.posX = x;
            fakePlayer.posY = y;
            fakePlayer.posZ = z;

            fakePlayer.dimension = 0;

            configurationManager.playerEntityList.add(fakePlayer);
            configurationManager.saveAllPlayerData();
            configurationManager.playerEntityList.remove(fakePlayer);
        }

        DiscordClient.getInstance().sendMessage("Player sent to spawn");
    }
}
