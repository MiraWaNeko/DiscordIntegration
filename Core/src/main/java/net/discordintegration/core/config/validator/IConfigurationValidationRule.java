package net.discordintegration.core.config.validator;

public interface IConfigurationValidationRule {
    /**
     * @return true, if the config is valid in the view of this rule
     */
    ValidationResult validate();
}
