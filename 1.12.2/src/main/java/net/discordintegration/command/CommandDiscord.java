package net.discordintegration.command;

import mcp.MethodsReturnNonnullByDefault;
import net.discordintegration.core.config.Configuration;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CommandDiscord extends CommandTreeBase {
    public CommandDiscord() {
        this.addSubcommand(new SubCommandConfig());
        this.addSubcommand(new SubCommandOnline());
        this.addSubcommand(new SubCommandTps());
        this.addSubcommand(new SubCommandUnstuck());
        this.addSubcommand(new SubCommandUptime());
        this.addSubcommand(new SubCommandLink());
        this.addSubcommand(new SubCommandUnlink());
    }

    @Override
    public String getName() {
        return "discord";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/discord <config|online" + (Configuration.getConfig().discord.allowLinking ? "|link|unlink" : "") + "|tps|unstuck|uptime> [options]";
    }
}
