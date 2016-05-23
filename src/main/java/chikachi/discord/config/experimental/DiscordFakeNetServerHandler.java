package chikachi.discord.config.experimental;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.server.MinecraftServer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
class DiscordFakeNetServerHandler extends NetHandlerPlayServer {
    DiscordFakeNetServerHandler(MinecraftServer minecraftServer, NetworkManager networkManager, EntityPlayerMP player) {
        super(minecraftServer, networkManager, player);
    }

    @Override
    public void sendPacket(Packet packet) {
    }

    @Override
    public void update() {
    }

    @Override
    public void kickPlayerFromServer(String reason) {

    }

    @Override
    public void processKeepAlive(CPacketKeepAlive packet) {

    }

    @Override
    public void handleResourcePackStatus(CPacketResourcePackStatus packet) {
    }

    @Override
    public void processClickWindow(CPacketClickWindow packet) {

    }

    @Override
    public void processPlayerBlockPlacement(CPacketPlayerTryUseItem packet) {

    }

    @Override
    public void processCustomPayload(CPacketCustomPayload packet) {

    }


}
