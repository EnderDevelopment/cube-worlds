package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class JoinListener implements Listener {
    private final Main plugin;
    public JoinListener(Main plugin) { this.plugin = plugin; }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String prefix = plugin.ranks().getPrefix(event.getPlayer().getUniqueId());
        event.joinMessage(Component.text(prefix + " ").color(NamedTextColor.GOLD)
            .append(Component.text(event.getPlayer().getName()).color(NamedTextColor.WHITE)));
        event.getPlayer().sendMessage(Component.text("Welcome to EnderNetwork. Use /help to explore the network.").color(NamedTextColor.AQUA));
    }
}
