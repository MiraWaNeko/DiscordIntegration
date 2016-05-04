package chikachi.discord;

class EnableMessageTuple {
    private boolean enabled;
    private String message;

    EnableMessageTuple(boolean enabled, String message) {
        this.enabled = enabled;
        this.message = message;
    }

    boolean isEnabled() {
        return enabled;
    }

    String getMessage() {
        return message;
    }
}
