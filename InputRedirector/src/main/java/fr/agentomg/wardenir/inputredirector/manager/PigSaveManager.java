package fr.agentomg.wardenir.inputredirector.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class PigSaveManager {
    private final JavaPlugin plugin;
    private final PigGuiManager pigGuiManager;
    private final File dataFile;
    private FileConfiguration config;
    private final Set<UUID> managedPigIds = new HashSet<>(); // Pour tracker les cochons du plugin

    public PigSaveManager(JavaPlugin plugin, PigGuiManager pigGuiManager) {
        this.plugin = plugin;
        this.pigGuiManager = pigGuiManager;
        this.dataFile = new File(plugin.getDataFolder(), "pigs.yml");
        loadConfig();
    }

    private void loadConfig() {
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Impossible de créer le fichier pigs.yml", e);
            }
        }
        config = YamlConfiguration.loadConfiguration(dataFile);
    }

    /**
     * Sauvegarde tous les cochons et leurs inventaires
     */
    public void savePigs() {
        try {
            // Nettoyer la configuration existante
            config.set("pigs", null);
            config.set("managedPigIds", null);

            List<Map<String, Object>> pigDataList = new ArrayList<>();
            List<String> pigIdList = new ArrayList<>();

            for (Map.Entry<Pig, Inventory> entry : pigGuiManager.getPigInventories().entrySet()) {
                Pig pig = entry.getKey();
                Inventory inventory = entry.getValue();

                // Vérifier que le cochon est encore valide
                if (!pig.isValid()) {
                    continue;
                }

                Map<String, Object> pigData = new HashMap<>();

                // Sauvegarder les données du cochon
                Location loc = pig.getLocation();
                pigData.put("world", loc.getWorld().getName());
                pigData.put("x", loc.getX());
                pigData.put("y", loc.getY());
                pigData.put("z", loc.getZ());
                pigData.put("yaw", loc.getYaw());
                pigData.put("pitch", loc.getPitch());
                pigData.put("uuid", pig.getUniqueId().toString());

                // Propriétés du cochon
                if (pig.getCustomName() != null) {
                    pigData.put("customName", pig.getCustomName());
                }
                pigData.put("customNameVisible", pig.isCustomNameVisible());
                pigData.put("hasAI", pig.hasAI());
                pigData.put("invulnerable", pig.isInvulnerable());

                // Sauvegarder l'inventaire
                pigData.put("inventoryTitle", inventory.getType().getDefaultTitle());
                if (inventory.getSize() > 0) {
                    pigData.put("inventorySize", inventory.getSize());

                    List<Map<String, Object>> items = new ArrayList<>();
                    for (int i = 0; i < inventory.getSize(); i++) {
                        ItemStack item = inventory.getItem(i);
                        if (item != null && item.getType() != Material.AIR) {
                            Map<String, Object> itemData = new HashMap<>();
                            itemData.put("slot", i);
                            itemData.put("item", item.serialize());
                            items.add(itemData);
                        }
                    }
                    pigData.put("items", items);
                }

                pigDataList.add(pigData);
                pigIdList.add(pig.getUniqueId().toString());
            }

            config.set("pigs", pigDataList);
            config.set("managedPigIds", pigIdList);
            config.save(dataFile);

            plugin.getLogger().info("Sauvegardé " + pigDataList.size() + " cochons avec leurs GUIs");

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Erreur lors de la sauvegarde des cochons", e);
        }
    }

    /**
     * Charge tous les cochons et leurs inventaires
     */
    public void loadPigs() {
        try {
            // IMPORTANT: Supprimer d'abord tous les cochons existants du plugin
            removeExistingManagedPigs();

            if (!config.contains("pigs")) {
                plugin.getLogger().info("Aucun cochon à charger");
                return;
            }

            List<Map<?, ?>> pigDataList = config.getMapList("pigs");
            int loadedCount = 0;

            // Charger les IDs des cochons précédemment gérés
            List<String> savedPigIds = config.getStringList("managedPigIds");
            managedPigIds.clear();
            for (String idString : savedPigIds) {
                try {
                    managedPigIds.add(UUID.fromString(idString));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("UUID invalide trouvé dans la sauvegarde: " + idString);
                }
            }

            for (Map<?, ?> pigDataMap : pigDataList) {
                try {
                    if (loadPig(pigDataMap)) {
                        loadedCount++;
                    }
                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Erreur lors du chargement d'un cochon", e);
                }
            }

            plugin.getLogger().info("Chargé " + loadedCount + " cochons avec leurs GUIs");

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Erreur lors du chargement des cochons", e);
        }
    }

    /**
     * Supprime tous les cochons existants qui étaient gérés par le plugin
     */
    private void removeExistingManagedPigs() {
        int removedCount = 0;

        // Méthode 1: Supprimer les cochons actuellement dans la Map
        for (Pig pig : new ArrayList<>(pigGuiManager.getPigInventories().keySet())) {
            if (pig.isValid()) {
                pig.remove();
                removedCount++;
            }
        }
        pigGuiManager.getPigInventories().clear();

        // Méthode 2: Supprimer les cochons basés sur les UUIDs sauvegardés
        if (config.contains("managedPigIds")) {
            List<String> savedPigIds = config.getStringList("managedPigIds");
            for (String idString : savedPigIds) {
                try {
                    UUID pigId = UUID.fromString(idString);
                    // Chercher dans tous les mondes
                    for (World world : Bukkit.getWorlds()) {
                        for (Entity entity : world.getEntities()) {
                            if (entity.getType() == EntityType.PIG && entity.getUniqueId().equals(pigId)) {
                                entity.remove();
                                removedCount++;
                                break;
                            }
                        }
                    }
                } catch (IllegalArgumentException e) {
                    // UUID invalide, ignorer
                }
            }
        }

        if (removedCount > 0) {
            plugin.getLogger().info("Supprimé " + removedCount + " anciens cochons du plugin");
        }
    }

    @SuppressWarnings("unchecked")
    private boolean loadPig(Map<?, ?> pigData) {
        try {
            // Récupérer la localisation
            String worldName = (String) pigData.get("world");
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                plugin.getLogger().warning("Monde introuvable: " + worldName);
                return false;
            }

            double x = ((Number) pigData.get("x")).doubleValue();
            double y = ((Number) pigData.get("y")).doubleValue();
            double z = ((Number) pigData.get("z")).doubleValue();
            float yaw = ((Number) pigData.get("yaw")).floatValue();
            float pitch = ((Number) pigData.get("pitch")).floatValue();

            Location location = new Location(world, x, y, z, yaw, pitch);

            // Spawner le cochon
            Pig pig = world.spawn(location, Pig.class);

            // Ajouter l'ID du cochon à notre tracker
            managedPigIds.add(pig.getUniqueId());

            // Appliquer les propriétés
            if (pigData.containsKey("customName")) {
                pig.setCustomName((String) pigData.get("customName"));
            }
            if (pigData.containsKey("customNameVisible")) {
                pig.setCustomNameVisible((Boolean) pigData.get("customNameVisible"));
            }
            if (pigData.containsKey("hasAI")) {
                pig.setAI((Boolean) pigData.get("hasAI"));
            }
            if (pigData.containsKey("invulnerable")) {
                pig.setInvulnerable((Boolean) pigData.get("invulnerable"));
            }

            // Créer l'inventaire
            int inventorySize = pigData.containsKey("inventorySize") ?
                    ((Number) pigData.get("inventorySize")).intValue() : 9;
            String title = pigData.containsKey("inventoryTitle") ?
                    (String) pigData.get("inventoryTitle") : "Ma GUI du Four";

            Inventory inventory = Bukkit.createInventory(null, inventorySize, title);

            // Charger les items
            if (pigData.containsKey("items")) {
                List<Map<?, ?>> items = (List<Map<?, ?>>) pigData.get("items");
                for (Map<?, ?> itemData : items) {
                    try {
                        int slot = ((Number) itemData.get("slot")).intValue();
                        Map<String, Object> itemMap = (Map<String, Object>) itemData.get("item");
                        ItemStack item = ItemStack.deserialize(itemMap);

                        if (slot >= 0 && slot < inventory.getSize()) {
                            inventory.setItem(slot, item);
                        }
                    } catch (Exception e) {
                        plugin.getLogger().log(Level.WARNING, "Erreur lors du chargement d'un item", e);
                    }
                }
            }

            // Enregistrer dans le PigGuiManager
            pigGuiManager.getPigInventories().put(pig, inventory);

            return true;

        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Erreur lors du chargement des données du cochon", e);
            return false;
        }
    }

    /**
     * Nettoie les cochons invalides de la sauvegarde
     */
    public void cleanupInvalidPigs() {
        pigGuiManager.getPigInventories().entrySet().removeIf(entry -> !entry.getKey().isValid());
    }

    /**
     * Ajoute un cochon à la liste des cochons gérés
     */
    public void addManagedPig(Pig pig) {
        managedPigIds.add(pig.getUniqueId());
    }

    /**
     * Retire un cochon de la liste des cochons gérés
     */
    public void removeManagedPig(Pig pig) {
        managedPigIds.remove(pig.getUniqueId());
    }
}