package net.discordintegration.core.bridge;

import net.discordintegration.core.bridge.minecraft.IMinecraftMethods;

public class Bridge {
    public final EventBridge event = new EventBridge();
    private IMinecraftMethods minecraft;

    public IMinecraftMethods getMinecraft() {
        if (minecraft == null) {
            throw new RuntimeException("IMinecraftMethods must be set first!");
        }
        return minecraft;
    }

    public void setMinecraft(IMinecraftMethods minecraft) {
        this.minecraft = minecraft;
    }


}
