package fr.agentomg.wardenir.inputredirector.command.impl;

import fr.agentomg.wardenir.inputredirector.InputRedirector;
import fr.agentomg.wardenir.inputredirector.manager.GateManager;
import fr.agentomg.wardenir.inputredirector.manager.GateSaveManager;
import fr.agentomg.wardenir.inputredirector.util.LocationUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GateCommand implements CommandExecutor {
    private final GateManager gateManager;
    private final InputRedirector plugin;

    public GateCommand(GateManager gateManager, InputRedirector plugin) {
        this.gateManager = gateManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (!player.hasPermission("wardenir.gate")) {
            player.sendMessage("§cPermission refusée.");
            return true;
        }

        if (args.length == 0) {
            // Commande principale : définir une nouvelle gate
            gateManager.setGateLocation(player.getLocation().clone());

            // Sauvegarde automatique
            GateSaveManager gateSaveManager = plugin.getGateSaveManager();
            if (gateSaveManager != null) {
                gateSaveManager.saveGate();
            }

            player.sendMessage("§aGate position saved at " + LocationUtil.formatLocation(gateManager.getGateLocation()));
            return true;
        }

        // Sous-commandes
        switch (args[0].toLowerCase()) {
            case "remove":
            case "delete":
                if (gateManager.getGateLocation() != null) {
                    gateManager.setGateLocation(null);

                    // Sauvegarde automatique (suppression)
                    GateSaveManager gateSaveManager = plugin.getGateSaveManager();
                    if (gateSaveManager != null) {
                        gateSaveManager.saveGate();
                    }

                    player.sendMessage("§aGate supprimée avec succès.");
                } else {
                    player.sendMessage("§cAucune gate à supprimer.");
                }
                break;

            case "info":
            case "status":
                if (gateManager.getGateLocation() != null) {
                    player.sendMessage("§6Gate actuelle : " + LocationUtil.formatLocation(gateManager.getGateLocation()));
                } else {
                    player.sendMessage("§cAucune gate définie.");
                }
                break;

            case "tp":
            case "teleport":
                if (gateManager.getGateLocation() != null) {
                    player.teleport(gateManager.getGateLocation());
                    player.sendMessage("§aTéléporté à la gate !");
                } else {
                    player.sendMessage("§cAucune gate définie pour la téléportation.");
                }
                break;

            case "reload":
                GateSaveManager gateSaveManager = plugin.getGateSaveManager();
                if (gateSaveManager != null) {
                    gateSaveManager.loadGate();
                    if (gateManager.getGateLocation() != null) {
                        player.sendMessage("§aGate rechargée : " + LocationUtil.formatLocation(gateManager.getGateLocation()));
                    } else {
                        player.sendMessage("§cAucune gate trouvée dans la sauvegarde.");
                    }
                } else {
                    player.sendMessage("§cErreur : gestionnaire de sauvegarde indisponible.");
                }
                break;

            default:
                player.sendMessage("§cUsage:");
                player.sendMessage("§7/gate - Définir une gate à votre position");
                player.sendMessage("§7/gate remove - Supprimer la gate actuelle");
                player.sendMessage("§7/gate info - Afficher les informations de la gate");
                player.sendMessage("§7/gate tp - Se téléporter à la gate");
                player.sendMessage("§7/gate reload - Recharger la gate depuis la sauvegarde");
                break;
        }

        return true;
    }
}
