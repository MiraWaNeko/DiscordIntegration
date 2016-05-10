package chikachi.discord.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class NonLibCommandHandler extends CommandBase {
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
        CommandProcessor.processCommand(sender, args);
    }
}
