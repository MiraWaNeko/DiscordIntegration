package chikachi.discord.test;

import chikachi.discord.core.config.discord.CommandConfig;
import chikachi.discord.test.impl.FakeGuild;
import chikachi.discord.test.impl.FakeRole;
import chikachi.discord.test.impl.FakeTextChannel;
import chikachi.discord.test.impl.FakeUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class CommandTest {
    private CommandConfig commandConfig;

    private final long permittedUserId = 1234567890;
    private final String permittedUserName = "PermittedUser#1234";
    private final long permittedRoleId = 1987654321;
    private final String permittedRoleName = "PermittedRole";
    private final long notPermittedId = 1597534568;
    private final String notPermittedUserName = "NoPermission#5678";
    private final String notPermittedRoleName = "NoPermissionRole";

    private final FakeGuild fakeGuild = new FakeGuild();
    private final FakeTextChannel fakeTextChannel = new FakeTextChannel(fakeGuild);
    private final FakeUser fakePermittedUser = new FakeUser(permittedUserId, "Permitted", "0000");

    @Before
    public void prepare() {
        // Create CommandConfig
        List<String> aliases = new ArrayList<>();
        aliases.add("cake");

        List<String> permissions = new ArrayList<>();
        permissions.add("role:" + permittedRoleId);
        permissions.add("role:" + permittedRoleName);
        permissions.add("user:" + permittedUserId);
        permissions.add("user:" + permittedUserName);

        commandConfig = new CommandConfig("test", "test {ARG_1} {ARG_2} {ARGS}", true, aliases, permissions);
    }

    @Test
    public void arguments() {
        List<String> args = new ArrayList<>();
        Assert.assertTrue("No args", commandConfig.buildCommand(args).equals("test"));
        args.add("first");
        Assert.assertTrue("1 arg", commandConfig.buildCommand(args).equals("test first  first"));
        args.add("second");
        Assert.assertTrue("2 args", commandConfig.buildCommand(args).equals("test first second first second"));
    }

    @Test
    public void aliases() {
        Assert.assertTrue("Command", commandConfig.shouldExecute("test", fakePermittedUser, fakeTextChannel));
        Assert.assertTrue("Alias", commandConfig.shouldExecute("cake", fakePermittedUser, fakeTextChannel));
        Assert.assertFalse("Wrong", commandConfig.shouldExecute("wrong", fakePermittedUser, fakeTextChannel));
    }

    @Test
    public void permissions() {
        FakeUser fakeUser = new FakeUser(permittedUserId, notPermittedUserName);
        fakeUser.addRole(new FakeRole(permittedRoleId, notPermittedRoleName));
        Assert.assertTrue("Role ID", commandConfig.shouldExecute("test", fakeUser, fakeTextChannel));

        fakeUser = new FakeUser(notPermittedId, permittedUserName);
        fakeUser.addRole(new FakeRole(notPermittedId, permittedRoleName));
        Assert.assertTrue("Role Name", commandConfig.shouldExecute("test", fakeUser, fakeTextChannel));

        fakeUser = new FakeUser(permittedUserId, notPermittedUserName);
        Assert.assertTrue("User ID", commandConfig.shouldExecute("test", fakeUser, fakeTextChannel));

        fakeUser = new FakeUser(notPermittedId, permittedUserName);
        Assert.assertTrue("User Name", commandConfig.shouldExecute("test", fakeUser, fakeTextChannel));

        fakeUser = new FakeUser(notPermittedId, notPermittedUserName);
        fakeUser.addRole(new FakeRole(notPermittedId, notPermittedRoleName));
        Assert.assertFalse("No Permission", commandConfig.shouldExecute("test", fakeUser, fakeTextChannel));
    }
}
