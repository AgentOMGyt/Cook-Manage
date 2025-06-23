package fr.agentomg.wardenir.inputredirector.command.impl;

import fr.agentomg.wardenir.inputredirector.manager.PigGuiManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SavePigsCommand implements CommandExecutor {
    private final PigGuiManager pigGuiManager;

    public SavePigsCommand(PigGuiManager pigGuiManager) {
        this.pigGuiManager = pigGuiManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("wardenir.savepigs")) {
            sender.sendMessage("§cPermission refusée.");
            return true;
        }

        sender.sendMessage("§6Sauvegarde des cochons en cours...");

        try {
            pigGuiManager.savePigs();
            sender.sendMessage("§aLes cochons ont été sauvegardés avec succès !");
        } catch (Exception e) {
            sender.sendMessage("§cErreur lors de la sauvegarde : " + e.getMessage());
        }

        return true;
    }
}