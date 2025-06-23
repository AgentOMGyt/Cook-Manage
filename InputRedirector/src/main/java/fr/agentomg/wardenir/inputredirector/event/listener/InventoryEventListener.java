package fr.agentomg.wardenir.inputredirector.event.listener;

import fr.agentomg.wardenir.inputredirector.manager.PigGuiManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryEventListener implements Listener {
    private final PigGuiManager pigGuiManager;

    public InventoryEventListener(PigGuiManager pigGuiManager) {
        this.pigGuiManager = pigGuiManager;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        pigGuiManager.onInventoryClose(event);
    }
}
