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
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;

public class DiscordCommandSender extends FakePlayer {
    private static final UUID playerUUID = UUID.fromString("828653ca-0185-43d4-b26d-620a7f016be6");

    public DiscordCommandSender(User user) {
        super(FMLCommonHandler.instance().getMinecraftServerInstance().worlds[0], new GameProfile(playerUUID, "@" + user.getName()));
    }

    public DiscordCommandSender(String name) {
        super(FMLCommonHandler.instance().getMinecraftServerInstance().worlds[0], new GameProfile(playerUUID, "@" + name));
    }

    @Override
    public boolean canUseCommand(int i, String s) {
        return true;
    }
}
