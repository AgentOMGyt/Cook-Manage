package fr.agentomg.wardenir.inputredirector.service;

import fr.agentomg.wardenir.inputredirector.manager.MobManager;
import fr.agentomg.wardenir.inputredirector.manager.PlayerStateManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class MovementService {
    private final PlayerStateManager playerStateManager;
    private final MobManager mobManager;

    public MovementService(PlayerStateManager playerStateManager, MobManager mobManager) {
        this.playerStateManager = playerStateManager;
        this.mobManager = mobManager;
    }

    public void handlePlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        Location to = event.getTo();
        Location from = event.getFrom();

        if (to == null || !playerStateManager.isFrozen(playerId) || from.distanceSquared(to) == 0) {
            return;
        }

        // Bloquer le mouvement physique mais permettre la rotation
        Location locked = from.clone();
        locked.setYaw(to.getYaw());
        locked.setPitch(to.getPitch());
        locked.setY(from.getY());
        event.setTo(locked);

        // Mettre à jour la position virtuelle
        updateVirtualPosition(playerId, from, to);
    }

    private void updateVirtualPosition(UUID playerId, Location from, Location to) {
        Location virtual = mobManager.getLastPositions().get(playerId);
        if (virtual == null) return;

        double multiplier = 3.0;
        double dx = (to.getX() - from.getX()) * multiplier;
        double dz = (to.getZ() - from.getZ()) * multiplier;

        virtual.add(dx, 0, dz);
        virtual.setYaw((float) Math.toDegrees(Math.atan2(-dx, dz)));
        mobManager.getLastPositions().put(playerId, virtual.clone());
    }
}

