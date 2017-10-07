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

import chikachi.discord.core.Patterns;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@SuppressWarnings("EntityConstructor")
@ParametersAreNonnullByDefault
public class DiscordCommandSender extends FakePlayer {
    private static final UUID playerUUID = UUID.fromString("828653ca-0185-43d4-b26d-620a7f016be6");
    private final MessageChannel channel;

    public DiscordCommandSender(MessageChannel channel, User user) {
        super(MinecraftServer.getServer().worldServers[0], new GameProfile(playerUUID, "@" + user.getName()));
        this.channel = channel;
    }

    @SuppressWarnings("unused")
    public DiscordCommandSender(WorldServer world, MessageChannel channel, String name) {
        super(world, new GameProfile(playerUUID, "@" + name));
        this.channel = channel;
    }

    @Override
    public boolean canCommandSenderUseCommand(int i, String s) {
        return true;
    }

    @Override
    public void addChatMessage(IChatComponent component) {
        this.channel.sendMessage(
            Patterns.minecraftCodePattern.matcher(
                component.getUnformattedText()
            ).replaceAll("")
        ).queue();
    }

    @Override
    public void addChatComponentMessage(IChatComponent component) {
        this.channel.sendMessage(
            Patterns.minecraftCodePattern.matcher(
                component.getUnformattedText()
            ).replaceAll("")
        ).queue();
    }
}
