package fr.agentomg.wardenir.inputredirector.command.impl;

import fr.agentomg.wardenir.inputredirector.InputRedirector;
import fr.agentomg.wardenir.inputredirector.manager.GateManager;
import fr.agentomg.wardenir.inputredirector.manager.GateSaveManager;
import fr.agentomg.wardenir.inputredirector.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class GateCommand implements CommandExecutor {
    private final GateManager gateManager;
    private final InputRedirector plugin;

    public GateCommand(GateManager gateManager, InputRedirector plugin) {
        this.gateManager = gateManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("wardenir.gate")) {
            player.sendMessage("§cPermission refusée.");
            return true;
        }

        if (args.length == 0) {
            showUsage(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
            case "add":
            case "set":
                handleCreateGate(player, args);
                break;

            case "remove":
            case "delete":
                handleRemoveGate(player, args);
                break;

            case "list":
                handleListGates(player);
                break;

            case "info":
                handleGateInfo(player, args);
                break;

            case "tp":
            case "teleport":
                handleTeleport(player, args);
                break;

            case "reload":
                handleReload(player);
                break;

            case "save":
                handleSave(player);
                break;

            default:
                showUsage(player);
                break;
        }

        return true;
    }

    private void handleCreateGate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /gate create <nom>");
            return;
        }

        String gateName = args[1];
        Location location = player.getLocation();

        // Vérifier si la gate existe déjà
        boolean existed = gateManager.hasGate(gateName);

        gateManager.addGate(gateName, location);

        // Sauvegarde automatique
        GateSaveManager gateSaveManager = plugin.getGateSaveManager();
        if (gateSaveManager != null) {
            gateSaveManager.saveGate(gateName);
        }

        if (existed) {
            player.sendMessage("§aGate '" + gateName + "' mise à jour à " + LocationUtil.formatLocation(location));
        } else {
            player.sendMessage("§aGate '" + gateName + "' créée à " + LocationUtil.formatLocation(location));
        }
    }

    private void handleRemoveGate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /gate remove <nom>");
            return;
        }

        String gateName = args[1];

        if (!gateManager.hasGate(gateName)) {
            player.sendMessage("§cLa gate '" + gateName + "' n'existe pas.");
            return;
        }

        gateManager.removeGate(gateName);

        // Sauvegarde automatique (suppression)
        GateSaveManager gateSaveManager = plugin.getGateSaveManager();
        if (gateSaveManager != null) {
            gateSaveManager.saveGate(gateName);
        }

        player.sendMessage("§aGate '" + gateName + "' supprimée avec succès.");
    }

    private void handleListGates(Player player) {
        Map<String, Location> gates = gateManager.getAllGates();

        if (gates.isEmpty()) {
            player.sendMessage("§cAucune gate définie.");
            return;
        }

        player.sendMessage("§6=== Liste des Gates (" + gates.size() + ") ===");
        for (Map.Entry<String, Location> entry : gates.entrySet()) {
            String name = entry.getKey();
            Location loc = entry.getValue();
            player.sendMessage("§e" + name + "§7: " + LocationUtil.formatLocation(loc));
        }
    }

    private void handleGateInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /gate info <nom>");
            return;
        }

        String gateName = args[1];
        Location location = gateManager.getGate(gateName);

        if (location == null) {
            player.sendMessage("§cLa gate '" + gateName + "' n'existe pas.");
            return;
        }

        player.sendMessage("§6=== Informations de la Gate '" + gateName + "' ===");
        player.sendMessage("§7Position: " + LocationUtil.formatLocationWithRotation(location));
        player.sendMessage("§7Distance: §f" + String.format("%.2f", player.getLocation().distance(location)) + " blocs");
    }

    private void handleTeleport(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /gate tp <nom>");
            return;
        }

        String gateName = args[1];
        Location location = gateManager.getGate(gateName);

        if (location == null) {
            player.sendMessage("§cLa gate '" + gateName + "' n'existe pas.");
            return;
        }

        player.teleport(location);
        player.sendMessage("§aTéléporté à la gate '" + gateName + "' !");
    }

    private void handleReload(Player player) {
        GateSaveManager gateSaveManager = plugin.getGateSaveManager();
        if (gateSaveManager == null) {
            player.sendMessage("§cErreur : gestionnaire de sauvegarde indisponible.");
            return;
        }

        int oldCount = gateManager.getGateCount();
        gateSaveManager.loadGates();
        int newCount = gateManager.getGateCount();

        player.sendMessage("§aGates rechargées ! (" + oldCount + " → " + newCount + ")");
    }

    private void handleSave(Player player) {
        GateSaveManager gateSaveManager = plugin.getGateSaveManager();
        if (gateSaveManager == null) {
            player.sendMessage("§cErreur : gestionnaire de sauvegarde indisponible.");
            return;
        }

        gateSaveManager.saveGates();
        player.sendMessage("§aToutes les gates ont été sauvegardées ! (" + gateManager.getGateCount() + " gates)");
    }

    private void showUsage(Player player) {
        player.sendMessage("§6=== Commandes Gate ===");
        player.sendMessage("§e/gate create <nom> §7- Créer une gate à votre position");
        player.sendMessage("§e/gate remove <nom> §7- Supprimer une gate");
        player.sendMessage("§e/gate list §7- Lister toutes les gates");
        player.sendMessage("§e/gate info <nom> §7- Informations sur une gate");
        player.sendMessage("§e/gate tp <nom> §7- Se téléporter à une gate");
        player.sendMessage("§e/gate reload §7- Recharger les gates depuis la sauvegarde");
        player.sendMessage("§e/gate save §7- Sauvegarder toutes les gates");
    }
}