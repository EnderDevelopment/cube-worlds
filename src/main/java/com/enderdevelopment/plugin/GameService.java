package com.enderdevelopment.plugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class GameService {
    private final Map<String, List<UUID>> queues = new LinkedHashMap<>();

    public GameService() {
        queues.put("skywars", new ArrayList<>());
        queues.put("bedwars", new ArrayList<>());
        queues.put("survival", new ArrayList<>());
    }

    public Map<String, List<UUID>> queues() { return queues; }

    public boolean join(String game, UUID player) {
        List<UUID> queue = queues.get(game.toLowerCase());
        if (queue == null || queue.contains(player)) return false;
        queue.add(player);
        return true;
    }

    public boolean leave(UUID player) {
        return queues.values().stream().anyMatch(queue -> queue.remove(player));
    }
}
