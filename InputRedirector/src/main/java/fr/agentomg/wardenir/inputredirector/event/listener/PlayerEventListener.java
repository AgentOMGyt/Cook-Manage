package fr.agentomg.wardenir.inputredirector.event.listener;

import fr.agentomg.wardenir.inputredirector.InputRedirector;
import fr.agentomg.wardenir.inputredirector.manager.GateManager;
import fr.agentomg.wardenir.inputredirector.manager.MobManager;
import fr.agentomg.wardenir.inputredirector.manager.PigGuiManager;
import fr.agentomg.wardenir.inputredirector.manager.PlayerStateManager;
import fr.agentomg.wardenir.inputredirector.service.MovementService;
import fr.agentomg.wardenir.inputredirector.service.RedirectionService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

import java.util.UUID;

public class PlayerEventListener implements Listener {
    private final PlayerStateManager playerStateManager;
    private final MobManager mobManager;
    private final GateManager gateManager;
    private final PigGuiManager pigGuiManager;
    private final MovementService movementService;
    private final RedirectionService redirectionService;

    public PlayerEventListener(InputRedirector plugin, PlayerStateManager playerStateManager, MobManager mobManager,
                               GateManager gateManager, PigGuiManager pigGuiManager) {
        this.playerStateManager = playerStateManager;
        this.mobManager = mobManager;
        this.gateManager = gateManager;
        this.pigGuiManager = pigGuiManager;
        this.movementService = new MovementService(playerStateManager, mobManager);
        this.redirectionService = new RedirectionService(plugin, playerStateManager, mobManager);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Gestion du mouvement normal
        movementService.handlePlayerMove(event);

        // Gestion de l'entrée dans la gate
        handleGateEntry(event);
    }

    private void handleGateEntry(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (playerStateManager.isFrozen(playerId)) return;

        Location to = event.getTo();
        if (to == null) return;

        if (gateManager.isInGateArea(to) && gateManager.canTrigger(playerId)) {
            redirectionService.toggleRedirectionWithFeedback(player);
            gateManager.updateCooldown(playerId);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            player.sendMessage("§e[DEBUG] Interaction détectée.");

            pigGuiManager.handlePigGui(player, mobManager);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent e) {
        if (playerStateManager.isFrozen(e.getPlayer().getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
        if (playerStateManager.isFrozen(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().setSneaking(false);
        }
    }
}
