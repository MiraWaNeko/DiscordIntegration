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

        Patterns.addDiscordToMinecraftPattern(Patterns.strikeThroughPattern, TextFormatting.STRIKETHROUGH + "$1\ufffd");
        Patterns.addDiscordToMinecraftPattern(Patterns.underlinePattern, TextFormatting.UNDERLINE + "$1\ufffd");
        Patterns.addDiscordToMinecraftPattern(Patterns.italicMePattern, TextFormatting.ITALIC + "$1\ufffd");
        Patterns.addDiscordToMinecraftPattern(Patterns.italicPattern, TextFormatting.ITALIC + "$1\ufffd");
        Patterns.addDiscordToMinecraftPattern(Patterns.boldPattern, TextFormatting.BOLD + "$1\ufffd");
        Patterns.addDiscordToMinecraftPattern(Patterns.multiCodePattern, "$1");
        Patterns.addDiscordToMinecraftPattern(Patterns.singleCodePattern, "$1");

        Patterns.addMinecraftFormattingPattern(Pattern.compile("(?i)((\u00c2?[\u00a7&]([0-9A-FK-OR]))|\ufffd)"), new Patterns.ReplacementCallback() {
            private ArrayList<TextFormatting> layers = new ArrayList<>();

            @Override
            public String pre(String text) {
                return text;
            }

            @Override
            public String replace(ArrayList<String> groups) {
                if (groups.get(0).charAt(0) == '\ufffd') {
                    if (layers.size() > 0) {
                        layers.remove(layers.size() - 1);
                        return TextFormatting.RESET + Joiner.on("").join(layers.stream().map(TextFormatting::toString).collect(Collectors.toList()));
                    }

                    return TextFormatting.RESET.toString();
                }

                String modifier = String.valueOf("\u00a7") + groups.get(2).substring(groups.get(2).length() - 1);

                for (TextFormatting textFormatting : TextFormatting.values()) {
                    if (modifier.equalsIgnoreCase(textFormatting.toString())) {
                        layers.add(textFormatting);
                        break;
                    }
                }

                return modifier;
            }

            @Override
            public String post(String text) {
                layers.clear();
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
                if (bold) {
                    text += "**";
                }
                if (italic) {
                    text += "*";
                }
                if (underline) {
                    text += "__";
                }
                if (strikethrough) {
                    text += "~~";
                }
                bold = false;
                italic = false;
                underline = false;
                strikethrough = false;
                return text;
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
