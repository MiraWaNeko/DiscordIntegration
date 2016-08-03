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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import java.util.ArrayList;
import java.util.List;

public class IMCHandler {
    private static List<String> registeredIMCMods = new ArrayList<>();

    public static void onMessageReceived(FMLInterModComms.IMCMessage imcMessage) {
        String modId = imcMessage.getSender();
        if (imcMessage.key.equalsIgnoreCase("registerListener")) {
            if (!registeredIMCMods.contains(modId)) {
                DiscordIntegration.Log(String.format(
                        "Added %s as listener",
                        modId
                ));

                registeredIMCMods.add(modId);
            }
        } else if (imcMessage.key.equalsIgnoreCase("unregisterListener")) {
            if (registeredIMCMods.contains(modId)) {
                DiscordIntegration.Log(String.format(
                        "Removed %s as listener",
                        modId
                ));

                registeredIMCMods.remove(modId);
            }
        } else if (imcMessage.key.equalsIgnoreCase("sendMessage")) {
            if (imcMessage.isNBTMessage()) {
                NBTTagCompound tagCompound = imcMessage.getNBTValue();

                if (tagCompound.hasKey("message")) {
                    DiscordClient.getInstance().sendMessage(
                            String.format(
                                    "[%s] %s",
                                    modId,
                                    tagCompound.getString("message")
                            )
                    );
                }
            }
        }
    }

    public static List<String> getRegisteredIMCMods() {
        return registeredIMCMods;
    }
}
