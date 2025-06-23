package fr.agentomg.wardenir.inputredirector.command.impl;

import fr.agentomg.wardenir.inputredirector.manager.PigGuiManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class FourCommand implements CommandExecutor {
    private final PigGuiManager pigGuiManager;

    public FourCommand(PigGuiManager pigGuiManager) {
        this.pigGuiManager = pigGuiManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        Location loc = player.getLocation().clone();
        Pig pig = player.getWorld().spawn(loc, Pig.class);
        pig.setAI(false);
        pig.setInvulnerable(true);
        pig.setCustomName("FourPig");
        pig.setCustomNameVisible(false);

        Inventory pigGui = Bukkit.createInventory(null, 9, "Ma GUI du Four");
        pigGuiManager.getPigInventories().put(pig, pigGui);

        player.sendMessage("§aCochon spawné avec GUI dédiée.");
        return true;
    }
}
