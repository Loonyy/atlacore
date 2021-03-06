package com.plushnode.atlacore.collision;

import com.plushnode.atlacore.collision.geometry.AABB;
import com.plushnode.atlacore.collision.geometry.Ray;
import com.plushnode.atlacore.platform.*;
import com.plushnode.atlacore.platform.block.Block;
import com.plushnode.atlacore.platform.block.Material;
import com.plushnode.atlacore.util.VectorUtil;
import com.plushnode.atlacore.util.WorldUtil;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.Collections;
import java.util.Optional;

public final class CollisionUtil {
    private CollisionUtil() {

    }

    // Checks a collider to see if it's hitting any entities near it.
    // Calls the CollisionCallback when hitting a target.
    // Returns true if it hits a target.
    public static boolean handleEntityCollisions(User user, Collider collider, CollisionCallback callback, boolean livingOnly) {
        return handleEntityCollisions(user, collider, callback, livingOnly, false);
    }

    // Checks a collider to see if it's hitting any entities near it.
    // Calls the CollisionCallback when hitting a target.
    // Returns true if it hits a target.
    public static boolean handleEntityCollisions(User user, Collider collider, CollisionCallback callback, boolean livingOnly, boolean selfCollision) {
        // This is used to increase the lookup volume for nearby entities.
        // Entity locations can be out of the collider volume while still intersecting.
        final double ExtentBuffer = 4.0;

        // Create the extent vector to use as size of bounding box to find nearby entities.
        Vector3D extent = collider.getHalfExtents().add(new Vector3D(ExtentBuffer, ExtentBuffer, ExtentBuffer));
        Vector3D pos = collider.getPosition();
        Location location = user.getLocation().setX(pos.getX()).setY(pos.getY()).setZ(pos.getZ());

        boolean hit = false;

        for (Entity entity : location.getWorld().getNearbyEntities(location, extent.getX(), extent.getY(), extent.getZ())) {
            if (!selfCollision && entity.equals(user)) continue;

            if (entity instanceof Player && ((Player) entity).getGameMode() == GameMode.SPECTATOR) {
                continue;
            }

            if (livingOnly && !(entity instanceof LivingEntity)) {
                continue;
            }

            AABB entityBounds = entity.getBounds().at(entity.getLocation());
            if (collider.intersects(entityBounds)) {
                if (callback.onCollision(entity)) {
                    return true;
                }

                hit = true;
            }
        }

        return hit;
    }

    public static boolean handleBlockCollisions(World world, Collider collider, Location begin, Location end, boolean liquids) {
        double maxExtent = VectorUtil.getMaxComponent(collider.getHalfExtents());
        double distance = begin.distance(end);

        Vector3D toEnd = end.subtract(begin).toVector().normalize();
        Ray ray = new Ray(begin.toVector(), toEnd);

        Location mid = begin.add(toEnd.scalarMultiply(distance / 2.0));
        double lookupRadius = (distance / 2.0) + maxExtent + 1.0;

        for (Block block : WorldUtil.getNearbyBlocks(mid, lookupRadius, Collections.singletonList(Material.AIR))) {
            AABB localBounds = block.getBounds();

            if (liquids && block.isLiquid()) {
                localBounds = AABB.BLOCK_BOUNDS;
            }

            AABB blockBounds = localBounds.at(block.getLocation());

            Optional<Double> result = blockBounds.intersects(ray);
            if (result.isPresent()) {
                double d = result.get();
                if (d < distance) {
                    return true;
                }
            }
        }

        return false;
    }
}
