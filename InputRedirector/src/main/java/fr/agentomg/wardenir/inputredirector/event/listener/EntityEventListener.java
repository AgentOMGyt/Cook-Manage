package fr.agentomg.wardenir.inputredirector.event.listener;

import fr.agentomg.wardenir.inputredirector.manager.MobManager;
import fr.agentomg.wardenir.inputredirector.manager.PigGuiManager;
import org.bukkit.entity.Pig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityEventListener implements Listener {
    private final MobManager mobManager;
    private final PigGuiManager pigGuiManager;

    public EntityEventListener(MobManager mobManager, PigGuiManager pigGuiManager) {
        this.mobManager = mobManager;
        this.pigGuiManager = pigGuiManager;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        // Protection des mobs redirigés
        if (mobManager.getRedirectedMobs().values().stream().anyMatch(m -> m.getEntity().getBukkitEntity().equals(e.getEntity())))
            e.setCancelled(true);

        // Nettoyage des cochons morts
        if (e.getEntity() instanceof Pig) {
            Pig pig = (Pig) e.getEntity();
            pigGuiManager.getPigInventories().remove(pig);
        }
    }
}
