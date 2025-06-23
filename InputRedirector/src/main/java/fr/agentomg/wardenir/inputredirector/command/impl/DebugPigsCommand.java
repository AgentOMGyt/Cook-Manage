package fr.agentomg.wardenir.inputredirector.command.impl;

import fr.agentomg.wardenir.inputredirector.manager.PigGuiManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;

public class DebugPigsCommand implements CommandExecutor {
    private final PigGuiManager pigGuiManager;

    public DebugPigsCommand(PigGuiManager pigGuiManager) {
        this.pigGuiManager = pigGuiManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        player.sendMessage("§6[DEBUG] Cochons enregistrés : " + pigGuiManager.getPigInventories().size());
        for (Pig pig : pigGuiManager.getPigInventories().keySet()) {
            player.sendMessage("§7- " + pig.getUniqueId() + " | Valid: " + pig.isValid() + " | Dist: " + pig.getLocation().distance(player.getLocation()));
        }
        return true;
    }
}
