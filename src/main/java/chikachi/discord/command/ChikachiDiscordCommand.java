package chikachi.discord.command;

import chikachi.discord.ChikachiDiscord;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class ChikachiDiscordCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "discord";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/discord [reload]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        ChikachiDiscord.processCommand(sender, args);
    }
}
