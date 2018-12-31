package net.discordintegration.core.config.validator.rules;

import com.google.common.base.Joiner;
import net.discordintegration.core.config.Configuration;
import net.discordintegration.core.config.discord.CommandConfig;
import net.discordintegration.core.config.discord.DiscordChannelConfig;
import net.discordintegration.core.config.validator.IConfigurationValidationRule;
import net.discordintegration.core.config.validator.ValidationResult;

import java.util.*;

public class DuplicatedCommandOrAliasRule implements IConfigurationValidationRule {
    private String getHint() {
        return "At least one command or alias is used twice. Please use every command only once.";
    }

    /**
     * Checks for duplicated commands
     *
     * @return true, if no commands are duplicated
     */
    @Override
    public ValidationResult validate() {
        Set<String> invalidCommandsOrAliases = new HashSet<>();

        // Check the global commands first and fill them into the checkMap
        ArrayList<CommandConfig> commands = Configuration.getConfig().discord.channels.generic.commands;
        HashMap<String, Integer> globalCheckMap = new HashMap<>();

        for (CommandConfig command : commands) {
            invalidCommandsOrAliases.addAll(checkCommand(globalCheckMap, command));
        }

        // Now test the channel configurations.
        for (Map.Entry<Long, DiscordChannelConfig> entry : Configuration.getConfig().discord.channels.channels.entrySet()) {
            HashMap<String, Integer> localCheckMap = new HashMap<>();
            // NOTE: If you want to check whether the commands are also in the generic command list, add globalCheckMap
            // to localCheckMap!

            for (CommandConfig command : entry.getValue().commands) {
                invalidCommandsOrAliases.addAll(checkCommand(localCheckMap, command));
            }
        }

        if (invalidCommandsOrAliases.size() == 0) {
            return new ValidationResult(true, null);
        } else {
            return new ValidationResult(
                false,
                getHint() + " Commands/Aliases: " + Joiner.on(", ").join(invalidCommandsOrAliases)
            );
        }
    }

    private Set<String> checkCommand(HashMap<String, Integer> checkMap, CommandConfig command) {
        Set<String> invalidCommandsOrAliases = new HashSet<>();
        String name = command.getName();
        int newVal = checkMap.getOrDefault(name, 0) + 1;
        if (newVal > 1) {
            invalidCommandsOrAliases.add(name);
        }

        checkMap.put(name, newVal);

        // Check for aliases
        for (String alias : command.getAliases()) {
            int aliasVal = checkMap.getOrDefault(alias, 0) + 1;
            if (aliasVal > 1) {
                invalidCommandsOrAliases.add(alias);
            }
            checkMap.put(alias, aliasVal);
        }
        return invalidCommandsOrAliases;
    }
}
