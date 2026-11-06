package com.enderdevelopment.plugin;

import java.util.UUID;
import org.bukkit.configuration.file.FileConfiguration;

public final class EconomyService {
    private final Main plugin;
    private final FileConfiguration config;

    public EconomyService(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public double balance(UUID uuid) {
        return config.getDouble("balances." + uuid, config.getDouble("starting-balance", 100.0));
    }

    public void setBalance(UUID uuid, double amount) {
        config.set("balances." + uuid, Math.max(0, amount));
        plugin.saveConfig();
    }

    public boolean withdraw(UUID uuid, double amount) {
        if (amount <= 0 || balance(uuid) < amount) return false;
        setBalance(uuid, balance(uuid) - amount);
        return true;
    }

    public void deposit(UUID uuid, double amount) {
        if (amount > 0) setBalance(uuid, balance(uuid) + amount);
    }
}
