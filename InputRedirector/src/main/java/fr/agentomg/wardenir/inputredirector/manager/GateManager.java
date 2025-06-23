package fr.agentomg.wardenir.inputredirector.manager;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GateManager {
    private Location gateLocation = null;
    private final Map<UUID, Long> gateCooldowns = new HashMap<>();
    private final long COOLDOWN_MS = 5000;

    public Location getGateLocation() { return gateLocation; }
    public void setGateLocation(Location location) { this.gateLocation = location; }

    public boolean isInGateArea(Location loc) {
        return gateLocation != null && loc.getWorld().equals(gateLocation.getWorld()) && loc.distanceSquared(gateLocation) <= 1;
    }

    public boolean canTrigger(UUID id) {
        return !gateCooldowns.containsKey(id) || System.currentTimeMillis() - gateCooldowns.get(id) >= COOLDOWN_MS;
    }

    public void updateCooldown(UUID id) {
        gateCooldowns.put(id, System.currentTimeMillis());
    }
}
