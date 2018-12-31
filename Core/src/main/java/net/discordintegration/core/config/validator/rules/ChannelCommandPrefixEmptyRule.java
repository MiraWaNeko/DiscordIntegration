package net.discordintegration.core.config.validator.rules;

import com.google.common.base.Joiner;
import net.discordintegration.core.config.Configuration;
import net.discordintegration.core.config.validator.IConfigurationValidationRule;
import net.discordintegration.core.config.validator.ValidationResult;

import java.util.ArrayList;
import java.util.List;

public class ChannelCommandPrefixEmptyRule implements IConfigurationValidationRule {
    private String getHint() {
        return "For at least one channel there is 'canExecuteCommands' set to true and the 'commandPrefix' is set to an empty string. " +
            "You should always specify a prefix.";
    }

    @Override
    public ValidationResult validate() {
        List<Long> list = new ArrayList<>();
        Configuration.getConfig().discord.channels.channels.forEach((key, value) -> {
            Boolean canExecuteCommands = value.canExecuteCommands;
            if (canExecuteCommands == null) {
                canExecuteCommands = Configuration.getConfig().discord.channels.generic.canExecuteCommands;
            }

            String commandPrefix = value.commandPrefix;
            if (commandPrefix == null) {
                commandPrefix = Configuration.getConfig().discord.channels.generic.commandPrefix;
            }

            if (canExecuteCommands != null && canExecuteCommands &&
                commandPrefix != null && commandPrefix.trim().equals("")
                ) {
                list.add(key);
            }
        });

        if (list.size() == 0) {
            return new ValidationResult(true, null);
        } else {
            return new ValidationResult(
                false,
                getHint() + " Channel(s): " + Joiner.on(", ").join(list)
            );
        }
    }
}
