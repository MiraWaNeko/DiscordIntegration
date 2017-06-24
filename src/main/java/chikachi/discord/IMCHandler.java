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

import chikachi.discord.core.DiscordClient;
import chikachi.discord.core.Message;
import chikachi.discord.core.config.types.MessageConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import java.util.ArrayList;
import java.util.List;

public class IMCHandler {
    private static List<String> registeredIMCMods = new ArrayList<>();

    private static NBTTagCompound getErrorNBT(String method, String message) {
        NBTTagCompound error = new NBTTagCompound();

        error.setString("method", method);
        error.setString("message", message);

        return error;
    }

    public static void onMessageReceived(FMLInterModComms.IMCMessage imcMessage) {
        String modId = imcMessage.getSender();
        if (imcMessage.isStringMessage()) {
            onMessageReceived(modId, imcMessage.key, imcMessage.getStringValue());
        } else if (imcMessage.isNBTMessage()) {
            onMessageReceived(modId, imcMessage.key, imcMessage.getNBTValue());
        }
    }

    @SuppressWarnings("UnusedParameters")
    public static void onMessageReceived(String modId, String key, String message) {
        if (key.equalsIgnoreCase("registerListener")) {
            if (!registeredIMCMods.contains(modId)) {
                DiscordIntegrationLogger.Log(String.format(
                    "Added %s as listener",
                    modId
                ));

                registeredIMCMods.add(modId);
            } else {
                FMLInterModComms.sendRuntimeMessage(
                    DiscordIntegration.instance,
                    modId,
                    "error",
                    getErrorNBT("registerListener", "Already registered")
                );
            }
        } else if (key.equalsIgnoreCase("unregisterListener")) {
            if (registeredIMCMods.contains(modId)) {
                DiscordIntegrationLogger.Log(String.format(
                    "Removed %s as listener",
                    modId
                ));

                registeredIMCMods.remove(modId);
            } else {
                FMLInterModComms.sendRuntimeMessage(
                    DiscordIntegration.instance,
                    modId,
                    "error",
                    getErrorNBT("registerListener", "Already unregistered")
                );
            }
        }
    }

    public static void onMessageReceived(String modId, String key, NBTTagCompound message) {
        if (key.equalsIgnoreCase("sendMessage")) {
            if (!message.hasKey("message") || message.getString("message").trim().length() == 0) {
                FMLInterModComms.sendRuntimeMessage(
                    DiscordIntegration.instance,
                    modId,
                    "error",
                    getErrorNBT("sendMessage", "Missing message")
                );
                return;
            }

            if (!message.hasKey("channel") || message.getLong("channel") == 0) {
                FMLInterModComms.sendRuntimeMessage(
                    DiscordIntegration.instance,
                    modId,
                    "error",
                    getErrorNBT("sendMessage", "Missing channel")
                );
                return;
            }

            DiscordClient.getInstance().broadcast(
                new Message(
                    modId,
                    new MessageConfig(
                        message.getString("message")
                    )
                ),
                message.getLong("channel")
            );
        }
    }

    public static boolean haveListeners() {
        return registeredIMCMods.size() > 0;
    }

    public static List<String> getRegisteredIMCMods() {
        return registeredIMCMods;
    }

    public static void emitMessage(String key, String message) {
        registeredIMCMods.forEach(registeredIMCMod -> FMLInterModComms.sendRuntimeMessage(
            DiscordIntegration.instance,
            registeredIMCMod,
            key,
            message
        ));
    }

    public static void emitMessage(String key, NBTTagCompound message) {
        registeredIMCMods.forEach(registeredIMCMod -> FMLInterModComms.sendRuntimeMessage(
            DiscordIntegration.instance,
            registeredIMCMod,
            key,
            message
        ));
    }
}
