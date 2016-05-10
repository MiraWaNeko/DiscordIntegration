package chikachi.discord.config;

public class EnableMessageTuple {
    private boolean enabled;
    private String message;

    EnableMessageTuple(boolean enabled, String message) {
        this.enabled = enabled;
        this.message = message;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getMessage() {
        return message;
    }
}
