# Changelog (MC 1.12)

### 3.0.5
- [Forge] Updated to build against 1.12.2-14.23.2.2611
- [Library] Updated JDA
- [Library] Updated emote-java
- [Commands] Rewrite the message batching to use the new Batcher class in core (Thanks @Ricket)
- [Commands] Combine responses to commands (#125)
- [Commands] Send commands from main thread for compatibility (Thanks @132ikl)
- [Events] Added listener for advancement event (#126)
- [Events] Added Discord to Minecraft nickname support. (Thanks @132ikl)

### 3.0.4
- [Forge] Updated to build against 1.12.2-14.23.0.2491
- [Events] Fixed console spam on Discord => Minecraft chat messages
- [IMC] Now sends a response to all requests

### 3.0.3
- [Forge] Updated to build against 1.12.1-14.22.1.2478
- [Library] Updated JDA (#87)
- [Library] Updated emote-java
- [Dynmap Integration] Use the same checks as Discord => Minecraft (#83) (#84)

### 3.0.2
- Fix for messages with a single modifier being changed (#76)
- Convert Minecraft formatting with `&` to `§` (#77)
- Made Dynmap integration work both ways (#80)
- Fixed client code crashes (#78)
- Config generated will now make `discordChannels` an empty array in `generic` dimension settings.

### 3.0.1
- Fix for command arguments (#71)

### 3.0.0
- Complete rewrite
  - Added support for sending to multiple Discord channels
  - Added support for multiple Discord channels config
  - Added support for multiple Minecraft dimension config
  - Added support for webhook
  - Added support for mentioning roles
  - Added support for ignore
    - Discord users
    - Minecraft messages by regex
    - Minecraft FakePlayers
  - Added support for `/me` messages
  - Added support for commands through Discord DM
    - **Notice:** Uses user's roles in all guilds for role permissions
  - Added integration for DynmapForge
  - Added Discord user discriminator to IMC user NBTTagCompound
  - Added linking between Discord user and Minecraft user
  - Added whitelist/blacklist to IMC to prevent possible spamming mods
  - Moved all commands to Minecraft `/discord ...`
    - **Hint:** Use tab autocompletion
    - When creating commands for Discord, just point towards the Minecraft command
    - Added `/discord uptime` command
    - Colored tps is now `/discord tps --color`
  - Cuts off messages relayed (to Discord) longer than 2000 characters
    - **Notice:** This might break some command responses / formatting
      - Such as TPS for servers with many dimensions
  - Fixed permission missing crashes
  - Fixed relaying cancelled events
  - Removed support for `&` in formatting