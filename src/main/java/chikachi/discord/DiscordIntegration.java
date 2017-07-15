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

import chikachi.discord.command.CommandDiscord;
import chikachi.discord.core.CoreConstants;
import chikachi.discord.core.CoreProxy;
import chikachi.discord.core.DiscordClient;
import chikachi.discord.core.Patterns;
import chikachi.discord.listener.DiscordListener;
import chikachi.discord.listener.MinecraftListener;
import com.google.common.base.Joiner;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Mod(modid = CoreConstants.MODID, name = CoreConstants.MODNAME, version = CoreConstants.VERSION, serverSideOnly = true, acceptableRemoteVersions = "*")
public class DiscordIntegration {
    @Mod.Instance
    public static DiscordIntegration instance;

    private static CoreProxy coreProxy = new CoreProxy();

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        coreProxy.onPreInit(event.getModConfigurationDirectory());

        addPatterns();

        MinecraftForge.EVENT_BUS.register(new MinecraftListener());
    }

    public static void addPatterns() {
        Patterns.clearCustomPatterns();

        Patterns.addMinecraftFormattingPattern(Pattern.compile("(?i)(\\*\\*|\\*|__|_|~~|`|```)"), new Patterns.ReplacementCallback() {
            private boolean bold = false;
            private boolean italic = false;
            private boolean underline = false;
            private boolean strikethrough = false;

            @Override
            public String pre(String text) {
                return text;
            }

            @Override
            public String replace(ArrayList<String> groups) {
                String modifier = groups.get(0);

                switch (modifier) {
                    case "**":
                        bold = !bold;
                        modifier = bold ? TextFormatting.BOLD.toString() : resetString();
                        break;
                    case "*":
                    case "_":
                        italic = !italic;
                        modifier = italic ? TextFormatting.ITALIC.toString() : resetString();
                        break;
                    case "__":
                        underline = !underline;
                        modifier = underline ? TextFormatting.UNDERLINE.toString() : resetString();
                        break;
                    case "~~":
                        strikethrough = !strikethrough;
                        modifier = strikethrough ? TextFormatting.STRIKETHROUGH.toString() : resetString();
                        break;
                }

                return modifier;
            }

            private String resetString() {
                String text = TextFormatting.RESET.toString();
                if (strikethrough) {
                    text += TextFormatting.STRIKETHROUGH.toString();
                }
                if (underline) {
                    text += TextFormatting.UNDERLINE.toString();
                }
                if (italic) {
                    text += TextFormatting.ITALIC.toString();
                }
                if (bold) {
                    text += TextFormatting.BOLD.toString();
                }
                return text;
            }

            @Override
            public String post(String text) {
                text = Pattern.compile("(?i)\u00a7r(\u00a7([0-9A-FK-OR]))+\u00a7r").matcher(text).replaceAll(TextFormatting.RESET.toString());
                return text;
            }
        });

        Patterns.addDiscordFormattingPattern(Pattern.compile("(?i)(\u00a7[0-9A-FK-OR])"), new Patterns.ReplacementCallback() {
            private boolean bold = false;
            private boolean italic = false;
            private boolean underline = false;
            private boolean strikethrough = false;

            @Override
            public String pre(String text) {
                return text;
            }

            @Override
            public String replace(ArrayList<String> groups) {
                String modifier = groups.get(0);

                for (TextFormatting textFormatting : TextFormatting.values()) {
                    if (modifier.equalsIgnoreCase(textFormatting.toString())) {
                        if (textFormatting.equals(TextFormatting.BOLD)) {
                            bold = true;
                            modifier = "**";
                        } else if (textFormatting.equals(TextFormatting.ITALIC)) {
                            italic = true;
                            modifier = "*";
                        } else if (textFormatting.equals(TextFormatting.UNDERLINE)) {
                            underline = true;
                            modifier = "__";
                        } else if (textFormatting.equals(TextFormatting.STRIKETHROUGH)) {
                            strikethrough = true;
                            modifier = "~~";
                        } else if (textFormatting.equals(TextFormatting.RESET)) {
                            modifier = "";
                            if (bold) {
                                bold = false;
                                modifier += "**";
                            }
                            if (italic) {
                                italic = false;
                                modifier += "*";
                            }
                            if (underline) {
                                underline = false;
                                modifier += "__";
                            }
                            if (strikethrough) {
                                strikethrough = false;
                                modifier += "~~";
                            }
                        } else {
                            modifier = "";
                        }
                        break;
                    }
                }

                return modifier;
            }

            @Override
            public String post(String text) {
                if (strikethrough) {
                    text += "~~";
                    strikethrough = false;
                }
                if (underline) {
                    text += "__";
                    underline = false;
                }
                if (italic) {
                    text += "*";
                    italic = false;
                }
                if (bold) {
                    text += "**";
                    bold = false;
                }
                return text.replaceAll("\\*\\*\\*\\*\\*", "*");
            }
        });
    }

    @Mod.EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        coreProxy.onServerStarting();

        DiscordClient.getInstance().addEventListner(new DiscordListener());

        event.registerServerCommand(new CommandDiscord());
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        coreProxy.onServerStarted();
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        coreProxy.onServerStopping();
    }

    @Mod.EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        coreProxy.onServerStopped();
    }

    @Mod.EventHandler
    public void imcReceived(FMLInterModComms.IMCEvent event) {
        event.getMessages().forEach(IMCHandler::onMessageReceived);
    }
}
