package chikachi.discord.command;

import chikachi.discord.DiscordClient;
import chikachi.discord.config.Configuration;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;

class CommandProcessor {
    static void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "reload":
                    boolean shouldTryConnect = Configuration.getToken().length() == 0;

                    Configuration.load();
                    sender.addChatMessage(new TextComponentString("Config reloaded"));

                    if (shouldTryConnect && Configuration.getToken().length() > 0) {
                        DiscordClient.getInstance().connect(sender.getServer());
                    }
                    return;
            }
        }

        sender.addChatMessage(new TextComponentString("Unknown command - Available commands: reload"));
    }
}
