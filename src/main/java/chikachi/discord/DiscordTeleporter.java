/**
 * Copyright (C) 2016 Chikachi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package chikachi.discord;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
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
    public void removeStalePortalLocations(long worldTime) {
    }

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
