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

import com.google.common.base.Joiner;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.LongStream;

public class CoreUtils {
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

    public static String tpsToColorString(double tps, boolean isDiscord) {
        if (19 <= tps) {
            return isDiscord ? "+ " : "\u00a7a";
        } else if (15 <= tps) {
            return isDiscord ? "! " : "\u00a7e";
        } else {
            return isDiscord ? "- " : "\u00a7c";
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
}