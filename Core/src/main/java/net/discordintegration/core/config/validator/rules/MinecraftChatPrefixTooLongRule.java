package net.discordintegration.core.config.validator.rules;

import net.discordintegration.core.config.Configuration;
import net.discordintegration.core.config.validator.IConfigurationValidationRule;
import net.discordintegration.core.config.validator.ValidationResult;

public class MinecraftChatPrefixTooLongRule implements IConfigurationValidationRule {

    private static final int MAX_LEN = 30;

    private String getHint() {
        return String.format("At least one dimension has a 'chatPrefix' that is longer than %d characters." +
            " You should trim it to a shorter value.", MAX_LEN);
    }

    @Override
    public ValidationResult validate() {
        boolean valid = (Configuration.getConfig().minecraft.dimensions.generic.chatPrefix.length() <= MAX_LEN)
            && Configuration.getConfig().minecraft.dimensions.dimensions.values().stream()
            .allMatch(dim -> dim.chatPrefix.length() <= MAX_LEN);

        return valid ? new ValidationResult(true, null) : new ValidationResult(false, getHint());
    }
}
