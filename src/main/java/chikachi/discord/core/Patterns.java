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

import java.util.regex.Pattern;

public class Patterns {
    public static final Pattern everyonePattern = Pattern.compile("(^|\\W)@everyone\\b");
    public static final Pattern herePattern = Pattern.compile("(^|\\W)@here\\b");

    public static final Pattern mcFormattingPattern = Pattern.compile("§.(^|\\s)(^|.)");
    public static final Pattern customFormattingPattern = Pattern.compile("&([0-9a-fA-F])");

    public static final Pattern boldPattern = Pattern.compile("\\*\\*(.*)\\*\\*");
    public static final Pattern underlinePattern = Pattern.compile("__(.*)__");
    public static final Pattern italicPattern = Pattern.compile("\\*(.*)\\*");
    public static final Pattern italicMePattern = Pattern.compile("_(.*)_");
    public static final Pattern lineThroughPattern = Pattern.compile("~~(.*)~~");
    public static final Pattern singleCodePattern = Pattern.compile("`(.*)`");
    public static final Pattern multiCodePattern = Pattern.compile("```(.*)```");

    public static final Pattern tagPattern = Pattern.compile("@([^\\s])");
}