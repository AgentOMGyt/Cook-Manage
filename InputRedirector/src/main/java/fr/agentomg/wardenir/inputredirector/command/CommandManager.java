package fr.agentomg.wardenir.inputredirector.command;

import fr.agentomg.wardenir.inputredirector.InputRedirector;
import fr.agentomg.wardenir.inputredirector.command.impl.DebugPigsCommand;
import fr.agentomg.wardenir.inputredirector.command.impl.FourCommand;
import fr.agentomg.wardenir.inputredirector.command.impl.GateCommand;
import fr.agentomg.wardenir.inputredirector.command.impl.MainCommand;
import fr.agentomg.wardenir.inputredirector.manager.GateManager;
import fr.agentomg.wardenir.inputredirector.manager.MobManager;
import fr.agentomg.wardenir.inputredirector.manager.PigGuiManager;
import fr.agentomg.wardenir.inputredirector.manager.PlayerStateManager;

public class CommandManager {
    private final InputRedirector plugin;
    private final PlayerStateManager playerStateManager;
    private final MobManager mobManager;
    private final GateManager gateManager;
    private final PigGuiManager pigGuiManager;

    private final FourCommand fourCommand;
    private final GateCommand gateCommand;
    private final DebugPigsCommand debugPigsCommand;
    private final MainCommand mainCommand;

    public CommandManager(InputRedirector plugin, PlayerStateManager playerStateManager, MobManager mobManager,
                          GateManager gateManager, PigGuiManager pigGuiManager) {
        this.plugin = plugin;
        this.playerStateManager = playerStateManager;
        this.mobManager = mobManager;
        this.gateManager = gateManager;
        this.pigGuiManager = pigGuiManager;

        this.fourCommand = new FourCommand(pigGuiManager);
        this.gateCommand = new GateCommand(gateManager, plugin); // Passer le plugin ici
        this.debugPigsCommand = new DebugPigsCommand(pigGuiManager);
        this.mainCommand = new MainCommand(plugin, playerStateManager, mobManager);
    }

    public void registerCommands() {
        plugin.getCommand("four").setExecutor(fourCommand);
        plugin.getCommand("gate").setExecutor(gateCommand);
        plugin.getCommand("debugpigs").setExecutor(debugPigsCommand);

        // Pour la commande principale, on l'enregistre via onCommand dans InputRedirector
        plugin.getServer().getPluginCommand(plugin.getName().toLowerCase()).setExecutor(mainCommand);
    }

    public MainCommand getMainCommand() {
        return mainCommand;
    }
}