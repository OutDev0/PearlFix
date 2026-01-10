/*
 * This file is part of PearlFix - https://github.com/OutDev0/PearlFix
 * Copyright © 2026 OutDev and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.outdev.pearlfix.utils;

import com.destroystokyo.paper.ParticleBuilder;
import de.outdev.pearlfix.PearlFix;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BoundingBoxUtils {

    public static final double SNEAK_HEIGHT_RATIO = (1.5 / 1.8);
    public static final double SWIM_HEIGHT_RATIO = (0.6 / 1.8);
    public static final double BLOCK_HEIGHT_RATIO = (1 / 1.8);

    /**
     * Checks weather a provided {@link Location} is safe to be teleported to.
     *
     * @param location  {@link Location} that should be used to perform the check.
     * @param halfWidth The initial width of the {@link org.bukkit.util.BoundingBox} divided by two.
     * @param height    The height of the {@link org.bukkit.util.BoundingBox}.
     * @return          true if the checked location is safe.
     */
    public static boolean isSafe(@NotNull Location location, double halfWidth, double height) {
        World world = location.getWorld();
        if (world == null) return false;

        return !hasAnyBlockCollisions(
            getBoundingBoxAt(location, halfWidth, height), world
        );
    }


    public static boolean hasAnyBlockCollisions(@NotNull BoundingBox boundingBox, @NotNull World world) {
        int minX = (int) Math.floor(boundingBox.getMinX());
        int maxX = (int) Math.floor(boundingBox.getMaxX());
        int minY = (int) Math.floor(boundingBox.getMinY());
        int maxY = (int) Math.floor(boundingBox.getMaxY());
        int minZ = (int) Math.floor(boundingBox.getMinZ());
        int maxZ = (int) Math.floor(boundingBox.getMaxZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (!block.isSolid()) continue;
                    if (block.getBoundingBox().overlaps(boundingBox)) return true;
                }
            }
        }

        return false;
    }

    public static BoundingBox getBoundingBoxAt(@NotNull Location loc, double halfWidth, double height) {
        return new BoundingBox(
            loc.getX() - halfWidth,
            loc.getY(),
            loc.getZ() - halfWidth,
            loc.getX() + halfWidth,
            loc.getY() + height,
            loc.getZ() + halfWidth
        );
    }

    public static void drawBoundingBoxTimes(PearlFix plugin, @NotNull BoundingBox box, @NotNull World world, Color color, int amount, long interval) {
        if (amount <= 0) return;

        AtomicInteger count = new AtomicInteger(0);
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task -> {
            if (count.getAndIncrement() > amount) {
                task.cancel();
            }

            drawBoundingBox(box, world, color);
        }, interval, interval, TimeUnit.MILLISECONDS);
    }

    public static void drawBoundingBox(BoundingBox box, World world, Color color) {
        // min
        double minX = box.getMinX();
        double minY = box.getMinY();
        double minZ = box.getMinZ();
        // max
        double maxX = box.getMaxX();
        double maxY = box.getMaxY();
        double maxZ = box.getMaxZ();

        final double step = 0.2;

        ParticleBuilder particle = new ParticleBuilder(Particle.DUST)
            .color(color)
            .count(1)
            .extra(0);

        // x edges
        for (double x = minX; x <= maxX; x += step) {
            particle.location(world, x, minY, minZ).spawn();
            particle.location(world, x, minY, maxZ).spawn();
            particle.location(world, x, maxY, minZ).spawn();
            particle.location(world, x, maxY, maxZ).spawn();
        }

        // y edges
        for (double y = minY; y <= maxY; y += step) {
            particle.location(world, minX, y, minZ).spawn();
            particle.location(world, maxX, y, minZ).spawn();
            particle.location(world, minX, y, maxZ).spawn();
            particle.location(world, maxX, y, maxZ).spawn();
        }

        // z edges
        for (double z = minZ; z <= maxZ; z += step) {
            particle.location(world, minX, minY, z).spawn();
            particle.location(world, maxX, minY, z).spawn();
            particle.location(world, minX, maxY, z).spawn();
            particle.location(world, maxX, maxY, z).spawn();
        }
    }
}
