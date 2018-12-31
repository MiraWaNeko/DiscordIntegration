package net.discordintegration.core.bridge.minecraft;

import java.util.UUID;

public interface IMinecraftMethods {
    String getPlayerNameByUUID(UUID uuid);

    String translate(String input);

    String[] getOnlinePlayerNames();

    long getOnlinePlayerCount();

    int getMaxPlayerCount();

    int getUniquePlayerCount();

    double getAverageTickCount();

    double getAverageTPS();
}
