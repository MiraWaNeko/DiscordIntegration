/*
 * Copyright (C) 2017 Chikachi
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package chikachi.discord.test.impl;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.RoleManager;
import net.dv8tion.jda.core.managers.RoleManagerUpdatable;
import net.dv8tion.jda.core.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public class FakeRole implements Role {
    private final long id;
    private final String name;

    public FakeRole(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public int getPositionRaw() {
        return 0;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isManaged() {
        return false;
    }

    @Override
    public boolean isHoisted() {
        return false;
    }

    @Override
    public boolean isMentionable() {
        return false;
    }

    @Override
    public long getPermissionsRaw() {
        return 0;
    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public boolean isPublicRole() {
        return false;
    }

    @Override
    public boolean canInteract(Role role) {
        return false;
    }

    @Override
    public Guild getGuild() {
        return null;
    }

    @Override
    public List<Permission> getPermissions() {
        return null;
    }

    @Override
    public boolean hasPermission(Permission... permissions) {
        return false;
    }

    @Override
    public boolean hasPermission(Collection<Permission> collection) {
        return false;
    }

    @Override
    public boolean hasPermission(Channel channel, Permission... permissions) {
        return false;
    }

    @Override
    public boolean hasPermission(Channel channel, Collection<Permission> collection) {
        return false;
    }

    @Override
    public RoleManager getManager() {
        return null;
    }

    @Override
    public RoleManagerUpdatable getManagerUpdatable() {
        return null;
    }

    @Override
    public AuditableRestAction<Void> delete() {
        return null;
    }

    @Override
    public JDA getJDA() {
        return null;
    }

    @Override
    public int compareTo(@NotNull Role o) {
        return 0;
    }

    @Override
    public String getAsMention() {
        return null;
    }

    @Override
    public long getIdLong() {
        return this.id;
    }
}
