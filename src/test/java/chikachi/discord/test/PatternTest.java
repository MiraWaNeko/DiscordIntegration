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
package chikachi.discord.test;

import chikachi.discord.DiscordIntegration;
import chikachi.discord.core.Patterns;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PatternTest {
    @Before
    public void init() {
        DiscordIntegration.addPatterns();
    }

    @Test
    public void discordToMinecraft() {
        Assert.assertTrue("Bold", "\u00a7lBold\u00a7r".equals(Patterns.discordToMinecraft("**Bold**")));
        Assert.assertTrue("BoldItalic", "\u00a7lBold \u00a7oItalic\u00a7r".equals(Patterns.discordToMinecraft("**Bold *Italic***")));
        Assert.assertTrue("BoldItalicUnderline", "\u00a7lBold \u00a7oItalic \u00a7nUnderline\u00a7r".equals(Patterns.discordToMinecraft("**Bold *Italic __Underline__***")));
        Assert.assertTrue("BoldItalicUnderline2", "\u00a7lBold \u00a7oItalic\u00a7r\u00a7l \u00a7nUnderline\u00a7r".equals(Patterns.discordToMinecraft("**Bold *Italic* __Underline__**")));
        Assert.assertTrue("Strikethrough", "\u00a7mStrikethrough\u00a7r".equals(Patterns.discordToMinecraft("~~Strikethrough~~")));
        Assert.assertTrue("Underline", "\u00a7nUnderline\u00a7r".equals(Patterns.discordToMinecraft("__Underline__")));
        Assert.assertTrue("Italic", "\u00a7oItalic\u00a7r".equals(Patterns.discordToMinecraft("*Italic*")));
        Assert.assertTrue("Italic /me", "\u00a7oItalic\u00a7r".equals(Patterns.discordToMinecraft("_Italic_")));
        Assert.assertTrue("Reset", "\u00a7lBold\u00a7rNormal".equals(Patterns.discordToMinecraft("**Bold**Normal")));
    }

    @Test
    public void minecraftToDiscord() {
        Assert.assertTrue("Color", "Color".equals(Patterns.minecraftToDiscord("\u00a70\u00a71\u00a72\u00a73\u00a74\u00a75\u00a76\u00a77\u00a78\u00a79\u00a7a\u00a7b\u00a7c\u00a7d\u00a7e\u00a7fColor")));
        Assert.assertTrue("Obfuscated", "Obfuscated".equals(Patterns.minecraftToDiscord("\u00a7kObfuscated")));
        Assert.assertTrue("Bold", "**Bold**".equals(Patterns.minecraftToDiscord("\u00a7lBold")));
        Assert.assertTrue("BoldItalic", "**Bold *Italic***".equals(Patterns.minecraftToDiscord("\u00a7lBold \u00a7oItalic")));
        Assert.assertTrue("BoldItalicUnderline", "**Bold *Italic __Underline__***".equals(Patterns.minecraftToDiscord("\u00a7lBold \u00a7oItalic \u00a7nUnderline")));
        Assert.assertTrue("BoldItalicUnderline2", "**Bold *Italic* __Underline__**".equals(Patterns.minecraftToDiscord("\u00a7lBold \u00a7oItalic\u00a7r\u00a7l \u00a7nUnderline")));
        Assert.assertTrue("Strikethrough", "~~Strikethrough~~".equals(Patterns.minecraftToDiscord("\u00a7mStrikethrough")));
        Assert.assertTrue("Underline", "__Underline__".equals(Patterns.minecraftToDiscord("\u00a7nUnderline")));
        Assert.assertTrue("Italic", "*Italic*".equals(Patterns.minecraftToDiscord("\u00a7oItalic")));
        Assert.assertTrue("Reset", "**Bold**Normal".equals(Patterns.minecraftToDiscord("\u00a7lBold\u00a7rNormal")));
    }
}
