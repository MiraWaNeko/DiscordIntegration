package chikachi.discord.config.command;

import chikachi.discord.DiscordClient;
import com.google.common.base.Joiner;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.DimensionManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public class TpsCommandConfig extends CommandConfig {
    private static final DecimalFormat timeFormatter = new DecimalFormat("########0.000");

    public TpsCommandConfig() {
        super("tps", false);
    }

    @Override
    public void execute(List<String> args) {
        MinecraftServer minecraftServer = MinecraftServer.getServer();
        List<String> tpsTimes = new ArrayList<>();

        for (Integer dimId : DimensionManager.getIDs()) {
            double worldTickTime = this.mean(minecraftServer.worldTickTimes.get(dimId)) * 1.0E-6D;
            double worldTPS = Math.min(1000.0 / worldTickTime, 20);
            tpsTimes.add(
                    StatCollector.translateToLocalFormatted(
                            "commands.forge.tps.summary",
                            String.format("Dim %d", dimId),
                            timeFormatter.format(worldTickTime),
                            timeFormatter.format(worldTPS)
                    )
            );
        }

        double meanTickTime = this.mean(minecraftServer.tickTimeArray) * 1.0E-6D;
        double meanTPS = Math.min(1000.0 / meanTickTime, 20);
        tpsTimes.add(
                StatCollector.translateToLocalFormatted(
                        "commands.forge.tps.summary",
                        "Overall",
                        timeFormatter.format(meanTickTime),
                        timeFormatter.format(meanTPS)
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
