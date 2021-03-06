package com.plushnode.atlacore.util;

import com.plushnode.atlacore.platform.Location;
import com.plushnode.atlacore.platform.LocationWrapper;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public final class TypeUtil {
    private TypeUtil() {

    }

    public static Material adapt(com.plushnode.atlacore.platform.block.Material material) {
        return Material.values()[material.ordinal()];
    }

    public static com.plushnode.atlacore.platform.block.Material adapt(Material material) {
        return com.plushnode.atlacore.platform.block.Material.values()[material.ordinal()];
    }

    public static Vector adapt(Vector3D vec) {
        if (vec == null) return null;
        return new Vector(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Vector3D adapt(Vector vec) {
        if (vec == null) return null;
        return new Vector3D(vec.getX(), vec.getY(), vec.getZ());
    }

    public static org.bukkit.Location adapt(Location loc) {
        return ((LocationWrapper)loc).getBukkitLocation();
    }

    public static Location adapt(org.bukkit.Location loc) {
        return new LocationWrapper(loc);
    }
}
