package chikachi.discord.test.impl;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

public class FakeMember implements Member {
    private final FakeGuild guild;
    private final FakeUser user;

    FakeMember(FakeGuild guild, FakeUser user) {
        this.guild = guild;
        this.user = user;
    }

    @Override
    public User getUser() {
        return this.user;
    }

    @Override
    public Guild getGuild() {
        return this.guild;
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
    public JDA getJDA() {
        return null;
    }

    @Override
    public OffsetDateTime getJoinDate() {
        return null;
    }

    @Override
    public GuildVoiceState getVoiceState() {
        return null;
    }

    @Override
    public Game getGame() {
        return null;
    }

    @Override
    public OnlineStatus getOnlineStatus() {
        return null;
    }

    @Override
    public String getNickname() {
        return null;
    }

    @Override
    public String getEffectiveName() {
        return null;
    }

    @Override
    public List<Role> getRoles() {
        return this.user.getRoles();
    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public List<Permission> getPermissions(Channel channel) {
        return null;
    }

    @Override
    public boolean canInteract(Member member) {
        return false;
    }

    @Override
    public boolean canInteract(Role role) {
        return false;
    }

    @Override
    public boolean canInteract(Emote emote) {
        return false;
    }

    @Override
    public boolean isOwner() {
        return false;
    }

    @Override
    public String getAsMention() {
        return this.user.getAsMention();
    }
}
