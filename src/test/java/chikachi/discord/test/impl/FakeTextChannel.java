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
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.ChannelManager;
import net.dv8tion.jda.core.managers.ChannelManagerUpdatable;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.core.requests.restaction.InviteAction;
import net.dv8tion.jda.core.requests.restaction.PermissionOverrideAction;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class FakeTextChannel implements TextChannel {
    private final Guild guild;

    public FakeTextChannel(Guild guild) {
        this.guild = guild;
    }

    @Override
    public String getTopic() {
        return "";
    }

    @Override
    public boolean isNSFW() {
        return false;
    }

    @Override
    public RestAction<List<Webhook>> getWebhooks() {
        return null;
    }

    @Override
    public RestAction<Void> deleteMessages(Collection<Message> collection) {
        return null;
    }

    @Override
    public RestAction<Void> deleteMessagesByIds(Collection<String> collection) {
        return null;
    }

    @Override
    public AuditableRestAction<Void> deleteWebhookById(String s) {
        return null;
    }

    @Override
    public RestAction<Void> clearReactionsById(String s) {
        return null;
    }

    @Override
    public boolean canTalk() {
        return false;
    }

    @Override
    public boolean canTalk(Member member) {
        return false;
    }

    @Override
    public int compareTo(@NotNull TextChannel o) {
        return 0;
    }

    @Override
    public ChannelType getType() {
        return null;
    }

    @Override
    public long getLatestMessageIdLong() {
        return 0;
    }

    @Override
    public boolean hasLatestMessage() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Guild getGuild() {
        return this.guild;
    }

    @Override
    public List<Member> getMembers() {
        return null;
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
    public JDA getJDA() {
        return null;
    }

    @Override
    public PermissionOverride getPermissionOverride(Member member) {
        return null;
    }

    @Override
    public PermissionOverride getPermissionOverride(Role role) {
        return null;
    }

    @Override
    public List<PermissionOverride> getPermissionOverrides() {
        return null;
    }

    @Override
    public List<PermissionOverride> getMemberPermissionOverrides() {
        return null;
    }

    @Override
    public List<PermissionOverride> getRolePermissionOverrides() {
        return null;
    }

    @Override
    public ChannelManager getManager() {
        return null;
    }

    @Override
    public ChannelManagerUpdatable getManagerUpdatable() {
        return null;
    }

    @Override
    public AuditableRestAction<Void> delete() {
        return null;
    }

    @Override
    public PermissionOverrideAction createPermissionOverride(Member member) {
        return null;
    }

    @Override
    public PermissionOverrideAction createPermissionOverride(Role role) {
        return null;
    }

    @Override
    public InviteAction createInvite() {
        return null;
    }

    @Override
    public RestAction<List<Invite>> getInvites() {
        return null;
    }

    @Override
    public String getAsMention() {
        return null;
    }

    @Override
    public long getIdLong() {
        return 0;
    }
}
