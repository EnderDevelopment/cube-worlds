package com.enderdevelopment.plugin;

import java.util.Locale;
import java.util.UUID;
import org.bukkit.configuration.file.FileConfiguration;

public final class RankService {
    private final Main plugin;
    private final FileConfiguration config;

    public RankService(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public String getRank(UUID uuid) {
        return config.getString("player-ranks." + uuid, "default");
    }

    public String getPrefix(UUID uuid) {
        return config.getString("ranks." + getRank(uuid) + ".prefix", "[Player]");
    }

    public boolean setRank(UUID uuid, String rank) {
        String normalized = rank.toLowerCase(Locale.ROOT);
        if (!config.isConfigurationSection("ranks." + normalized)) return false;
        config.set("player-ranks." + uuid, normalized);
        plugin.saveConfig();
        return true;
    }
}
