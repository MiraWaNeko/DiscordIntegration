package net.discordintegration.core.config.validator;

import net.discordintegration.core.DiscordIntegrationLogger;
import net.discordintegration.core.config.validator.rules.*;

import java.util.ArrayList;

public abstract class ConfigurationValidator {
    private static ArrayList<IConfigurationValidationRule> rules = new ArrayList<>();

    static {
        addRule(new DiscordTokenMustBeSetRule());
        addRule(new DuplicatedCommandOrAliasRule());
        addRule(new ChannelCommandPrefixEmptyRule());
        addRule(new ChannelDescriptionsEnabledButEmptyRule());
        addRule(new ChannelRelayChatTrueButMessageEmptyRule());
        addRule(new MinecraftChatPrefixTooLongRule());
        addRule(new IMCEnabledAndBlacklistEmptyRule());
    }

    public static void addRule(IConfigurationValidationRule rule) {
        rules.add(rule);
    }

    public static ValidationResult[] validateAll() {
        return rules
            .stream()
            .map(IConfigurationValidationRule::validate)
            .toArray(ValidationResult[]::new);
    }

    public static void validateAndPrintAll() {
        DiscordIntegrationLogger.Log("Validating the configuration..");
        ValidationResult[] results = validateAll();
        int invalid = 0;

        for (ValidationResult result : results) {
            if (!result.successful) {
                DiscordIntegrationLogger.Log(String.format("[HINT] %s", result.hint));
                invalid++;
            }
        }

        DiscordIntegrationLogger.Log(String.format(
            "Configuration validated. %d of %d rules were successful.",
            getTotalTestCount() - invalid,
            getTotalTestCount()
        ));
    }

    public static int getTotalTestCount() {
        return rules.size();
    }
}
