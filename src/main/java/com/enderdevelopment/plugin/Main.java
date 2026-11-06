package com.enderdevelopment.plugin;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements TabExecutor {
    private EconomyService economy;
    private RankService ranks;
    private GameService games;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        economy = new EconomyService(this);
        ranks = new RankService(this);
        games = new GameService();
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        String[] commands = {"hub", "sethub", "balance", "pay", "rank", "shop", "worlds", "world", "game"};
        for (String name : commands) {
            if (getCommand(name) != null) getCommand(name).setExecutor(this);
        }
        getLogger().info("EnderNetwork core enabled. Optional cross-play plugins are detected at runtime.");
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    public EconomyService economy() { return economy; }
    public RankService ranks() { return ranks; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String name = command.getName().toLowerCase(Locale.ROOT);
        if (name.equals("sethub")) return setHub(sender);
        if (name.equals("hub")) return hub(sender);
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command requires a player.");
            return true;
        }
        return switch (name) {
            case "balance" -> balance(player);
            case "pay" -> pay(player, args);
            case "rank" -> rank(sender, player, args);
            case "shop" -> shop(player, args);
            case "worlds" -> worlds(player);
            case "world" -> world(player, args);
            case "game" -> game(player, args);
            default -> false;
        };
    }

    private boolean setHub(CommandSender sender) {
        if (!(sender instanceof Player player)) { sender.sendMessage("This command requires a player."); return true; }
        if (!sender.hasPermission("endernetwork.admin")) { deny(sender); return true; }
        Location l = player.getLocation();
        getConfig().set("hub.world", l.getWorld().getName());
        getConfig().set("hub.x", l.getX()); getConfig().set("hub.y", l.getY()); getConfig().set("hub.z", l.getZ());
        getConfig().set("hub.yaw", l.getYaw()); getConfig().set("hub.pitch", l.getPitch());
        saveConfig(); player.sendMessage(msg("Hub location saved.", NamedTextColor.GREEN)); return true;
    }

    private boolean hub(CommandSender sender) {
        if (!(sender instanceof Player player)) { sender.sendMessage("This command requires a player."); return true; }
        String worldName = getConfig().getString("hub.world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) { player.sendMessage(msg("The hub world is unavailable.", NamedTextColor.RED)); return true; }
        Location location = new Location(world, getConfig().getDouble("hub.x"), getConfig().getDouble("hub.y"), getConfig().getDouble("hub.z"), (float)getConfig().getDouble("hub.yaw"), (float)getConfig().getDouble("hub.pitch"));
        player.teleport(location); player.sendMessage(msg("Teleported to the hub.", NamedTextColor.AQUA)); return true;
    }

    private boolean balance(Player p) { p.sendMessage(msg("Balance: " + String.format("%.2f", economy.balance(p.getUniqueId())) + " coins", NamedTextColor.GOLD)); return true; }

    private boolean pay(Player p, String[] args) {
        if (args.length != 2) { p.sendMessage(msg("Usage: /pay <player> <amount>", NamedTextColor.YELLOW)); return true; }
        Player target = Bukkit.getPlayerExact(args[0]);
        double amount;
        try { amount = Double.parseDouble(args[1]); } catch (NumberFormatException e) { amount = -1; }
        if (target == null || amount <= 0 || target.equals(p)) { p.sendMessage(msg("Choose an online player and a positive amount.", NamedTextColor.RED)); return true; }
        if (!economy.withdraw(p.getUniqueId(), amount)) { p.sendMessage(msg("You do not have enough coins.", NamedTextColor.RED)); return true; }
        economy.deposit(target.getUniqueId(), amount);
        p.sendMessage(msg("Payment sent.", NamedTextColor.GREEN)); target.sendMessage(msg(p.getName() + " paid you " + amount + " coins.", NamedTextColor.GREEN)); return true;
    }

    private boolean rank(CommandSender sender, Player p, String[] args) {
        if (args.length == 0) { p.sendMessage(msg("Your rank: " + ranks.getRank(p.getUniqueId()), NamedTextColor.LIGHT_PURPLE)); return true; }
        if (!sender.hasPermission("endernetwork.admin") || args.length != 2) { deny(sender); return true; }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !ranks.setRank(target.getUniqueId(), args[1])) { sender.sendMessage(msg("Unknown player or rank.", NamedTextColor.RED)); return true; }
        sender.sendMessage(msg("Rank updated.", NamedTextColor.GREEN)); target.sendMessage(msg("Your rank is now " + args[1] + ".", NamedTextColor.GREEN)); return true;
    }

    private boolean shop(Player p, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("list")) { p.sendMessage(msg("Shop: bread, iron, diamond. Use /shop buy <item>", NamedTextColor.YELLOW)); return true; }
        if (args.length != 2 || !args[0].equalsIgnoreCase("buy")) { p.sendMessage(msg("Usage: /shop buy <item>", NamedTextColor.YELLOW)); return true; }
        String path = "shop." + args[1].toLowerCase(Locale.ROOT);
        if (!getConfig().isConfigurationSection(path)) { p.sendMessage(msg("Unknown shop item.", NamedTextColor.RED)); return true; }
        double price = getConfig().getDouble(path + ".price"); int amount = getConfig().getInt(path + ".amount");
        Material material = Material.matchMaterial(getConfig().getString(path + ".material", "AIR"));
        if (material == null || !economy.withdraw(p.getUniqueId(), price)) { p.sendMessage(msg("You cannot afford this item.", NamedTextColor.RED)); return true; }
        p.getInventory().addItem(new ItemStack(material, amount)); p.sendMessage(msg("Purchase complete.", NamedTextColor.GREEN)); return true;
    }

    private boolean worlds(Player p) { p.sendMessage(msg("Worlds: " + String.join(", ", Bukkit.getWorlds().stream().map(World::getName).toList()), NamedTextColor.AQUA)); return true; }

    private boolean world(Player p, String[] args) {
        if (args.length == 0) { p.sendMessage(msg("Usage: /world <name> or /world create <name>", NamedTextColor.YELLOW)); return true; }
        String worldName = args[0];
        if (args[0].equalsIgnoreCase("create") && args.length == 2) worldName = args[1];
        World world = Bukkit.getWorld(worldName);
        if (world == null && args[0].equalsIgnoreCase("create")) world = Bukkit.createWorld(new WorldCreator(worldName));
        if (world == null) { p.sendMessage(msg("World not found.", NamedTextColor.RED)); return true; }
        p.teleport(world.getSpawnLocation()); p.sendMessage(msg("Teleported to " + world.getName() + ".", NamedTextColor.AQUA)); return true;
    }

    private boolean game(Player p, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("list")) { p.sendMessage(msg("Games: " + String.join(", ", games.queues().keySet()), NamedTextColor.AQUA)); return true; }
        if (args[0].equalsIgnoreCase("leave")) { games.leave(p.getUniqueId()); p.sendMessage(msg("You left all game queues.", NamedTextColor.YELLOW)); return true; }
        if (args[0].equalsIgnoreCase("join") && args.length == 2 && games.join(args[1], p.getUniqueId())) { p.sendMessage(msg("You joined the " + args[1] + " queue.", NamedTextColor.GREEN)); return true; }
        p.sendMessage(msg("Usage: /game list, /game join <game>, /game leave", NamedTextColor.YELLOW)); return true;
    }

    private void deny(CommandSender sender) { sender.sendMessage(msg("You do not have permission.", NamedTextColor.RED)); }
    private Component msg(String text, NamedTextColor color) { return Component.text(text).color(color); }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("game") && args.length == 2 && args[0].equalsIgnoreCase("join")) return games.queues().keySet().stream().toList();
        if (command.getName().equalsIgnoreCase("shop") && args.length == 2 && args[0].equalsIgnoreCase("buy")) return getConfig().getConfigurationSection("shop").getKeys(false).stream().toList();
        return java.util.List.of();
    }
}
