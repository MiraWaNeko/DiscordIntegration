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

package chikachi.discord.core;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Patterns {
    public static final Pattern everyonePattern = Pattern.compile("(^|\\W)@everyone\\b");
    public static final Pattern herePattern = Pattern.compile("(^|\\W)@here\\b");

    public static final Pattern boldPattern = Pattern.compile("\\*\\*(.*)\\*\\*");
    public static final Pattern underlinePattern = Pattern.compile("__(.*)__");
    public static final Pattern italicPattern = Pattern.compile("\\*(.*)\\*");
    public static final Pattern italicMePattern = Pattern.compile("_(.*)_");
    public static final Pattern strikeThroughPattern = Pattern.compile("~~(.*)~~");
    public static final Pattern singleCodePattern = Pattern.compile("`(.*)`");
    public static final Pattern multiCodePattern = Pattern.compile("```(.*)```");

    static final Pattern tagPattern = Pattern.compile("@([^\\s]+)");

    private static final HashMap<Pattern, String> discordToMinecraftPatterns = new HashMap<>();
    private static final HashMap<Pattern, String> minecraftToDiscordPatterns = new HashMap<>();
    private static final HashMap<Pattern, ReplacementCallback> discordFormattingPatterns = new HashMap<>();
    private static final HashMap<Pattern, ReplacementCallback> minecraftFormattingPatterns = new HashMap<>();

    public static void clearCustomPatterns() {
        discordFormattingPatterns.clear();
        minecraftToDiscordPatterns.clear();
        discordFormattingPatterns.clear();
        minecraftFormattingPatterns.clear();
    }

    public static void addDiscordToMinecraftPattern(Pattern pattern, String replacement) {
        discordToMinecraftPatterns.put(pattern, replacement);
    }

    public static void addMinecraftToDiscordPattern(Pattern pattern, String replacement) {
        minecraftToDiscordPatterns.put(pattern, replacement);
    }

    public static void addMinecraftFormattingPattern(Pattern pattern, ReplacementCallback replacement) {
        minecraftFormattingPatterns.put(pattern, replacement);
    }

    public static void addDiscordFormattingPattern(Pattern pattern, ReplacementCallback replacement) {
        discordFormattingPatterns.put(pattern, replacement);
    }

    public static String discordToMinecraft(String content) {
        if (content == null) {
            return "";
        }

        for (Map.Entry<Pattern, String> entry : discordToMinecraftPatterns.entrySet()) {
            content = entry.getKey().matcher(content).replaceAll(entry.getValue());
        }

        for (Map.Entry<Pattern, ReplacementCallback> entry : minecraftFormattingPatterns.entrySet()) {
            content = executeReplacement(content, entry);
        }

        return content;
    }

    public static String minecraftToDiscord(String content) {
        if (content == null) {
            return "";
        }

        for (Map.Entry<Pattern, String> entry : minecraftToDiscordPatterns.entrySet()) {
            content = entry.getKey().matcher(content).replaceAll(entry.getValue());
        }

        for (Map.Entry<Pattern, ReplacementCallback> entry : discordFormattingPatterns.entrySet()) {
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
                for (int i = 0, j = matcher.groupCount(); i < j; i++) {
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