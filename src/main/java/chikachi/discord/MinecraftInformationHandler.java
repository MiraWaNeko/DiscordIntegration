package chikachi.discord;

import chikachi.discord.core.CoreUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.stream.Stream;

public abstract class MinecraftInformationHandler {
    public static Stream<EntityPlayerMP> getOnlineRealPlayerStream() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()
            .stream()
            .filter(player -> !player.getDisplayNameString().startsWith("@"))
            .filter(EntityPlayerMP.class::isInstance);
    }

    public static String[] getOnlineRealPlayerNames() {
        return MinecraftInformationHandler
            .getOnlineRealPlayerStream()
            .map(EntityPlayer::getDisplayNameString)
            .toArray(String[]::new);
    }

    public static long getOnlineRealPlayerCount() {
        return getOnlineRealPlayerStream().count();
    }

    public static int getMaxPlayerCount() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getMaxPlayers();
    }

    public static double getAverageTickCount() {
        MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        return CoreUtils.mean(minecraftServer.tickTimeArray) * 1.0E-6D;
    }

    public static double getAverageTPS() {
        return Math.min(1000.0 / getAverageTickCount(), 20);
    }

}
