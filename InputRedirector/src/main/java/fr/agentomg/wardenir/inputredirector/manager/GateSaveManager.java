package fr.agentomg.wardenir.inputredirector.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
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
     * Sauvegarde la position de la gate
     */
    public void saveGate() {
        try {
            Location gateLocation = gateManager.getGateLocation();

            if (gateLocation != null) {
                config.set("gate.world", gateLocation.getWorld().getName());
                config.set("gate.x", gateLocation.getX());
                config.set("gate.y", gateLocation.getY());
                config.set("gate.z", gateLocation.getZ());
                config.set("gate.yaw", gateLocation.getYaw());
                config.set("gate.pitch", gateLocation.getPitch());

                plugin.getLogger().info("Position de la gate sauvegardée: " +
                        gateLocation.getWorld().getName() + " " +
                        gateLocation.getBlockX() + " " +
                        gateLocation.getBlockY() + " " +
                        gateLocation.getBlockZ());
            } else {
                // Supprimer la gate si elle n'existe plus
                config.set("gate", null);
                plugin.getLogger().info("Gate supprimée de la sauvegarde");
            }

            config.save(dataFile);

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Erreur lors de la sauvegarde de la gate", e);
        }
    }

    /**
     * Charge la position de la gate
     */
    public void loadGate() {
        try {
            if (!config.contains("gate.world")) {
                plugin.getLogger().info("Aucune gate à charger");
                return;
            }

            String worldName = config.getString("gate.world");
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                plugin.getLogger().warning("Monde introuvable pour la gate: " + worldName);
                return;
            }

            double x = config.getDouble("gate.x");
            double y = config.getDouble("gate.y");
            double z = config.getDouble("gate.z");
            float yaw = (float) config.getDouble("gate.yaw");
            float pitch = (float) config.getDouble("gate.pitch");

            Location gateLocation = new Location(world, x, y, z, yaw, pitch);
            gateManager.setGateLocation(gateLocation);

            plugin.getLogger().info("Gate chargée: " +
                    worldName + " " +
                    gateLocation.getBlockX() + " " +
                    gateLocation.getBlockY() + " " +
                    gateLocation.getBlockZ());

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Erreur lors du chargement de la gate", e);
        }
    }
}