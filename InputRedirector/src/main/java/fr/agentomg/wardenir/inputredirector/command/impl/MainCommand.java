package fr.agentomg.wardenir.inputredirector.command.impl;

import fr.agentomg.wardenir.inputredirector.InputRedirector;
import fr.agentomg.wardenir.inputredirector.manager.MobManager;
import fr.agentomg.wardenir.inputredirector.manager.PlayerStateManager;
import fr.agentomg.wardenir.inputredirector.service.RedirectionService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {
    private final RedirectionService redirectionService;

    public MainCommand(InputRedirector plugin, PlayerStateManager playerStateManager, MobManager mobManager) {
        this.redirectionService = new RedirectionService(plugin, playerStateManager, mobManager);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        redirectionService.toggleRedirectionWithFeedback(player);
        return true;
    }
}
