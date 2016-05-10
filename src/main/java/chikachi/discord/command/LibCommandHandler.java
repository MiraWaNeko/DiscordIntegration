package chikachi.discord.command;

import chikachi.lib.common.command.sub.CommandChikachiBase;
import chikachi.lib.common.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@SuppressWarnings("unused")
public class LibCommandHandler extends CommandChikachiBase {
    public LibCommandHandler() {
        super("discord");
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        CommandProcessor.processCommand(sender, args);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return !(sender instanceof EntityPlayer) || PlayerUtils.IsOP((EntityPlayer) sender);
    }
}
