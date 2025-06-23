package fr.agentomg.wardenir.inputredirector.command.impl;

import fr.agentomg.wardenir.inputredirector.manager.GateManager;
import fr.agentomg.wardenir.inputredirector.util.LocationUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GateCommand implements CommandExecutor {
    private final GateManager gateManager;

    public GateCommand(GateManager gateManager) {
        this.gateManager = gateManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (!player.hasPermission("wardenir.gate")) {
            player.sendMessage("§cPermission refusée.");
            return true;
        }

        gateManager.setGateLocation(player.getLocation().clone());
        player.sendMessage("§aGate position saved at " + LocationUtil.formatLocation(gateManager.getGateLocation()));
        return true;
    }
}
