package fr.agentomg.wardenir.inputredirector.event;

import fr.agentomg.wardenir.inputredirector.InputRedirector;
import fr.agentomg.wardenir.inputredirector.event.listener.EntityEventListener;
import fr.agentomg.wardenir.inputredirector.event.listener.InventoryEventListener;
import fr.agentomg.wardenir.inputredirector.event.listener.PlayerEventListener;
import fr.agentomg.wardenir.inputredirector.manager.GateManager;
import fr.agentomg.wardenir.inputredirector.manager.MobManager;
import fr.agentomg.wardenir.inputredirector.manager.PigGuiManager;
import fr.agentomg.wardenir.inputredirector.manager.PlayerStateManager;
import org.bukkit.Bukkit;

public class EventManager {
    private final InputRedirector plugin;
    private final PlayerEventListener playerEventListener;
    private final EntityEventListener entityEventListener;
    private final InventoryEventListener inventoryEventListener;

    public EventManager(InputRedirector plugin, PlayerStateManager playerStateManager, MobManager mobManager,
                        GateManager gateManager, PigGuiManager pigGuiManager) {
        this.plugin = plugin;
        this.playerEventListener = new PlayerEventListener(plugin, playerStateManager, mobManager, gateManager, pigGuiManager);
        this.entityEventListener = new EntityEventListener(mobManager, pigGuiManager);
        this.inventoryEventListener = new InventoryEventListener(pigGuiManager);
    }

    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(playerEventListener, plugin);
        Bukkit.getPluginManager().registerEvents(entityEventListener, plugin);
        Bukkit.getPluginManager().registerEvents(inventoryEventListener, plugin);
    }
}
