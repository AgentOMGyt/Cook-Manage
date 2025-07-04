package fr.agentomg.wardenir.inputredirector;

import fr.agentomg.wardenir.inputredirector.command.CommandManager;
import fr.agentomg.wardenir.inputredirector.event.EventManager;
import fr.agentomg.wardenir.inputredirector.manager.GateManager;
import fr.agentomg.wardenir.inputredirector.manager.GateSaveManager;
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
    private GateSaveManager gateSaveManager;
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

        // Chargement des données sauvegardées (après un délai pour s'assurer que le monde est chargé)
        Bukkit.getScheduler().runTaskLater(this, () -> {
            // Chargement des cochons
            pigGuiManager.loadPigs();

            // Chargement des gates (CHANGEMENT ICI)
            gateSaveManager.loadGates(); // Utiliser loadGates() au lieu de loadGate()

            getLogger().info("Plugin InputRedirector activé avec succès !");
        }, 20L); // 1 seconde de délai
    }

    @Override
    public void onDisable() {
        // Sauvegarde des données avant l'arrêt
        getLogger().info("Sauvegarde des données en cours...");

        // Sauvegarde des cochons
        pigGuiManager.savePigs();

        // Sauvegarde des gates (CHANGEMENT ICI)
        gateSaveManager.saveGates(); // Utiliser saveGates() au lieu de saveGate()

        // Nettoyage
        cleanupOnDisable();

        getLogger().info("Plugin InputRedirector désactivé avec succès !");
    }

    private void initializeManagers() {
        playerStateManager = new PlayerStateManager();
        mobManager = new MobManager();
        gateManager = new GateManager();
        gateSaveManager = new GateSaveManager(this, gateManager);
        pigGuiManager = new PigGuiManager();

        // Initialiser le PigGuiManager avec le plugin pour la sauvegarde
        pigGuiManager.initialize(this);

        commandManager = new CommandManager(this, playerStateManager, mobManager, gateManager, pigGuiManager);
        eventManager = new EventManager(this, playerStateManager, mobManager, gateManager, pigGuiManager);

        mobUpdateTask = new MobUpdateTask(playerStateManager, mobManager, gateManager);
        playerRotationTask = new PlayerRotationTask(playerStateManager, mobManager);
    }

    private void startTasks() {
        Bukkit.getScheduler().runTaskTimer(this, mobUpdateTask, 0L, 1L);
        Bukkit.getScheduler().runTaskTimer(this, playerRotationTask, 0L, 1L);

        // Tâche de sauvegarde automatique toutes les 5 minutes (CHANGEMENT ICI)
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            pigGuiManager.savePigs();
            gateSaveManager.saveGates(); // Utiliser saveGates() au lieu de saveGate()
            getLogger().info("Sauvegarde automatique effectuée (cochons + gates)");
        }, 6000L, 6000L); // 5 minutes
    }

    private void cleanupOnDisable() {
        // Nettoyage des données en mémoire
        playerStateManager.getFrozenPlayers().clear();
        mobManager.getRedirectedMobs().values().forEach(mob -> {
            if (mob.getEntity().isValid()) mob.getEntity().getBukkitEntity().remove();
        });
        mobManager.getRedirectedMobs().clear();
        mobManager.getLastPositions().clear();
        mobManager.getOriginalPositions().clear();
        mobManager.getPreviousMobLocations().clear();

        // Supprimer tous les cochons spawnés par le plugin
        pigGuiManager.getPigInventories().keySet().forEach(pig -> {
            if (pig.isValid()) {
                pig.remove();
            }
        });
        pigGuiManager.getPigInventories().clear();

        getLogger().info("Tous les cochons du plugin ont été supprimés");
    }

    // === GETTERS ===
    public PlayerStateManager getPlayerStateManager() { return playerStateManager; }
    public MobManager getMobManager() { return mobManager; }
    public GateManager getGateManager() { return gateManager; }
    public GateSaveManager getGateSaveManager() { return gateSaveManager; }
    public PigGuiManager getPigGuiManager() { return pigGuiManager; }
}