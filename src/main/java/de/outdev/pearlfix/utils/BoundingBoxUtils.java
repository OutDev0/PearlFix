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

    // A collection of relevant ratio constants.
    public static final double SNEAK_HEIGHT_RATIO = (1.5 / 1.8);
    public static final double SWIM_HEIGHT_RATIO = (0.6 / 1.8);
    public static final double BLOCK_HEIGHT_RATIO = (1 / 1.8);

    /**
     * Checks weather a provided {@link Location} is safe to be teleported to.
     *
     * @param location  {@link Location} that should be used to perform the check.
     * @param halfWidth The initial width of the {@link org.bukkit.util.BoundingBox} divided by two.
     * @param height    The height of the {@link org.bukkit.util.BoundingBox}.
     * @param debug     The {@link DebugHook} used for debug features.
     * @return          true if the checked location is safe.
     */
    public static boolean isSafe(@NotNull Location location, double halfWidth, double height, DebugHook debug) {
        World world = location.getWorld();
        if (world == null) return false;

        final BoundingBox box = getBoundingBoxAt(location, halfWidth, height);
        final boolean safe = !hasAnyBlockCollisions(box, world, debug);

        debug.onCollisionCheck(box, world, !safe);
        return safe;
    }

    /**
     * Checks weather a provided {@link BoundingBox} is overlapping with
     * any {@link BoundingBox} of a neighboring block.
     *
     * @param boundingBox The {@link BoundingBox} being checked for block collisions.
     * @param world       The {@link World} where the blocks are retrieved from.
     * @param debug       The {@link DebugHook} used for debug features.
     * @return            true if the {@link BoundingBox} overlaps with any blocks.
     */
    public static boolean hasAnyBlockCollisions(@NotNull BoundingBox boundingBox, @NotNull World world, DebugHook debug) {
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

                    final BoundingBox box = block.getBoundingBox();
                    final boolean overlaps = box.overlaps(boundingBox);

                    if (debug != DebugHook.NO_OP) {
                        drawBoundingBox(
                            box,
                            world,
                            overlaps ? Color.YELLOW : Color.WHITE
                        );
                    }

                    if (overlaps) return true;
                }
            }
        }

        return false;
    }

    /**
     * Create a {@link BoundingBox} at a given {@link Location}.
     *
     * @param location  The {@link Location} where the {@link BoundingBox} should be created at.
     * @param halfWidth The initial width of the {@link BoundingBox} divided by two.
     * @param height    The height of the {@link BoundingBox}.
     * @return          A new {@link BoundingBox} at the target {@link Location}.
     */
    public static BoundingBox getBoundingBoxAt(@NotNull Location location, double halfWidth, double height) {
        return new BoundingBox(
            location.getX() - halfWidth,
            location.getY(),
            location.getZ() - halfWidth,
            location.getX() + halfWidth,
            location.getY() + height,
            location.getZ() + halfWidth
        );
    }

    /**
     * Helper method for calling the {@link BoundingBoxUtils#drawBoundingBox(BoundingBox, World, Color)}
     * method multiple times in a provided interval and amount.
     *
     * @param plugin      The {@link PearlFix} instance.
     * @param boundingBox The {@link BoundingBox} that should be drawn.
     * @param world       The {@link World} in which the {@link BoundingBox} should be drawn.
     * @param color       The {@link Color} of the {@link Particle#DUST} particle.
     * @param amount      The amount of times the {@link BoundingBox} should be drawn.
     * @param interval    The interval in {@link TimeUnit#MILLISECONDS} the {@link BoundingBox} should be drawn.
     */
    public static void drawBoundingBoxTimes(PearlFix plugin, @NotNull BoundingBox boundingBox, @NotNull World world, Color color, int amount, long interval) {
        if (amount <= 0) return;

        AtomicInteger count = new AtomicInteger(0);
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task -> {
            if (count.getAndIncrement() > amount) {
                task.cancel();
            }

            drawBoundingBox(boundingBox, world, color);
        }, interval, interval, TimeUnit.MILLISECONDS);
    }

    /**
     * Debug method for visualizing the edges of a {@link BoundingBox}
     * using the {@link Particle#DUST} particle.
     *
     * @param boundingBox The {@link BoundingBox} that should be drawn.
     * @param world       The {@link World} in which the {@link BoundingBox} should be drawn.
     * @param color       The {@link Color} of the {@link Particle#DUST} particle.
     */
    public static void drawBoundingBox(BoundingBox boundingBox, World world, Color color) {
        // min
        double minX = boundingBox.getMinX();
        double minY = boundingBox.getMinY();
        double minZ = boundingBox.getMinZ();
        // max
        double maxX = boundingBox.getMaxX();
        double maxY = boundingBox.getMaxY();
        double maxZ = boundingBox.getMaxZ();

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
