package net.discordintegration.core.config.validator.rules;

import net.discordintegration.core.config.Configuration;
import net.discordintegration.core.config.validator.IConfigurationValidationRule;
import net.discordintegration.core.config.validator.ValidationResult;

public class DiscordTokenMustBeSetRule implements IConfigurationValidationRule {
    private String getHint() {
        return "You must add a Discord token in order to use the bot.";
    }

    @Override
    public ValidationResult validate() {
        boolean b = Configuration.getConfig().discord.token != null && !Configuration.getConfig().discord.token.equals("");
        return new ValidationResult(b, getHint());
    }
}
