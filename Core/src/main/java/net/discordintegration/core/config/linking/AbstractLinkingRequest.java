package net.discordintegration.core.config.linking;

import com.google.gson.annotations.Since;

import java.util.Date;
import java.util.Random;

public class AbstractLinkingRequest {
    private static Random rand = new Random();
    @Since(3.0)
    private String code;
    @Since(3.0)
    private long expires;

    public void generateCode() {
        this.code = String.format("%04d", rand.nextInt(10000));
        this.expires = new Date(System.currentTimeMillis() + 5 * 60 * 1000).getTime();
    }

    public String getCode() {
        return code;
    }

    public boolean hasExpired() {
        return this.expires <= new Date().getTime();
    }

    public String expiresIn() {
        int seconds = (int) Math.max(0, Math.floorDiv(this.expires - new Date().getTime(), (int) 1e3));
        int minutes = Math.floorDiv(seconds, 60);
        seconds -= minutes * 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
