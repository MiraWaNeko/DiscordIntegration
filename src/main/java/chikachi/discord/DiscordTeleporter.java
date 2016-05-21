package chikachi.discord;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class DiscordTeleporter extends Teleporter {
    private boolean forcePos = false;
    private double x;
    private double y;
    private double z;

    public DiscordTeleporter(WorldServer world) {
        super(world);
    }

    public DiscordTeleporter(WorldServer world, double x, double y, double z) {
        super(world);

        this.forcePos = true;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean makePortal(Entity entity) {
        return true;
    }

    @Override
    public boolean placeInExistingPortal(Entity entity, float r) {
        return true;
    }

    @Override
    public void removeStalePortalLocations(long worldTime) {}

    @Override
    public void placeInPortal(Entity entity, float r) {
        if (this.forcePos) {
            entity.setLocationAndAngles(this.x, this.y, this.z, entity.rotationYaw, entity.rotationPitch);
        }

        entity.motionX = 0;
        entity.motionY = 0;
        entity.motionZ = 0;
        entity.fallDistance = 0;
    }
}
