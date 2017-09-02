# Changelog (MC 1.12)

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