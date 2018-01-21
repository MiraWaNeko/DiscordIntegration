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

package chikachi.discord;

import chikachi.discord.core.Patterns;
import com.google.common.base.Joiner;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@SuppressWarnings("EntityConstructor")
@ParametersAreNonnullByDefault
public class DiscordCommandSender extends FakePlayer {
    private static final UUID playerUUID = UUID.fromString("828653ca-0185-43d4-b26d-620a7f016be6");
    private final MessageChannel channel;
    private List<String> queue;
    private Timer timer;
    private TimerTask timerTask;

    public DiscordCommandSender(MessageChannel channel, User user) {
        super(FMLCommonHandler.instance().getMinecraftServerInstance().worlds[0], new GameProfile(playerUUID, "@" + user.getName()));
        this.channel = channel;
    }

    @SuppressWarnings("unused")
    public DiscordCommandSender(WorldServer world, MessageChannel channel, String name) {
        super(world, new GameProfile(playerUUID, "@" + name));
        this.channel = channel;
    }

    @Override
    public boolean canUseCommand(int i, String s) {
        return true;
    }

    @Override
    public void sendMessage(ITextComponent component) {
        this.queueMessage(component);
    }

    @Override
    public void sendStatusMessage(ITextComponent component, boolean actionBar) {
        this.queueMessage(component);
    }

    private void queueMessage(ITextComponent component) {
        if (queue == null) {
            queue = new ArrayList<>();
        }
        if (this.timer == null) {
            this.timer = new Timer();
        }
        if (this.timerTask != null) {
            this.timerTask.cancel();
            this.timer.purge();
        }
        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                flushQueue();
            }
        };
        this.timer.schedule(this.timerTask, 500);
        this.queue.add(
            Patterns.minecraftCodePattern.matcher(
                component.getUnformattedText()
            ).replaceAll("")
        );
    }

    private void flushQueue() {
        this.channel.sendMessage(
            Joiner.on("\n").join(this.queue)
        ).queue();
    }
}
