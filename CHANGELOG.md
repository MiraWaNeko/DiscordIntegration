# Changelog (MC 1.10.2)

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

### 2.2.0
- Added `!uptime` command
- Minecraft formatting will be removed from Discord messages
- Minecraft messages can not trigger @here and @everyone mentions
- Hopefully fixed `customFormatting` using `&` gives weird character
- Don't queue when no token is set
- Proper update when switching channel

### 2.1.0
- Updated JDA to 2.2.1-353
- Added `customFormatting` setting for Discord chat => Minecraft, when enabled...
	- `usernameColor` is ignored
	- Able to use Minecraft formatting in message
	- Discord links are clickable in Minecraft
- Pretty formatted config file
- Added Minecraft command `save` to save the config file
	- Adds missing config settings
	- Removes unused config settings
	- Pretty formats the file

#### 2.0.1
- Fix achievements not triggering

### 2.0.0
- Added custom command prefix
- Changed name to DiscordIntegration
	- Config file will be renamed upon startup
	- Mods using IMC needs to update to new name

### 1.4.0
- Updated JDA to 2.2.0-334
- Added support for emojis
	- Converting emojis from Discord to Minecraft
	- Converting some smileys to emojis from Minecraft to Discord
- Added support for attachments (links to attachments in Minecraft)

### 1.3.0
- Added IMC support
	- Mods can now register/unregister as listener for events
		- Currently only `chat` events are sent
	- Mods can send messages to Discord
		- Messages will be prefixed with `[modid]`
- Added `relaySayCommand` option to Minecraft chat config

### 1.2.0
- Added `colored` option to `!tps` command
	- This will not show in colors on mobile Discord (At the time of writing)
	- Colors
		- Green when `19 <= TPS`
		- Orange when `15 <= TPS && TPS < 19`
		- Red when `TPS < 15`

#### 1.1.1
- Fixed crash on achievements

### 1.1.0
- Added `%DESCRIPTION%` to Achievement messages
- Added custom commands (Read more on [the wiki](https://github.com/Chikachi/ChikachiDiscord/wiki/Custom-Commands))
- `!tps` response is now padded so the times and TPS is lined up
- `!tps` response is now sorted after dimension ID
- Removed kick of player when using `!unstuck`

## 1.0.0 - Out of Alpha!
![RELEASE](https://media.giphy.com/media/duGmnxv5TZDy/giphy.gif)
- Improved the `!tps` response text
- Switched to asynchronous sending messages to Discord

### 0.6.0
- Added ability for message when detecting server crashing
- Added support for other Discord formatting for `_Italic_`
- Improved handling of `!unstuck` command

#### 0.5.1
- Fix color of Discord usernames
	- Added `usernameColor` to Minecraft chat config
- Fix missing characters after MC => Discord mention in Discord message

### 0.5.0
- Added ability for command aliases
- Added `!unstuck <player>` command to send a player to spawn
	- Works for both online and offline players
	- Will kick online players, as the client doesn't always update location
- Changed the way all messages are handled
	- Now supports some of Discord's formatting (`**Bold**`, `*Italic*` and `__Underline__`)
	- Removes formatting from `~~Line through~~` and ``` `code` ```
- Mention a Discord user by clicking the name

### 0.4.0
- Added max length on relayed Discord messages (Default -1, meaning disabled)
- Added limit of commands to roles (by role name)
- Improved Discord user mentions (Prevents wrong mention in case of both `User1` and `User1_`)
- Replaced placeholders in strings (**BREAKS ALL CURRENT CONFIGS**)
	- `%USER%` Name of user (Chat, Death, Achievement, Join, Leave)
	- `%MESSAGE%` Message (Chat, Death)
	- `%ACHIEVEMENT%` Name of achievement (Achievement)
- Returned player names from `!online` is now packed in like `this`
- Remove Minecraft formatting (1 character prefixed with §) from `/say` and chat messages

### 0.3.0
- Added `!tps` command for Discord
- Highlight player's name in Discord messages
- Highlight `@everyone` (Only from people with permission to mention everyone)

### 0.2.0
- Added command
	- If ChikachiLib is detected, `/chikachi discord`
	- Otherwise, `/discord`
- Broadcast messages (`/say`) is now also relayed
- Made Discord mentions case-insensitive

#### 0.1.3
- Fix NullPointerException on startup
- Added experimental fake players (CAN FILL YOUR SERVER UP WITH FAKE PLAYERS)  
![Example](https://ss.chikachi.net/2016-05-05_03-10-13_7109d217-ef20-4979-a5a4-0baa8c8a46c4.png)
- Call proper shutdown of Discord connection

#### 0.1.2
- Improve Discord mentioning

#### 0.1.1
- Fix DiscordFakePlayers showing up in !online list

### 0.1.0
- Initial alpha version