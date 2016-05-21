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
