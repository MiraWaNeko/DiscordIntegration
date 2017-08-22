package chikachi.discord.test.impl;

import net.dv8tion.jda.client.requests.restaction.pagination.MentionPaginationAction;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Region;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.managers.GuildManager;
import net.dv8tion.jda.core.managers.GuildManagerUpdatable;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.restaction.pagination.AuditLogPaginationAction;

import java.util.Collection;
import java.util.List;

public class FakeGuild implements Guild {
    @Override
    public String getName() {
        return "FakeGuild";
    }

    @Override
    public String getIconId() {
        return null;
    }

    @Override
    public String getIconUrl() {
        return null;
    }

    @Override
    public String getSplashId() {
        return null;
    }

    @Override
    public String getSplashUrl() {
        return null;
    }

    @Override
    public VoiceChannel getAfkChannel() {
        return null;
    }

    @Override
    public Member getOwner() {
        return null;
    }

    @Override
    public Timeout getAfkTimeout() {
        return null;
    }

    @Override
    public Region getRegion() {
        return null;
    }

    @Override
    public boolean isMember(User user) {
        return false;
    }

    @Override
    public Member getSelfMember() {
        return null;
    }

    @Override
    public Member getMember(User user) {
        return new FakeMember(this, (FakeUser)user);
    }

    @Override
    public Member getMemberById(String s) {
        return null;
    }

    @Override
    public Member getMemberById(long l) {
        return null;
    }

    @Override
    public List<Member> getMembers() {
        return null;
    }

    @Override
    public List<Member> getMembersByName(String s, boolean b) {
        return null;
    }

    @Override
    public List<Member> getMembersByNickname(String s, boolean b) {
        return null;
    }

    @Override
    public List<Member> getMembersByEffectiveName(String s, boolean b) {
        return null;
    }

    @Override
    public List<Member> getMembersWithRoles(Role... roles) {
        return null;
    }

    @Override
    public List<Member> getMembersWithRoles(Collection<Role> collection) {
        return null;
    }

    @Override
    public TextChannel getTextChannelById(String s) {
        return null;
    }

    @Override
    public TextChannel getTextChannelById(long l) {
        return null;
    }

    @Override
    public List<TextChannel> getTextChannels() {
        return null;
    }

    @Override
    public List<TextChannel> getTextChannelsByName(String s, boolean b) {
        return null;
    }

    @Override
    public VoiceChannel getVoiceChannelById(String s) {
        return null;
    }

    @Override
    public VoiceChannel getVoiceChannelById(long l) {
        return null;
    }

    @Override
    public List<VoiceChannel> getVoiceChannels() {
        return null;
    }

    @Override
    public List<VoiceChannel> getVoiceChannelsByName(String s, boolean b) {
        return null;
    }

    @Override
    public Role getRoleById(String s) {
        return null;
    }

    @Override
    public Role getRoleById(long l) {
        return null;
    }

    @Override
    public List<Role> getRoles() {
        return null;
    }

    @Override
    public List<Role> getRolesByName(String s, boolean b) {
        return null;
    }

    @Override
    public Emote getEmoteById(String s) {
        return null;
    }

    @Override
    public Emote getEmoteById(long l) {
        return null;
    }

    @Override
    public List<Emote> getEmotes() {
        return null;
    }

    @Override
    public List<Emote> getEmotesByName(String s, boolean b) {
        return null;
    }

    @Override
    public RestAction<List<User>> getBans() {
        return null;
    }

    @Override
    public RestAction<Integer> getPrunableMemberCount(int i) {
        return null;
    }

    @Override
    public Role getPublicRole() {
        return null;
    }

    @Override
    public TextChannel getPublicChannel() {
        return null;
    }

    @Override
    public GuildManager getManager() {
        return null;
    }

    @Override
    public GuildManagerUpdatable getManagerUpdatable() {
        return null;
    }

    @Override
    public GuildController getController() {
        return null;
    }

    @Override
    public MentionPaginationAction getRecentMentions() {
        return null;
    }

    @Override
    public AuditLogPaginationAction getAuditLogs() {
        return null;
    }

    @Override
    public RestAction<Void> leave() {
        return null;
    }

    @Override
    public RestAction<Void> delete() {
        return null;
    }

    @Override
    public RestAction<Void> delete(String s) {
        return null;
    }

    @Override
    public AudioManager getAudioManager() {
        return null;
    }

    @Override
    public JDA getJDA() {
        return null;
    }

    @Override
    public RestAction<List<Invite>> getInvites() {
        return null;
    }

    @Override
    public RestAction<List<Webhook>> getWebhooks() {
        return null;
    }

    @Override
    public List<GuildVoiceState> getVoiceStates() {
        return null;
    }

    @Override
    public VerificationLevel getVerificationLevel() {
        return null;
    }

    @Override
    public NotificationLevel getDefaultNotificationLevel() {
        return null;
    }

    @Override
    public MFALevel getRequiredMFALevel() {
        return null;
    }

    @Override
    public ExplicitContentLevel getExplicitContentLevel() {
        return null;
    }

    @Override
    public boolean checkVerification() {
        return false;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public long getIdLong() {
        return 0;
    }
}
