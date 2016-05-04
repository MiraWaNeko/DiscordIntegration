package chikachi.discord;

import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.entities.User;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.FakePlayer;

import java.util.UUID;

class DiscordFakePlayer extends FakePlayer {
    DiscordFakePlayer(User user) {
        super(MinecraftServer.getServer().worldServers[0], new GameProfile(UUID.randomUUID(), "@" + user.getUsername()));

        this.dimension = Integer.MIN_VALUE;
        this.playerNetServerHandler = new DiscordFakeNetServerHandler(new NetworkManager(EnumPacketDirection.SERVERBOUND), this);
    }
}
