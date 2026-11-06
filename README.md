# Minecraft Network Core Plugin

Cube Worlds is a Minecraft server plugin for managing basic network features, including player ranks and prefixes, an in-game economy, configurable item shop, hub teleportation, world travel and creation, and minigame queues for SkyWars, BedWars, and Survival. It runs on Paper and declares optional compatibility with Geyser, Floodgate, ViaVersion, and ViaBackwards; these integrations are not included.

## Install

1. Use a Java 21 runtime and a Paper 1.21.11 server.
2. Build the plugin with Maven using `mvn package`.
3. Copy the generated `target/plugin.jar` into the server’s `plugins` directory.
4. Start or restart the server to generate the default configuration, then edit `plugins/EnderNetwork/config.yml` as needed.

## Use

- Use `/hub` to teleport to the configured hub. Administrators can set it with `/sethub`.
- Use `/balance` to check your coins and `/pay <player> <amount>` to transfer coins to an online player.
- Use `/rank` to view your rank. Administrators can assign a configured rank with `/rank <player> <rank>`.
- Use `/shop` to view the shop items and `/shop buy <item>` to purchase one.
- Use `/worlds` to list loaded worlds and `/world <name>` to travel to one. Use `/world create <name>` to create and travel to a world.
- Use `/game list`, `/game join <game>`, and `/game leave` to view and manage minigame queues.

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/7c0dd1be-9b33-4673-8936-c0e6c83c7a2b?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=7c0dd1be-9b33-4673-8936-c0e6c83c7a2b
