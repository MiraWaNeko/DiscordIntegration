package chikachi.discord.test.impl;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.ArrayList;
import java.util.List;

public class FakeUser implements User {
    private final long id;
    private final String name;
    private final String discriminator;
    private final List<Role> roles = new ArrayList<>();

    public FakeUser(long id, String nameWithDiscriminator) {
        this.id = id;
        String[] parts = nameWithDiscriminator.split("#");
        this.name = parts[0];
        this.discriminator = parts[1];
    }

    public FakeUser(long id, String name, String discriminator) {
        this.id = id;
        this.name = name;
        this.discriminator = discriminator;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDiscriminator() {
        return this.discriminator;
    }

    @Override
    public String getAvatarId() {
        return null;
    }

    @Override
    public String getAvatarUrl() {
        return null;
    }

    @Override
    public String getDefaultAvatarId() {
        return null;
    }

    @Override
    public String getDefaultAvatarUrl() {
        return null;
    }

    @Override
    public String getEffectiveAvatarUrl() {
        return null;
    }

    @Override
    public boolean hasPrivateChannel() {
        return false;
    }

    @Override
    public RestAction<PrivateChannel> openPrivateChannel() {
        return null;
    }

    @Override
    public List<Guild> getMutualGuilds() {
        return null;
    }

    @Override
    public boolean isBot() {
        return false;
    }

    @Override
    public JDA getJDA() {
        return null;
    }

    @Override
    public boolean isFake() {
        return true;
    }

    @Override
    public String getAsMention() {
        return "<@" + this.id + ">";
    }

    @Override
    public long getIdLong() {
        return this.id;
    }

    List<Role> getRoles() {
        return this.roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }
}
