/*
 * Copyright (C) 2018 Chikachi and other contributors
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

package net.discordintegration.core.config.discord;

import com.google.common.base.Joiner;
import net.discordintegration.core.DiscordClient;
import net.discordintegration.core.config.Configuration;
import net.dv8tion.jda.core.entities.*;

import java.util.ArrayList;
import java.util.List;

public class CommandConfig {
    private String name;
    private String command;
    private boolean enabled;
    private boolean outputEnabled;
    private List<String> aliases = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();

    public CommandConfig() {

    }

    public CommandConfig(String name, String command, boolean enabled, boolean outputEnabled, List<String> aliases, List<String> permissions) {
        this.name = name;
        this.command = command;
        this.enabled = enabled;
        this.outputEnabled = outputEnabled;
        this.aliases = aliases;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isOutputEnabled() {
        return outputEnabled;
    }

    public boolean shouldExecute(String command, User executor, MessageChannel channel) {
        return isEnabled() && (this.name.equalsIgnoreCase(command) || this.aliases.contains(command.toLowerCase())) && this.checkPermission(executor, channel);
    }

    public String buildCommand(List<String> args) {
        String cmd = this.command;

        int argsCount = args.size();
        if (argsCount > 0) {
            for (int i = 0; i < argsCount; i++) {
                cmd = cmd.replaceAll("(?i)\\{ARG_" + (i + 1) + "}", args.get(i));
            }
            cmd = cmd.replaceAll("(?i)\\{ARGS}", Joiner.on(' ').join(args));
        }
        cmd = cmd.replaceAll("(?i)\\{(ARG_[0-9]+|ARGS)}", "");

        return cmd.trim();
    }

    private boolean checkPermission(User user, MessageChannel channel) {
        if (this.permissions.size() == 0) {
            return true;
        }

        if (user == null || channel == null) {
            return false;
        }

        final List<Role> roles = new ArrayList<>();
        if (channel instanceof TextChannel) {
            Member member = ((TextChannel) channel).getGuild().getMember(user);
            if (member != null) {
                roles.addAll(member.getRoles());
            }
        } else if (channel instanceof PrivateChannel && Configuration.getConfig().discord.channels.generic.allowDMCommands) {
            DiscordClient.getInstance().getJda().getGuilds()
                .forEach(guild -> {
                    Member member = guild.getMember(user);
                    if (member != null) {
                        roles.addAll(member.getRoles());
                    }
                });
        }

        for (String permission : permissions) {
            if (permission.startsWith("role:")) {
                if (roles.size() > 0) {
                    if (roles.stream().anyMatch(role -> role.getName().equalsIgnoreCase(permission.substring(5)) || role.getId().equals(permission.substring(5)))) {
                        return true;
                    }
                }
            } else if (permission.startsWith("user:")) {
                if (user.getId().equals(permission.substring(5)) || (user.getName() + "#" + user.getDiscriminator()).equals(permission.substring(5))) {
                    return true;
                }
            } else {
                if (user.getId().equals(permission) || (user.getName() + "#" + user.getDiscriminator()).equals(permission)) {
                    return true;
                }
            }
        }

        return false;
    }
}
