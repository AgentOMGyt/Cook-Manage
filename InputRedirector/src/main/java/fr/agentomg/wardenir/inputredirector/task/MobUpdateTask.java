package fr.agentomg.wardenir.inputredirector.task;

import fr.agentomg.wardenir.inputredirector.manager.GateManager;
import fr.agentomg.wardenir.inputredirector.manager.MobManager;
import fr.agentomg.wardenir.inputredirector.manager.PlayerStateManager;
import fr.agentomg.wardenir.inputredirector.util.MobUtil;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MobUpdateTask implements Runnable {
    private final PlayerStateManager playerStateManager;
    private final MobManager mobManager;
    private final GateManager gateManager;

    public MobUpdateTask(PlayerStateManager playerStateManager, MobManager mobManager, GateManager gateManager) {
        this.playerStateManager = playerStateManager;
        this.mobManager = mobManager;
        this.gateManager = gateManager;
    }

    @Override
    public void run() {
        for (UUID playerId : mobManager.getRedirectedMobs().keySet()) {
            updateMob(playerId);
        }
    }

    private void updateMob(UUID playerId) {
        ActiveMob mob = mobManager.getRedirectedMobs().get(playerId);
        Location target = mobManager.getLastPositions().get(playerId);

        if (mob == null || target == null || !mob.getEntity().isValid()) {
            return;
        }

        LivingEntity entity = (LivingEntity) mob.getEntity().getBukkitEntity();
        Location current = entity.getLocation();

        // Interpolation de position
        Location lerped = current.clone().add(
                (target.getX() - current.getX()) * 0.2,
                (target.getY() - current.getY()) * 0.2,
                (target.getZ() - current.getZ()) * 0.2
        );

        if (MobUtil.isPathBlocked(lerped)) {
            return;
        }

        // Gestion de l'animation de marche
        updateWalkingAnimation(playerId, current);

        // Mise à jour de la rotation
        lerped.setYaw(MobUtil.lerpAngle(current.getYaw(), target.getYaw(), 0.2f));
        lerped.setPitch(current.getPitch() + (target.getPitch() - current.getPitch()) * 0.2f);

        entity.setNoDamageTicks(1);
        entity.teleport(lerped);

        // Vérification de la gate
        checkGateInteraction(playerId, current);
    }

    private void updateWalkingAnimation(UUID playerId, Location current) {
        Location previous = mobManager.getPreviousMobLocations().getOrDefault(playerId, current);
        boolean walking = previous.distanceSquared(current) > 0.001;

        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            player.getInventory().setHeldItemSlot(walking ? 1 : 0);
        }

        mobManager.getPreviousMobLocations().put(playerId, current.clone());
    }

    private void checkGateInteraction(UUID playerId, Location current) {
        if (gateManager.isInGateArea(current) && gateManager.canTrigger(playerId)) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.performCommand("inputredirector");
            }
            gateManager.updateCooldown(playerId);
        }
    }
}
