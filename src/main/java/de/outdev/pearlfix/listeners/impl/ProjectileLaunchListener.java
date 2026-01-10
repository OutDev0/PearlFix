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

package de.outdev.pearlfix.listeners.impl;

import com.google.common.cache.Cache;
import de.outdev.pearlfix.PearlFix;
import de.outdev.pearlfix.listeners.BukkitListener;
import de.outdev.pearlfix.utils.BoundingBoxUtils;
import de.outdev.pearlfix.utils.PearlData;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.UUID;

public class ProjectileLaunchListener extends BukkitListener {

    private final Cache<UUID, PearlData> pearlCache;

    public ProjectileLaunchListener(PearlFix plugin) {
        super(plugin);
        this.pearlCache = plugin.getPearlDataCache();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl pearl)) return;
        if (!(pearl.getShooter() instanceof Player player)) return;

        final UUID uuid = pearl.getUniqueId();

        // The throw location of the player acts as the fallback location
        final Location origin = player.getLocation().clone();
        pearlCache.put(
            uuid,
            new PearlData(origin, origin)
        );

        // pre-calculating the width and height here once to avoid unnecessary calculations
        final double halfWidth = player.getWidth() / 2;
        final double height = player.getHeight() * BoundingBoxUtils.SWIM_HEIGHT_RATIO;

        // get the configured tick interval
        final int tickInterval = config.getSettings().getEnderPearls().getCheckTickInterval();

        // using the pearl's scheduler, automatically destroys with the entity
        pearl.getScheduler().runAtFixedRate(plugin, task -> {
            final Location currentLocation = pearl.getLocation();

            // only update pearl data if the current location is considered safe
            if (BoundingBoxUtils.isSafe(currentLocation, halfWidth, height)) {
                pearlCache.put(
                    uuid,
                    new PearlData(origin, currentLocation.clone())
                );
            }
        }, null, tickInterval, tickInterval);
    }
}
