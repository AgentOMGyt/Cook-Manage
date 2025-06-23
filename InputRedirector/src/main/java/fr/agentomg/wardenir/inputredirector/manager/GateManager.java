package fr.agentomg.wardenir.inputredirector.manager;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GateManager {
    private final Map<String, Location> gates = new HashMap<>();
    private final Map<UUID, Long> gateCooldowns = new HashMap<>();
    private final long COOLDOWN_MS = 5000;

    // === GESTION DES GATES ===

    /**
     * Ajoute ou met à jour une gate
     * @param name Nom de la gate
     * @param location Position de la gate
     */
    public void addGate(String name, Location location) {
        gates.put(name, location.clone());
    }

    /**
     * Supprime une gate
     * @param name Nom de la gate à supprimer
     * @return true si la gate existait et a été supprimée
     */
    public boolean removeGate(String name) {
        return gates.remove(name) != null;
    }

    /**
     * Récupère une gate par son nom
     * @param name Nom de la gate
     * @return Location de la gate ou null si inexistante
     */
    public Location getGate(String name) {
        Location loc = gates.get(name);
        return loc != null ? loc.clone() : null;
    }

    /**
     * Récupère toutes les gates
     * @return Map des gates (nom -> location)
     */
    public Map<String, Location> getAllGates() {
        Map<String, Location> result = new HashMap<>();
        for (Map.Entry<String, Location> entry : gates.entrySet()) {
            result.put(entry.getKey(), entry.getValue().clone());
        }
        return result;
    }

    /**
     * Vérifie si une gate existe
     * @param name Nom de la gate
     * @return true si la gate existe
     */
    public boolean hasGate(String name) {
        return gates.containsKey(name);
    }

    /**
     * Récupère le nombre de gates
     * @return Nombre de gates
     */
    public int getGateCount() {
        return gates.size();
    }

    /**
     * Supprime toutes les gates
     */
    public void clearAllGates() {
        gates.clear();
    }

    // === DÉTECTION DE ZONE ===

    /**
     * Vérifie si une location est dans une zone de gate
     * @param loc Location à vérifier
     * @return Nom de la gate si dans la zone, null sinon
     */
    public String getGateAtLocation(Location loc) {
        for (Map.Entry<String, Location> entry : gates.entrySet()) {
            Location gateLoc = entry.getValue();
            if (isInGateArea(loc, gateLoc)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Vérifie si une location est dans la zone d'une gate spécifique
     * @param loc Location à vérifier
     * @param gateLoc Location de la gate
     * @return true si dans la zone
     */
    public boolean isInGateArea(Location loc, Location gateLoc) {
        return gateLoc != null &&
                loc.getWorld().equals(gateLoc.getWorld()) &&
                loc.distanceSquared(gateLoc) <= 1;
    }

    // === GESTION DES COOLDOWNS ===

    /**
     * Vérifie si un joueur peut déclencher une gate
     * @param playerId UUID du joueur
     * @return true si le cooldown est écoulé
     */
    public boolean canTrigger(UUID playerId) {
        return !gateCooldowns.containsKey(playerId) ||
                System.currentTimeMillis() - gateCooldowns.get(playerId) >= COOLDOWN_MS;
    }

    /**
     * Met à jour le cooldown d'un joueur
     * @param playerId UUID du joueur
     */
    public void updateCooldown(UUID playerId) {
        gateCooldowns.put(playerId, System.currentTimeMillis());
    }

    // === MÉTHODES DE COMPATIBILITÉ (pour l'ancien système) ===

    /**
     * @deprecated Utilisez getGate("default") à la place
     */
    @Deprecated
    public Location getGateLocation() {
        return getGate("default");
    }

    /**
     * @deprecated Utilisez addGate("default", location) à la place
     */
    @Deprecated
    public void setGateLocation(Location location) {
        if (location == null) {
            removeGate("default");
        } else {
            addGate("default", location);
        }
    }

    /**
     * @deprecated Utilisez getGateAtLocation(loc) != null à la place
     */
    @Deprecated
    public boolean isInGateArea(Location loc) {
        return getGateAtLocation(loc) != null;
    }
}