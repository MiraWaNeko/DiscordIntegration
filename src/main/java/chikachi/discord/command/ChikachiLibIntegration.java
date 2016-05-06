package chikachi.discord.command;

import chikachi.discord.ChikachiDiscord;
import chikachi.lib.common.command.sub.CommandChikachiBase;
import chikachi.lib.common.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@SuppressWarnings("unused")
public class ChikachiLibIntegration extends CommandChikachiBase {
    public ChikachiLibIntegration() {
        super("discord");
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        ChikachiDiscord.processCommand(sender, args);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return !(sender instanceof EntityPlayer) || PlayerUtils.IsOP((EntityPlayer) sender);
    }
}
