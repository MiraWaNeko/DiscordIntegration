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
import chikachi.discord.core.Proxy;
import chikachi.discord.core.DiscordClient;
import chikachi.discord.core.Patterns;
import chikachi.discord.listener.DiscordListener;
import chikachi.discord.listener.MinecraftListener;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

@Mod(modid = CoreConstants.MODID, name = CoreConstants.MODNAME, version = CoreConstants.VERSION, serverSideOnly = true, acceptableRemoteVersions = "*")
public class DiscordIntegration {
    @Mod.Instance
    public static DiscordIntegration instance;

    private static Proxy proxy = new Proxy();

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        proxy.onPreInit(event.getModConfigurationDirectory());

        addPatterns();

        MinecraftForge.EVENT_BUS.register(new MinecraftListener());
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        event.buildSoftDependProxy("Dynmap", "chikachi.discord.integration.DynmapIntegration");
    }

    @Mod.EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        proxy.onServerStarting();

        DiscordClient.getInstance().addEventListener(new DiscordListener());

        event.registerServerCommand(new CommandDiscord());
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        proxy.onServerStarted();
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        proxy.onServerStopping();
    }

    @Mod.EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        proxy.onServerStopped();
    }

    @Mod.EventHandler
    public void imcReceived(FMLInterModComms.IMCEvent event) {
        event.getMessages().forEach(IMCHandler::onMessageReceived);
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
                        this.bold = !this.bold;
                        modifier = this.bold ? "\u00a7l" : resetString();
                        break;
                    case "*":
                    case "_":
                        this.italic = !this.italic;
                        modifier = this.italic ? "\u00a7o" : resetString();
                        break;
                    case "__":
                        this.underline = !this.underline;
                        modifier = this.underline ? "\u00a7n" : resetString();
                        break;
                    case "~~":
                        this.strikethrough = !this.strikethrough;
                        modifier = this.strikethrough ? "\u00a7m" : resetString();
                        break;
                }

                return modifier;
            }

            private String resetString() {
                String text = TextFormatting.RESET.toString();
                if (this.strikethrough) {
                    text += "\u00a7m";
                }
                if (this.underline) {
                    text += "\u00a7n";
                }
                if (this.italic) {
                    text += "\u00a7o";
                }
                if (this.bold) {
                    text += "\u00a7l";
                }
                return text;
            }

            @Override
            public String post(String text) {
                text = Pattern.compile("(?i)\u00a7r(\u00a7([0-9A-FK-OR]))+\u00a7r").matcher(text).replaceAll(TextFormatting.RESET.toString());
                return text;
            }
        });

        Patterns.addDiscordFormattingPattern(Patterns.minecraftCodePattern, new Patterns.ReplacementCallback() {
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
                            this.bold = true;
                            modifier = "**";
                        } else if (textFormatting.equals(TextFormatting.ITALIC)) {
                            this.italic = true;
                            modifier = "*";
                        } else if (textFormatting.equals(TextFormatting.UNDERLINE)) {
                            this.underline = true;
                            modifier = "__";
                        } else if (textFormatting.equals(TextFormatting.STRIKETHROUGH)) {
                            this.strikethrough = true;
                            modifier = "~~";
                        } else if (textFormatting.equals(TextFormatting.RESET)) {
                            modifier = "";
                            if (this.bold) {
                                this.bold = false;
                                modifier += "**";
                            }
                            if (this.italic) {
                                this.italic = false;
                                modifier += "*";
                            }
                            if (this.underline) {
                                this.underline = false;
                                modifier += "__";
                            }
                            if (this.strikethrough) {
                                this.strikethrough = false;
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
                if (this.strikethrough) {
                    text += "~~";
                    this.strikethrough = false;
                }
                if (this.underline) {
                    text += "__";
                    this.underline = false;
                }
                if (this.italic) {
                    text += "*";
                    this.italic = false;
                }
                if (this.bold) {
                    text += "**";
                    this.bold = false;
                }
                return text.replaceAll("\\*\\*\\*\\*\\*", "*");
            }
        });
    }
}
