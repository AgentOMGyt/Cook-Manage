package fr.agentomg.wardenir.inputredirector.util;

import org.bukkit.Location;

public class MobUtil {

    public static boolean isPathBlocked(Location loc) {
        return loc.getBlock().getType().isSolid() ||
                loc.clone().add(0, 1, 0).getBlock().getType().isSolid() ||
                !loc.clone().add(0, -1, 0).getBlock().getType().isSolid();
    }

    public static float lerpAngle(float start, float end, float t) {
        float delta = end - start;
        while (delta < -180) delta += 360;
        while (delta > 180) delta -= 360;
        return start + delta * t;
    }
}
