package fr.agentomg.wardenir.inputredirector.manager;

import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PigGuiManager {
    private final Map<Pig, Inventory> pigInventories = new HashMap<>();

    public Map<Pig, Inventory> getPigInventories() {
        return pigInventories;
    }

    public void handlePigGui(Player player, MobManager mobManager) {
        UUID playerId = player.getUniqueId();
        ActiveMob mob = mobManager.getRedirectedMobs().get(playerId);
        if (mob == null || !mob.getEntity().isValid()) return;

        Entity mobEntity = mob.getEntity().getBukkitEntity();
        Location mobLoc = mobEntity.getLocation();

        Pig nearest = null;
        double minDist = 2.5;

        for (Pig pig : pigInventories.keySet()) {
            if (!pig.isValid() || !pig.getWorld().equals(mobLoc.getWorld())) continue;

            double dist = pig.getLocation().distance(mobLoc);
            player.sendMessage("§eDistance cochon-mob: " + dist);
            if (dist < minDist) {
                nearest = pig;
                minDist = dist;
            }
        }

        if (nearest == null) {
            player.sendMessage("§cAucun cochon avec GUI à portée du mob.");
            return;
        }

        Inventory inv = pigInventories.get(nearest);
        if (inv == null) {
            player.sendMessage("§cGUI introuvable pour ce cochon.");
            return;
        }

        player.openInventory(inv);
    }

    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Inventory inv = event.getInventory();

        for (Map.Entry<Pig, Inventory> entry : pigInventories.entrySet()) {
            if (entry.getValue().equals(inv)) {
                return;
            }
        }
    }
}
