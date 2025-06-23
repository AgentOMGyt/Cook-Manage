package fr.agentomg.wardenir.inputredirector;

import fr.agentomg.wardenir.inputredirector.command.CommandManager;
import fr.agentomg.wardenir.inputredirector.event.EventManager;
import fr.agentomg.wardenir.inputredirector.manager.GateManager;
import fr.agentomg.wardenir.inputredirector.manager.MobManager;
import fr.agentomg.wardenir.inputredirector.manager.PigGuiManager;
import fr.agentomg.wardenir.inputredirector.manager.PlayerStateManager;
import fr.agentomg.wardenir.inputredirector.task.MobUpdateTask;
import fr.agentomg.wardenir.inputredirector.task.PlayerRotationTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class InputRedirector extends JavaPlugin {

    private PlayerStateManager playerStateManager;
    private MobManager mobManager;
    private GateManager gateManager;
    private PigGuiManager pigGuiManager;
    private CommandManager commandManager;
    private EventManager eventManager;
    private MobUpdateTask mobUpdateTask;
    private PlayerRotationTask playerRotationTask;

    @Override
    public void onEnable() {
        // Initialisation des managers
        initializeManagers();

        // Enregistrement des événements
        eventManager.registerEvents();

        // Enregistrement des commandes
        commandManager.registerCommands();

        // Démarrage des tâches
        startTasks();
    }

    @Override
    public void onDisable() {
        // Nettoyage
        cleanupOnDisable();
    }

    private void initializeManagers() {
        playerStateManager = new PlayerStateManager();
        mobManager = new MobManager();
        gateManager = new GateManager();
        pigGuiManager = new PigGuiManager();

        commandManager = new CommandManager(this, playerStateManager, mobManager, gateManager, pigGuiManager);
        eventManager = new EventManager(this, playerStateManager, mobManager, gateManager, pigGuiManager);

        mobUpdateTask = new MobUpdateTask(playerStateManager, mobManager, gateManager);
        playerRotationTask = new PlayerRotationTask(playerStateManager, mobManager);
    }

    private void startTasks() {
        Bukkit.getScheduler().runTaskTimer(this, mobUpdateTask, 0L, 1L);
        Bukkit.getScheduler().runTaskTimer(this, playerRotationTask, 0L, 1L);
    }

    private void cleanupOnDisable() {
        playerStateManager.getFrozenPlayers().clear();
        mobManager.getRedirectedMobs().values().forEach(mob -> {
            if (mob.getEntity().isValid()) mob.getEntity().getBukkitEntity().remove();
        });
        mobManager.getRedirectedMobs().clear();
        mobManager.getLastPositions().clear();
        mobManager.getOriginalPositions().clear();
        mobManager.getPreviousMobLocations().clear();
        pigGuiManager.getPigInventories().clear();
    }

    // === GETTERS ===
    public PlayerStateManager getPlayerStateManager() { return playerStateManager; }
    public MobManager getMobManager() { return mobManager; }
    public GateManager getGateManager() { return gateManager; }
    public PigGuiManager getPigGuiManager() { return pigGuiManager; }
}