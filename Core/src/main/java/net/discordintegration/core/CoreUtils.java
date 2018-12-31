/*
 * Copyright (C) 2018 Chikachi and other contributors
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

package net.discordintegration.core;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

@SuppressWarnings({"unused", "WeakerAccess"})
public class CoreUtils {
    public static void addPatterns() {
        Patterns.clearCustomPatterns();

        Patterns.addMinecraftFormattingUnifyPattern(Patterns.fakeMinecraftCodePattern, new Patterns.ReplacementCallback() {
            @Override
            public String pre(String text) {
                return text;
            }

            @Override
            public String replace(ArrayList<String> groups) {
                MinecraftFormattingCodes minecraftFormattingCode = MinecraftFormattingCodes.getByCode(groups.get(1).charAt(0));
                if (minecraftFormattingCode != null) {
                    return minecraftFormattingCode.toString();
                }
                return groups.get(0);
            }

            @Override
            public String post(String text) {
                return text;
            }
        });

        Patterns.addDiscordToMinecraftFormattingPattern(Pattern.compile("(?i)(\\*\\*|\\*|__|_|~~|`|```)"), new Patterns.ReplacementCallback() {
            private boolean bold = false;
            private boolean italic = false;
            private String lastItalic = "";
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
                        modifier = this.bold ? MinecraftFormattingCodes.BOLD.toString() : resetString();
                        break;
                    case "*":
                    case "_":
                        if (this.italic && !modifier.equals(this.lastItalic)) {
                            return modifier;
                        }
                        this.lastItalic = modifier;
                        this.italic = !this.italic;
                        modifier = this.italic ? MinecraftFormattingCodes.ITALIC.toString() : resetString();
                        break;
                    case "__":
                        this.underline = !this.underline;
                        modifier = this.underline ? MinecraftFormattingCodes.UNDERLINE.toString() : resetString();
                        break;
                    case "~~":
                        this.strikethrough = !this.strikethrough;
                        modifier = this.strikethrough ? MinecraftFormattingCodes.STRIKETHROUGH.toString() : resetString();
                        break;
                    default:
                        break;
                }

                return modifier;
            }

            private String resetString() {
                String text = MinecraftFormattingCodes.RESET.toString();
                if (this.strikethrough) {
                    text += MinecraftFormattingCodes.STRIKETHROUGH.toString();
                }
                if (this.underline) {
                    text += MinecraftFormattingCodes.UNDERLINE.toString();
                }
                if (this.italic) {
                    text += MinecraftFormattingCodes.ITALIC.toString();
                }
                if (this.bold) {
                    text += MinecraftFormattingCodes.BOLD.toString();
                }
                return text;
            }

            @Override
            public String post(String text) {
                if (this.strikethrough) {
                    int lastStrikethrough = text.lastIndexOf(MinecraftFormattingCodes.STRIKETHROUGH.toString());
                    text = text.substring(0, lastStrikethrough) + "~~" + text.substring(lastStrikethrough + 2);
                    this.strikethrough = false;
                }
                if (this.underline) {
                    int lastUnderline = text.lastIndexOf(MinecraftFormattingCodes.UNDERLINE.toString());
                    text = text.substring(0, lastUnderline) + "__" + text.substring(lastUnderline + 2);
                    this.underline = false;
                }
                if (this.italic) {
                    int lastItalic = text.lastIndexOf(MinecraftFormattingCodes.ITALIC.toString());
                    text = text.substring(0, lastItalic) + this.lastItalic + text.substring(lastItalic + 2);
                    this.italic = false;
                }
                if (this.bold) {
                    int lastBold = text.lastIndexOf(MinecraftFormattingCodes.BOLD.toString());
                    text = text.substring(0, lastBold) + "**" + text.substring(lastBold + 2);
                    this.bold = false;
                }
                text = Pattern.compile("(?i)\u00a7r(\u00a7([0-9A-FK-OR]))+\u00a7r").matcher(text).replaceAll(MinecraftFormattingCodes.RESET.toString());
                return text;
            }
        });

        Patterns.addMinecraftToDiscordFormattingPattern(Patterns.minecraftCodePattern, new Patterns.ReplacementCallback() {
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

                if (modifier.equalsIgnoreCase(MinecraftFormattingCodes.BOLD.toString())) {
                    this.bold = true;
                    modifier = "**";
                } else if (modifier.equalsIgnoreCase(MinecraftFormattingCodes.ITALIC.toString())) {
                    this.italic = true;
                    modifier = "*";
                } else if (modifier.equalsIgnoreCase(MinecraftFormattingCodes.UNDERLINE.toString())) {
                    this.underline = true;
                    modifier = "__";
                } else if (modifier.equalsIgnoreCase(MinecraftFormattingCodes.STRIKETHROUGH.toString())) {
                    this.strikethrough = true;
                    modifier = "~~";
                } else if (modifier.equalsIgnoreCase(MinecraftFormattingCodes.RESET.toString())) {
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

    public static String replace(Map<String, String> replaceMap, String text) {
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

    public static String tpsToColorString(double tps, boolean isDiscord) {
        if (19 <= tps) {
            return isDiscord ? "+ " : MinecraftFormattingCodes.GREEN.toString();
        } else if (15 <= tps) {
            return isDiscord ? "! " : MinecraftFormattingCodes.YELLOW.toString();
        } else {
            return isDiscord ? "- " : MinecraftFormattingCodes.RED.toString();
        }
    }

    public static String padLeft(String s, int n) {
        int spaces = n - s.length();

        if (spaces < 1) {
            return s;
        }

        String padding = new String(new char[spaces]).replace("\0", " ");
        return padding + s;
    }

    public static String padRight(String s, int n) {
        int spaces = n - s.length();

        if (spaces < 1) {
            return s;
        }

        String padding = new String(new char[spaces]).replace("\0", " ");
        return s + padding;
    }

    public static Integer getMinValue(Set<Integer> values) {
        if (values.size() == 0) {
            return 0;
        }

        Integer value = null;
        for (Integer val : values) {
            if (value == null) {
                value = val;
            } else if (val < value) {
                value = val;
            }
        }
        return value;
    }

    public static Integer getMaxValue(Set<Integer> values) {
        if (values.size() == 0) {
            return 0;
        }

        Integer value = null;
        for (Integer val : values) {
            if (value == null) {
                value = val;
            } else if (value < val) {
                value = val;
            }
        }
        return value;
    }

    public static Integer getMinLength(Collection<String> strings) {
        if (strings.size() == 0) {
            return 0;
        }

        Integer length = null;
        for (String string : strings) {
            int stringLength = string.length();
            if (length == null) {
                length = stringLength;
            } else if (stringLength < length) {
                length = stringLength;
            }
        }
        return length;
    }

    public static Integer getMaxLength(Collection<String> strings) {
        if (strings.size() == 0) {
            return 0;
        }

        Integer length = null;
        for (String string : strings) {
            int stringLength = string.length();
            if (length == null) {
                length = stringLength;
            } else if (length < stringLength) {
                length = stringLength;
            }
        }
        return length;
    }

    public static long mean(long[] values) {
        return LongStream.of(values).sum() / values.length;
    }

    public static String getAvatarUrl(String minecraftUsername) {
        return String.format("https://minotar.net/helm/%s/128.png", minecraftUsername);
    }
}
