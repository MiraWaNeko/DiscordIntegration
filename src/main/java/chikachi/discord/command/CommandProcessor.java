package chikachi.discord.command;

import chikachi.discord.config.Configuration;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

class CommandProcessor {
    static void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "reload":
                    Configuration.load();
                    sender.addChatMessage(new ChatComponentText("Config reloaded"));
                    return;
            }
        }

        sender.addChatMessage(new ChatComponentText("Unknown command - Available commands: reload"));
    }
}
