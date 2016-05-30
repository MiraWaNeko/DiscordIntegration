/**
 * Copyright (C) 2016 Chikachi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package chikachi.discord.command.discord;

import chikachi.discord.DiscordClient;
import com.google.common.base.Joiner;
import net.dv8tion.jda.entities.User;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.LongStream;

public class TpsCommandConfig extends CommandConfig {
    private static final DecimalFormat timeFormatter = new DecimalFormat("########0.000");

    public TpsCommandConfig() {
        super("tps", false);
    }

    private static String padLeft(String s, int n) {
        int spaces = n - s.length();

        if (spaces < 1) {
            return s;
        }

        String padding = new String(new char[spaces]).replace("\0", " ");
        return padding + s;
    }

    private static String padRight(String s, int n) {
        int spaces = n - s.length();

        if (spaces < 1) {
            return s;
        }

        String padding = new String(new char[spaces]).replace("\0", " ");
        return s + padding;
    }

    private static Integer getMinValue(Set<Integer> values) {
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

    private static Integer getMaxValue(Set<Integer> values) {
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

    private static Integer getMinLength(Collection<String> strings) {
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

    private static Integer getMaxLength(Collection<String> strings) {
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

    @Override
    public void execute(MinecraftServer minecraftServer, User user, List<String> args) {
        List<String> tpsTimes = new ArrayList<>();

        Integer[] dimensionIds = DimensionManager.getIDs();
        HashMap<Integer, String> dimensionMap = new HashMap<>();

        for (Integer dimensionId : dimensionIds) {
            dimensionMap.put(dimensionId, DimensionManager.getProviderType(dimensionId).getName());
        }

        int maxDimensionIdLength = Math.max(getMinValue(dimensionMap.keySet()).toString().length(), getMaxValue(dimensionMap.keySet()).toString().length());
        int maxDimensionNameLength = Math.max(getMinLength(dimensionMap.values()), getMaxLength(dimensionMap.values()));

        SortedSet<Integer> sortedDimensionIds = new TreeSet<>(dimensionMap.keySet());

        for (Integer dimensionId : sortedDimensionIds) {
            String dimensionName = dimensionMap.get(dimensionId);

            double worldTickTime = this.mean(minecraftServer.worldTickTimes.get(dimensionId)) * 1.0E-6D;
            double worldTPS = Math.min(1000.0 / worldTickTime, 20);

            tpsTimes.add(
                    String.format(
                            "%s : Mean tick time: %s ms. Mean TPS: %s",
                            String.format(
                                    "Dim %s %s",
                                    padLeft(dimensionId + "", maxDimensionIdLength),
                                    padRight(dimensionName, maxDimensionNameLength)
                            ),
                            padLeft(timeFormatter.format(worldTickTime), 6),
                            padLeft(timeFormatter.format(worldTPS), 6)
                    )
            );
        }

        double meanTickTime = this.mean(minecraftServer.tickTimeArray) * 1.0E-6D;
        double meanTPS = Math.min(1000.0 / meanTickTime, 20);
        tpsTimes.add(
                String.format(
                        "%s : Mean tick time: %s ms. Mean TPS: %s",
                        padRight("Overall", maxDimensionIdLength + maxDimensionNameLength + 5),
                        padLeft(timeFormatter.format(meanTickTime), 6),
                        padLeft(timeFormatter.format(meanTPS), 6)
                )
        );

        DiscordClient.getInstance().sendMessage(
                String.format(
                        "\n```\n%s\n```",
                        Joiner.on("\n").join(tpsTimes)
                ).replace("\\:", ":")
        );
    }

    private long mean(long[] values) {
        return LongStream.of(values).sum() / values.length;
    }
}
