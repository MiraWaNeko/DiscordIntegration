package net.discordintegration.core.config.validator;

public final class ValidationResult {
    public final boolean successful;
    public final String hint;

    public ValidationResult(boolean successful, String hint) {
        this.successful = successful;
        this.hint = hint != null ? hint : "";
    }
}
