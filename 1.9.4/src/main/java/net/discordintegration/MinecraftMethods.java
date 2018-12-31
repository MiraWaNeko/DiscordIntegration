package net.discordintegration;

import net.discordintegration.core.CoreUtils;
import net.discordintegration.core.bridge.minecraft.IMinecraftMethods;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.stream.Stream;

public class MinecraftMethods implements IMinecraftMethods {
    @Override
    @Nullable
    public String getPlayerNameByUUID(@NotNull UUID uuid) {
        EntityPlayer playerEntityByUUID = DimensionManager.getWorlds()[0].getPlayerEntityByUUID(uuid);
        if (playerEntityByUUID == null)
            return null;
        return playerEntityByUUID.getDisplayName().getUnformattedText();
    }

    @Override
    public String translate(String input) {
        //noinspection deprecation
        return I18n.translateToLocalFormatted(input, "KEY");
    }

    private static Stream<EntityPlayerMP> getOnlinePlayerStream() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerList()
            .stream()
            .filter(player -> !player.getDisplayNameString().startsWith("@"))
            .filter(EntityPlayerMP.class::isInstance);
    }

    @Override
    public String[] getOnlinePlayerNames() {
        return getOnlinePlayerStream()
            .map(EntityPlayer::getDisplayNameString)
            .toArray(String[]::new);
    }

    @Override
    public long getOnlinePlayerCount() {
        return getOnlinePlayerStream().count();
    }

    @Override
    public int getMaxPlayerCount() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getMaxPlayers();
    }

    @Override
    public int getUniquePlayerCount() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getUsernames().length;
    }

    @Override
    public double getAverageTickCount() {
        return CoreUtils.mean(FMLCommonHandler.instance().getMinecraftServerInstance().tickTimeArray) * 1.0E-6D;
    }

    @Override
    public double getAverageTPS() {
        return Math.min(1000.0 / getAverageTickCount(), 20);
    }
}
