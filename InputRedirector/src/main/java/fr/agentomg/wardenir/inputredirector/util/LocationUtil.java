package fr.agentomg.wardenir.inputredirector.util;

import org.bukkit.Location;

public class LocationUtil {

    /**
     * Formate une location en string lisible
     * @param location La location à formater
     * @return String formatée de la location
     */
    public static String formatLocation(Location location) {
        if (location == null) {
            return "§cLocation invalide";
        }

        return String.format("§e%s §7(§f%d, %d, %d§7)",
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
    }

    /**
     * Formate une location avec les coordonnées précises (double)
     * @param location La location à formater
     * @return String formatée de la location avec décimales
     */
    public static String formatLocationPrecise(Location location) {
        if (location == null) {
            return "§cLocation invalide";
        }

        return String.format("§e%s §7(§f%.2f, %.2f, %.2f§7)",
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ());
    }

    /**
     * Formate une location avec rotation
     * @param location La location à formater
     * @return String formatée avec position et rotation
     */
    public static String formatLocationWithRotation(Location location) {
        if (location == null) {
            return "§cLocation invalide";
        }

        return String.format("§e%s §7(§f%.2f, %.2f, %.2f§7) §8[§7yaw: §f%.1f°§7, pitch: §f%.1f°§8]",
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());
    }
}
