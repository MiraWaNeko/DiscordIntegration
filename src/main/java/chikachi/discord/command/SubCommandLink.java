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

package chikachi.discord.command;

import chikachi.discord.core.DiscordClient;
import chikachi.discord.core.MinecraftFormattingCodes;
import chikachi.discord.core.config.Configuration;
import chikachi.discord.core.config.linking.LinkingRequest;
import mcp.MethodsReturnNonnullByDefault;
import net.dv8tion.jda.core.entities.User;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class SubCommandLink extends CommandBase {
    @Override
    public String getName() {
        return "link";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return "/discord link <code>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] strings) throws CommandException {
        if (!(sender instanceof EntityPlayer)) {
            sender.sendMessage(new TextComponentString(MinecraftFormattingCodes.DARK_RED + "You need to be a player"));
            return;
        }

        UUID minecraftUUID = ((EntityPlayer) sender).getGameProfile().getId();
        Long discordId = Configuration.getLinking().getDiscordId(minecraftUUID);

        if (discordId != null) {
            User discordUser = DiscordClient.getInstance().getJda().getUserById(discordId);
            sender.sendMessage(
                new TextComponentString(
                    String.format(
                        MinecraftFormattingCodes.YELLOW + "You're already linked to %s#%s",
                        discordUser.getName(),
                        discordUser.getDiscriminator()
                    )
                )
            );
            return;
        }

        if (strings.length == 0) {
            sender.sendMessage(new TextComponentString(MinecraftFormattingCodes.DARK_RED + "Missing code"));
            return;
        }

        Optional<LinkingRequest> requestOptional = Configuration.getLinking().getRequestByCode(strings[0]);
        if (requestOptional.isPresent()) {
            LinkingRequest request = requestOptional.get();

            if (request.hasExpired()) {
                sender.sendMessage(new TextComponentString(MinecraftFormattingCodes.DARK_RED + "Linking request has expired"));
                return;
            }

            request.executeLinking(minecraftUUID);
            sender.sendMessage(new TextComponentString(MinecraftFormattingCodes.GREEN + "Linked"));
        } else {
            sender.sendMessage(new TextComponentString(MinecraftFormattingCodes.DARK_RED + "Linking request not found"));
        }
    }
}
