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

package chikachi.discord;

import com.google.common.base.Joiner;
import net.dv8tion.jda.entities.User;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class Utils {
    public static NBTTagCompound UserToNBT(User user) {
        NBTTagCompound tagCompound = new NBTTagCompound();

        tagCompound.setString("id", user.getId());
        tagCompound.setString("username", user.getUsername());

        return tagCompound;
    }

    public static NBTTagCompound UserToNBT(EntityPlayer player) {
        NBTTagCompound tagCompound = new NBTTagCompound();

        tagCompound.setString("id", player.getGameProfile().getId().toString());
        tagCompound.setString("username", player.getDisplayNameString());

        return tagCompound;
    }

    public static NBTTagCompound UserToNBT(ICommandSender sender) {
        NBTTagCompound tagCompound = new NBTTagCompound();

        tagCompound.setString("id", "-1");
        tagCompound.setString("username", sender.getName());

        return tagCompound;
    }

    public static String Replace(Map<String, String> replaceMap, String text) {
        String[] words = text.split(" ");

        Set<Map.Entry<String, String>> entries = replaceMap.entrySet();

        for (int i = 0, j = words.length; i < j; i++) {
            String word = words[i];

            for (Map.Entry<String, String> entry : entries) {
                if (word.equals(entry.getKey())) {
                    words[i] = entry.getValue();
                }
            }
        }

        return Joiner.on(" ").join(words);
    }
}
