package fr.agentomg.wardenir.inputredirector.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

public class GateSaveManager {
    private final JavaPlugin plugin;
    private final GateManager gateManager;
    private final File dataFile;
    private FileConfiguration config;

    public GateSaveManager(JavaPlugin plugin, GateManager gateManager) {
        this.plugin = plugin;
        this.gateManager = gateManager;
        this.dataFile = new File(plugin.getDataFolder(), "gates.yml");
        loadConfig();
    }

    private void loadConfig() {
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Impossible de créer le fichier gates.yml", e);
            }
        }
        config = YamlConfiguration.loadConfiguration(dataFile);
    }

    /**
     * Sauvegarde toutes les gates
     */
    public void saveGates() {
        try {
            // Vider la configuration existante
            config.set("gates", null);

            Map<String, Location> gates = gateManager.getAllGates();

            if (gates.isEmpty()) {
                plugin.getLogger().info("Aucune gate à sauvegarder");
            } else {
                // Sauvegarder chaque gate
                for (Map.Entry<String, Location> entry : gates.entrySet()) {
                    String gateName = entry.getKey();
                    Location location = entry.getValue();

                    String basePath = "gates." + gateName;
                    config.set(basePath + ".world", location.getWorld().getName());
                    config.set(basePath + ".x", location.getX());
                    config.set(basePath + ".y", location.getY());
                    config.set(basePath + ".z", location.getZ());
                    config.set(basePath + ".yaw", location.getYaw());
                    config.set(basePath + ".pitch", location.getPitch());
                }

                plugin.getLogger().info("Sauvegardé " + gates.size() + " gate(s)");
            }

            config.save(dataFile);

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Erreur lors de la sauvegarde des gates", e);
        }
    }

    /**
     * Charge toutes les gates
     */
    public void loadGates() {
        try {
            // Vider les gates existantes
            gateManager.clearAllGates();

            if (!config.contains("gates")) {
                plugin.getLogger().info("Aucune gate à charger");
                return;
            }

            // Charger les gates depuis la configuration
            for (String gateName : config.getConfigurationSection("gates").getKeys(false)) {
                try {
                    String basePath = "gates." + gateName;

                    String worldName = config.getString(basePath + ".world");
                    World world = Bukkit.getWorld(worldName);

                    if (world == null) {
                        plugin.getLogger().warning("Monde introuvable pour la gate '" + gateName + "': " + worldName);
                        continue;
                    }

                    double x = config.getDouble(basePath + ".x");
                    double y = config.getDouble(basePath + ".y");
                    double z = config.getDouble(basePath + ".z");
                    float yaw = (float) config.getDouble(basePath + ".yaw");
                    float pitch = (float) config.getDouble(basePath + ".pitch");

                    Location location = new Location(world, x, y, z, yaw, pitch);
                    gateManager.addGate(gateName, location);

                    plugin.getLogger().info("Gate '" + gateName + "' chargée: " +
                            worldName + " " +
                            location.getBlockX() + " " +
                            location.getBlockY() + " " +
                            location.getBlockZ());

                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Erreur lors du chargement de la gate '" + gateName + "'", e);
                }
            }

            plugin.getLogger().info("Chargé " + gateManager.getGateCount() + " gate(s) au total");

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Erreur lors du chargement des gates", e);
        }
    }

    /**
     * Sauvegarde une gate spécifique
     * @param gateName Nom de la gate à sauvegarder
     */
    public void saveGate(String gateName) {
        try {
            Location location = gateManager.getGate(gateName);

            if (location == null) {
                // Supprimer la gate du fichier si elle n'existe plus
                config.set("gates." + gateName, null);
                plugin.getLogger().info("Gate '" + gateName + "' supprimée de la sauvegarde");
            } else {
                // Sauvegarder la gate
                String basePath = "gates." + gateName;
                config.set(basePath + ".world", location.getWorld().getName());
                config.set(basePath + ".x", location.getX());
                config.set(basePath + ".y", location.getY());
                config.set(basePath + ".z", location.getZ());
                config.set(basePath + ".yaw", location.getYaw());
                config.set(basePath + ".pitch", location.getPitch());

                plugin.getLogger().info("Gate '" + gateName + "' sauvegardée: " +
                        location.getWorld().getName() + " " +
                        location.getBlockX() + " " +
                        location.getBlockY() + " " +
                        location.getBlockZ());
            }

            config.save(dataFile);

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Erreur lors de la sauvegarde de la gate '" + gateName + "'", e);
        }
    }

    // === MÉTHODES DE COMPATIBILITÉ ===

    /**
     * @deprecated Utilisez saveGates() à la place
     */
    @Deprecated
    public void saveGate() {
        saveGate("default");
    }

    /**
     * @deprecated Utilisez loadGates() à la place
     */
    @Deprecated
    public void loadGate() {
        loadGates();
    }
}