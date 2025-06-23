package fr.agentomg.wardenir.inputredirector.util;

import org.bukkit.Location;

public class LocationUtil {

    public static String formatLocation(Location loc) {
        if (loc == null) return "null";
        return "x=" + loc.getBlockX() + " y=" + loc.getBlockY() + " z=" + loc.getBlockZ() + " in " + loc.getWorld().getName();
    }
}
