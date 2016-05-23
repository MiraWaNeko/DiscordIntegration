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

package chikachi.discord.command;

import chikachi.lib.common.command.sub.CommandChikachiBase;
import chikachi.lib.common.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@SuppressWarnings("unused")
public class LibCommandHandler extends CommandChikachiBase {
    public LibCommandHandler() {
        super("discord");
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        CommandProcessor.processCommand(sender, args);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return !(sender instanceof EntityPlayer) || PlayerUtils.IsOP((EntityPlayer) sender);
    }
}
