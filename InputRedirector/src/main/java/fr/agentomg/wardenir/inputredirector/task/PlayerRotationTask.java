package fr.agentomg.wardenir.inputredirector.task;

import fr.agentomg.wardenir.inputredirector.manager.MobManager;
import fr.agentomg.wardenir.inputredirector.manager.PlayerStateManager;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class PlayerRotationTask implements Runnable {
    private final PlayerStateManager playerStateManager;
    private final MobManager mobManager;

    public PlayerRotationTask(PlayerStateManager playerStateManager, MobManager mobManager) {
        this.playerStateManager = playerStateManager;
        this.mobManager = mobManager;
    }

    @Override
    public void run() {
        for (UUID playerId : playerStateManager.getFrozenPlayers()) {
            updatePlayerRotation(playerId);
        }
    }

    private void updatePlayerRotation(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        ActiveMob mob = mobManager.getRedirectedMobs().get(playerId);

        if (player == null || mob == null || !mob.getEntity().isValid()) {
            return;
        }

        Location mobLoc = mob.getEntity().getBukkitEntity().getLocation().clone();
        Vector dir = mobLoc.toVector().subtract(player.getLocation().toVector());

        float yaw = (float) Math.toDegrees(Math.atan2(-dir.getX(), dir.getZ()));
        float pitch = (float) Math.toDegrees(-Math.atan2(dir.getY(),
                Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ())));

        player.setRotation(yaw, pitch);
    }
}
