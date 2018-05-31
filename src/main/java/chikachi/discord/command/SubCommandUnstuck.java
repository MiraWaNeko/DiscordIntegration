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
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SubCommandUnstuck extends CommandBase {

    @Override
    public String getName() {
        return "unstuck";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return "/discord unstuck <username>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] strings) throws CommandException {
        if (strings.length == 0) {
            sender.sendMessage(new TextComponentString("Missing user"));
            return;
        }

        String username = strings[0];
        if (username.length() == 0) {
            sender.sendMessage(new TextComponentString("Missing user"));
            return;
        }

        WorldServer overworld = DimensionManager.getWorld(0);
        BlockPos spawnpoint = overworld.getSpawnPoint();
        IBlockState blockState = overworld.getBlockState(spawnpoint);

        while (blockState.getBlock().isOpaqueCube(blockState)) {
            spawnpoint = spawnpoint.up(2);
            blockState = overworld.getBlockState(spawnpoint);
        }

        double x = spawnpoint.getX() + 0.5;
        double y = spawnpoint.getY();
        double z = spawnpoint.getZ() + 0.5;

        MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        EntityPlayerMP player = minecraftServer.getPlayerList().getPlayerByUsername(username);

        if (player != null) {
            int fromDimension = player.dimension;

            if (fromDimension != 0) {
                minecraftServer.getPlayerList().transferPlayerToDimension(player, 0, new DiscordTeleporter(overworld));

                if (fromDimension == 1 && player.isEntityAlive()) {
                    overworld.spawnEntity(player);
                    overworld.updateEntityWithOptionalForce(player, true);
                }
            }

            player.setPositionAndUpdate(x, y, z);
        } else {
            GameProfile playerProfile = minecraftServer.getPlayerProfileCache().getGameProfileForUsername(username);

            if (playerProfile == null || !playerProfile.isComplete()) {
                sender.sendMessage(new TextComponentString("Player not found"));
                return;
            }

            DiscordFakePlayer fakePlayer = new DiscordFakePlayer(minecraftServer.worlds[0], playerProfile);
            IPlayerFileData saveHandler = minecraftServer.worlds[0].getSaveHandler().getPlayerNBTManager();
            NBTTagCompound playerData = saveHandler.readPlayerData(fakePlayer);

            //noinspection ConstantConditions
            if (playerData == null) {
                sender.sendMessage(new TextComponentString("Player not found on server"));
                return;
            }

            fakePlayer.posX = x;
            fakePlayer.posY = y;
            fakePlayer.posZ = z;

            fakePlayer.dimension = 0;
            saveHandler.writePlayerData(fakePlayer);
        }

        sender.sendMessage(new TextComponentString("Player sent to spawn"));
    }
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(4, getName());
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return getListOfStringsMatchingLastWord(args, server.getPlayerList().getOnlinePlayerNames());
    }
}
