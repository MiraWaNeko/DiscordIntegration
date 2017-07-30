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

package chikachi.discord;

import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.core.entities.User;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import java.util.UUID;

public class DiscordFakePlayer extends FakePlayer {
    public DiscordFakePlayer(WorldServer world, GameProfile name) {
        super(world, name);
        if (world.getMinecraftServer() != null) {
            this.connection = new DiscordFakeNetServerHandler(world.getMinecraftServer(), new NetworkManager(EnumPacketDirection.SERVERBOUND), this);
        }
    }

    DiscordFakePlayer(MinecraftServer minecraftServer, User user) {
        this(minecraftServer.worldServers[0], new GameProfile(UUID.randomUUID(), "@" + user.getName()));

        this.dimension = Integer.MIN_VALUE;
    }
}
