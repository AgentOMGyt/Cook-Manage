package fr.agentomg.wardenir.inputredirector.manager;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.api.mobs.MythicMob;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MobManager {
    private final Map<UUID, ActiveMob> redirectedMobs = new HashMap<>();
    private final Map<UUID, Location> lastPositions = new HashMap<>();
    private final Map<UUID, Location> originalPositions = new HashMap<>();
    private final Map<UUID, Location> previousMobLocations = new HashMap<>();

    public Map<UUID, ActiveMob> getRedirectedMobs() { return redirectedMobs; }
    public Map<UUID, Location> getLastPositions() { return lastPositions; }
    public Map<UUID, Location> getOriginalPositions() { return originalPositions; }
    public Map<UUID, Location> getPreviousMobLocations() { return previousMobLocations; }

    public void startRedirection(Player player) {
        UUID playerId = player.getUniqueId();
        Optional<MythicMob> optMob = MythicBukkit.inst().getMobManager().getMythicMob("test_hand");
        if (!optMob.isPresent()) {
            player.sendMessage("§cMythicMob 'test_hand' introuvable.");
            return;
        }

        Location originalLoc = player.getLocation();
        ActiveMob mob = optMob.get().spawn(BukkitAdapter.adapt(originalLoc), 1.0);
        if (mob == null || !mob.getEntity().isValid()) {
            player.sendMessage("§cÉchec du spawn du mob.");
            return;
        }

        Vector back = originalLoc.getDirection().multiply(-5).setY(0);
        Location newLoc = originalLoc.clone().add(back).add(0, 5, 0);

        player.teleport(newLoc);
        originalPositions.put(playerId, originalLoc.clone());
        redirectedMobs.put(playerId, mob);
        lastPositions.put(playerId, originalLoc.clone());
        previousMobLocations.put(playerId, originalLoc.clone());

        player.setAllowFlight(true);
        player.setFlying(true);
        player.sendMessage("§aRedirection activée.");
    }

    public void stopRedirection(Player player, PlayerStateManager stateManager) {
        UUID playerId = player.getUniqueId();
        stateManager.unfreeze(playerId);

        ActiveMob mob = redirectedMobs.remove(playerId);
        if (mob != null && mob.getEntity().isValid()) mob.getEntity().getBukkitEntity().remove();

        lastPositions.remove(playerId);
        previousMobLocations.remove(playerId);

        Location originalLoc = originalPositions.remove(playerId);
        if (originalLoc != null) player.teleport(originalLoc);

        player.setAllowFlight(false);
        player.sendMessage("§aRedirection désactivée.");
    }
}
