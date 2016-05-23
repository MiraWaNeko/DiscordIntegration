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

package chikachi.discord.config.experimental;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.server.MinecraftServer;

class DiscordFakeNetServerHandler extends NetHandlerPlayServer {
    DiscordFakeNetServerHandler(NetworkManager networkManagerIn, EntityPlayerMP playerIn) {
        super(null, networkManagerIn, playerIn);
    }

    @Override
    public void sendPacket(Packet packetIn) {
    }

    @Override
    public void update() {
    }

    @Override
    public void kickPlayerFromServer(String reason) {
        MinecraftServer.getServer().getConfigurationManager().playerEntityList.remove(playerEntity);
    }

    @Override
    public void processKeepAlive(C00PacketKeepAlive packetIn) {

    }

    @Override
    public NetworkManager getNetworkManager() {
        return null;
    }

    @Override
    public void handleResourcePackStatus(C19PacketResourcePackStatus packetIn) {
    }

    @Override
    public void processClickWindow(C0EPacketClickWindow packetIn) {

    }

    @Override
    public void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement packetIn) {

    }

    @Override
    public void processVanilla250Packet(C17PacketCustomPayload packetIn) {

    }


}
