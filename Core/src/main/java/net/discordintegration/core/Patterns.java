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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Patterns {
    public static final Pattern minecraftCodePattern = Pattern.compile("(?i)(\u00a7[0-9A-FK-OR])");
    public static final Pattern fakeMinecraftCodePattern = Pattern.compile("(?i)(?<!@)&([0-9A-FK-OR])");

    static final Pattern tagPattern = Pattern.compile("(^|\\s)@([^\\s#]+)(#[0-9]+)?");
    private static final HashMap<Pattern, ReplacementCallback> minecraftToDiscordFormattingPatterns = new HashMap<>();
    private static final HashMap<Pattern, ReplacementCallback> discordToMinecraftFormattingPatterns = new HashMap<>();
    private static final HashMap<Pattern, ReplacementCallback> minecraftFormattingUnifyPatterns = new HashMap<>();

    static void clearCustomPatterns() {
        minecraftToDiscordFormattingPatterns.clear();
        discordToMinecraftFormattingPatterns.clear();
    }

    static void addDiscordToMinecraftFormattingPattern(Pattern pattern, ReplacementCallback replacement) {
        discordToMinecraftFormattingPatterns.put(pattern, replacement);
    }

    static void addMinecraftToDiscordFormattingPattern(Pattern pattern, ReplacementCallback replacement) {
        minecraftToDiscordFormattingPatterns.put(pattern, replacement);
    }

    /**
     * Add pattern that converts other Minecraft formattings to the standard Minecraft formatting
     * For example: &f to §f
     *
     * @param pattern     Pattern
     * @param replacement Replacement handler
     */
    static void addMinecraftFormattingUnifyPattern(Pattern pattern, ReplacementCallback replacement) {
        minecraftFormattingUnifyPatterns.put(pattern, replacement);
    }

    public static String discordToMinecraft(String content) {
        if (content == null) {
            return "";
        }

        for (Map.Entry<Pattern, ReplacementCallback> entry : discordToMinecraftFormattingPatterns.entrySet()) {
            content = executeReplacement(content, entry);
        }

        content = unifyMinecraftFormatting(content);

        return content;
    }

    public static String minecraftToDiscord(String content) {
        if (content == null) {
            return "";
        }

        content = unifyMinecraftFormatting(content);

        for (Map.Entry<Pattern, ReplacementCallback> entry : minecraftToDiscordFormattingPatterns.entrySet()) {
            content = executeReplacement(content, entry);
        }

        return content;
    }

    public static String unifyMinecraftFormatting(String content) {
        if (content == null) {
            return "";
        }

        for (Map.Entry<Pattern, ReplacementCallback> entry : minecraftFormattingUnifyPatterns.entrySet()) {
            content = executeReplacement(content, entry);
        }

        return content;
    }

    @NotNull
    private static String executeReplacement(String content, Map.Entry<Pattern, ReplacementCallback> entry) {
        ReplacementCallback replacer = entry.getValue();
        content = replacer.pre(content);
        Matcher matcher = entry.getKey().matcher(content);

        if (matcher.find()) {
            StringBuffer sb = new StringBuffer();
            do {
                ArrayList<String> groups = new ArrayList<>();
                for (int i = 0, j = matcher.groupCount(); i <= j; i++) {
                    groups.add(matcher.group(i));
                }
                matcher.appendReplacement(sb, replacer.replace(groups));
            } while (matcher.find());
            matcher.appendTail(sb);

            content = replacer.post(sb.toString());
        }

        return content;
    }

    public interface ReplacementCallback {
        String pre(String text);

        String replace(ArrayList<String> groups);

        String post(String text);
    }
}
