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
import chikachi.discord.core.DiscordIntegrationLogger;
import chikachi.discord.core.Message;
import chikachi.discord.core.config.Configuration;
import chikachi.discord.core.config.imc.IMCConfig;
import chikachi.discord.core.config.types.MessageConfig;
import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class IMCHandler {
    private static List<String> registeredIMCMods = new ArrayList<>();

    private static void sendStatusIMC(String modId, boolean success, String method, String message) {
        NBTTagCompound data = new NBTTagCompound();

        data.setString("method", method);
        data.setString("message", message);

        FMLInterModComms.sendRuntimeMessage(
            DiscordIntegration.instance,
            modId,
            success ? "success" : "error",
            data
        );
    }

    static void onMessageReceived(FMLInterModComms.IMCMessage imcMessage) {
        String modId = imcMessage.getSender();
        if (imcMessage.isStringMessage()) {
            onMessageReceived(modId, imcMessage.key, imcMessage.getStringValue());
        } else if (imcMessage.isNBTMessage()) {
            onMessageReceived(modId, imcMessage.key, imcMessage.getNBTValue());
        }
    }

    @SuppressWarnings("UnusedParameters")
    private static void onMessageReceived(String modId, String key, String message) {
        IMCConfig imcConfig = Configuration.getConfig().imc;
        if (key.equalsIgnoreCase("registerListener")) {
            if (!registeredIMCMods.contains(modId)) {
                if (imcConfig.isAllowed(modId)) {
                    DiscordIntegrationLogger.Log(
                        String.format(
                            "Added %s as listener",
                            modId
                        )
                    );

                    sendStatusIMC(modId, true, key, "Registered");

                    registeredIMCMods.add(modId);
                } else {
                    notAllowed(modId, key, "register as IMC listener");
                }
            } else if (imcConfig.isAllowed(modId)) {
                sendStatusIMC(modId, false, key, "Already registered");
            }
        } else if (key.equalsIgnoreCase("unregisterListener")) {
            if (registeredIMCMods.contains(modId)) {
                DiscordIntegrationLogger.Log(
                    String.format(
                        "Removed %s as listener",
                        modId
                    )
                );

                sendStatusIMC(modId, true, key, "Unregistered");

                registeredIMCMods.remove(modId);
            } else if (imcConfig.isAllowed(modId)) {
                sendStatusIMC(modId, false, key, "Already unregistered");
            }
        }
    }

    private static void onMessageReceived(String modId, String key, NBTTagCompound message) {
        IMCConfig imcConfig = Configuration.getConfig().imc;
        if (key.equalsIgnoreCase("sendMessage")) {
            if (!imcConfig.isAllowed(modId)) {
                notAllowed(modId, key, "send a message");
                return;
            }

            if (!message.hasKey("message") || message.getString("message").trim().length() == 0) {
                sendStatusIMC(modId, false, key, "Missing message");
                return;
            }

            if (!message.hasKey("channel") || message.getLong("channel") == 0) {
                sendStatusIMC(modId, false, key, "Missing channel");
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

            sendStatusIMC(modId, true, key, "Sent");
        }
    }

    private static void notAllowed(String modId, String key, String action) {
        IMCConfig imcConfig = Configuration.getConfig().imc;

        DiscordIntegrationLogger.Log(
            String.format(
                "%s tried to %s but %s",
                modId,
                action,
                imcConfig.isWhitelist() ? "wasn't on the whitelist" : "was on the blacklist"
            )
        );

        sendStatusIMC(modId, false, key, "Not Allowed");
    }

    public static boolean haveListeners() {
        return registeredIMCMods.size() > 0;
    }

    @SuppressWarnings("unused")
    public static List<String> getRegisteredIMCMods() {
        return registeredIMCMods;
    }

    @SuppressWarnings("unused")
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
