package fr.agentomg.wardenir.inputredirector.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerStateManager {
    private final Set<UUID> frozenPlayers = new HashSet<>();

    public Set<UUID> getFrozenPlayers() {
        return frozenPlayers;
    }

    public boolean isFrozen(UUID playerId) {
        return frozenPlayers.contains(playerId);
    }

    public void freeze(UUID playerId) {
        frozenPlayers.add(playerId);
    }

    public void unfreeze(UUID playerId) {
        frozenPlayers.remove(playerId);
    }
}