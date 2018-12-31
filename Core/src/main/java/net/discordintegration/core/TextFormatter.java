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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TextFormatter {
    private HashMap<String, String> arguments = new HashMap<>();

    public TextFormatter() {
    }

    public TextFormatter(HashMap<String, String> arguments) {
        this.addArguments(arguments);
    }

    public TextFormatter addArgument(String key, String value) {
        this.arguments.put(key, value);
        return this;
    }

    public TextFormatter addArgument(String key, int value) {
        return this.addArgument(key, Integer.toString(value));
    }

    public TextFormatter addArgument(String key, long value) {
        return this.addArgument(key, Long.toString(value));
    }

    public TextFormatter addArgument(String key, float value) {
        return this.addArgument(key, Float.toString(value));
    }

    public TextFormatter addArgument(String key, double value) {
        DecimalFormat decimalFormat = new DecimalFormat("0.000", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        return this.addArgument(key, decimalFormat.format(value));
    }

    public TextFormatter addArguments(HashMap<String, String> newArguments) {
        this.arguments.putAll(newArguments);
        return this;
    }

    public TextFormatter clearArguments() {
        this.arguments.clear();
        return this;
    }

    public String format(String message) {
        for (Map.Entry<String, String> entry : this.arguments.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }
}
