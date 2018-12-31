package net.discordintegration.core.config.validator.rules;

import com.google.common.base.Joiner;
import net.discordintegration.core.config.Configuration;
import net.discordintegration.core.config.validator.IConfigurationValidationRule;
import net.discordintegration.core.config.validator.ValidationResult;

import java.util.Map;

public class ChannelDescriptionsEnabledButEmptyRule implements IConfigurationValidationRule {
    private String getHint() {
        return "For at least one channel there is 'updateDescription' set to true but the 'descriptions' list is empty. " +
            "Either set 'updateDescription' to false or add values to the 'descriptions' list. ";
    }

    @Override
    public ValidationResult validate() {
        Object[] channelIDs = Configuration.getConfig().discord.channels.channels.entrySet()
            .stream()
            .filter(entry -> entry.getValue() != null && entry.getKey() != null)
            .filter(entry -> entry.getValue().updateDescription)
            .filter(entry -> entry.getValue().descriptions.size() == 0)
            .map(Map.Entry::getKey)
            .toArray();

        if (channelIDs.length == 0) {
            return new ValidationResult(true, null);
        } else {
            return new ValidationResult(
                false,
                getHint() + "Channel(s): " + Joiner.on(", ").join(channelIDs)
            );
        }
    }
}
