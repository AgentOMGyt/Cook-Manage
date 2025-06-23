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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

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
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Si le joueur est en mode redirection, gérer les interactions spéciales
        if (playerStateManager.isFrozen(playerId)) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                player.sendMessage("§e[DEBUG] Interaction détectée en mode redirection.");
                pigGuiManager.handlePigGui(player, mobManager);
            }
            // Annuler toutes les interactions en mode redirection
            event.setCancelled(true);
        }
        // En mode normal, laisser les interactions se faire normalement (ne pas annuler)
    }

    // === RESTRICTIONS EN MODE REDIRECTION ===

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Empêcher de poser des blocs en mode redirection
        if (playerStateManager.isFrozen(playerId)) {
            event.setCancelled(true);
            player.sendMessage("§cVous ne pouvez pas poser de blocs en mode redirection !");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Empêcher de casser des blocs en mode redirection
        if (playerStateManager.isFrozen(playerId)) {
            event.setCancelled(true);
            player.sendMessage("§cVous ne pouvez pas casser de blocs en mode redirection !");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        UUID playerId = player.getUniqueId();

        // Empêcher les clics d'inventaire en mode redirection (sauf les GUIs du plugin)
        if (playerStateManager.isFrozen(playerId)) {
            // Vérifier si c'est un GUI du plugin (cochon)
            boolean isPigGui = pigGuiManager.getPigInventories().values().contains(event.getInventory());

            if (!isPigGui) {
                event.setCancelled(true);
                player.sendMessage("§cVous ne pouvez pas utiliser votre inventaire en mode redirection !");
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Empêcher de jeter des objets en mode redirection
        if (playerStateManager.isFrozen(playerId)) {
            event.setCancelled(true);
            player.sendMessage("§cVous ne pouvez pas jeter d'objets en mode redirection !");
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Empêcher de ramasser des objets en mode redirection
        if (playerStateManager.isFrozen(playerId)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        UUID playerId = player.getUniqueId();

        // Empêcher les dégâts en mode redirection
        if (playerStateManager.isFrozen(playerId)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Empêcher le chat en mode redirection (optionnel)
        if (playerStateManager.isFrozen(playerId)) {
            event.setCancelled(true);
            player.sendMessage("§cVous ne pouvez pas parler en mode redirection !");
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Empêcher la plupart des commandes en mode redirection
        if (playerStateManager.isFrozen(playerId)) {
            String command = event.getMessage().toLowerCase();

            // Autoriser certaines commandes de base
            if (!command.startsWith("/help") &&
                    !command.startsWith("/list") &&
                    !command.startsWith("/who") &&
                    !command.startsWith("/inputredirector")) { // Autoriser les commandes du plugin

                event.setCancelled(true);
                player.sendMessage("§cVous ne pouvez pas utiliser cette commande en mode redirection !");
            }
        }
    }

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (playerStateManager.isFrozen(playerId)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (playerStateManager.isFrozen(playerId)) {
            event.setCancelled(true);
            player.setSneaking(false);
        }
    }
}
