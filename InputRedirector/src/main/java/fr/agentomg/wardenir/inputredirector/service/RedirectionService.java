package fr.agentomg.wardenir.inputredirector.service;

import fr.agentomg.wardenir.inputredirector.InputRedirector;
import fr.agentomg.wardenir.inputredirector.manager.MobManager;
import fr.agentomg.wardenir.inputredirector.manager.PlayerStateManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class RedirectionService {
    private final InputRedirector plugin;
    private final PlayerStateManager playerStateManager;
    private final MobManager mobManager;

    public RedirectionService(InputRedirector plugin, PlayerStateManager playerStateManager, MobManager mobManager) {
        this.plugin = plugin;
        this.playerStateManager = playerStateManager;
        this.mobManager = mobManager;
    }

    public void toggleRedirectionWithFeedback(Player player) {
        UUID playerId = player.getUniqueId();

        player.sendTitle("-", "", 10, 1, 10);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.getInventory().clear();

            if (playerStateManager.isFrozen(playerId)) {
                stopRedirection(player);
            } else {
                startRedirection(player);
            }
        }, 10L);
    }

    private void startRedirection(Player player) {
        UUID playerId = player.getUniqueId();

        mobManager.startRedirection(player);
        playerStateManager.freeze(playerId);

        player.getInventory().setItem(0, new ItemStack(Material.POTATO));
        player.getInventory().setItem(1, new ItemStack(Material.GOLDEN_APPLE));

        player.playSound(player.getLocation(), "hideaway:ui.dialogue.zoom_in", SoundCategory.AMBIENT, 5f, 1f);
        player.playSound(player.getLocation(), "hideaway:activities.event_jazzyjingle1", SoundCategory.AMBIENT, 5f, 1f);
        player.stopSound("hideaway:activities.treasure_diving_music_loop", SoundCategory.AMBIENT);
    }

    private void stopRedirection(Player player) {
        UUID playerId = player.getUniqueId();

        mobManager.stopRedirection(player, playerStateManager);

        player.playSound(player.getLocation(), "hideaway:ui.dialogue.zoom_out", SoundCategory.AMBIENT, 5f, 1f);
        player.stopSound("hideaway:activities.event_jazzyjingle1", SoundCategory.AMBIENT);
        player.playSound(player.getLocation(), "hideaway:activities.treasure_diving_music_loop", SoundCategory.AMBIENT, 2f, 1f);
    }
}
