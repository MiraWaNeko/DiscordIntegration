package chikachi.discord;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
        return (String[]) MinecraftInformationHandler
            .getOnlineRealPlayerStream()
            .map(EntityPlayer::getDisplayNameString).toArray();
    }

    public static long getOnlineRealPlayerCount() {
        return getOnlineRealPlayerStream().count();
    }
}
