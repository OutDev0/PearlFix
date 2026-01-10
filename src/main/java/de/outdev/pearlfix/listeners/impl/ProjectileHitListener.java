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
import org.bukkit.Sound;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.UUID;

public class ProjectileHitListener extends BukkitListener {

    // vanilla damage applied after teleporting with an EnderPearl
    private static final double PEARL_DAMAGE = 5.0;

    private final Cache<UUID, PearlData> pearlCache;

    public ProjectileHitListener(PearlFix plugin) {
        super(plugin);
        this.pearlCache = plugin.getPearlDataCache();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof EnderPearl pearl)) return;
        if (!(pearl.getShooter() instanceof Player player)) return;

        final PearlData data = pearlCache.getIfPresent(pearl.getUniqueId());
        if (data == null) return; // should not happen

        // pre-calculating the width and height here once to avoid unnecessary calculations
        final double halfWidth = player.getWidth() / 2;
        final double height = player.getHeight() * BoundingBoxUtils.SWIM_HEIGHT_RATIO;

        // opt out if the landing location is safe
        if (BoundingBoxUtils.isSafe(pearl.getLocation(), halfWidth, height)) {
            return;
        }

        final Location newLocation = data.lastSafe();
        newLocation.setYaw(player.getYaw());
        newLocation.setPitch(player.getPitch());

        // removing the pearl and clearing the cache
        pearl.remove();
        pearlCache.invalidate(pearl.getUniqueId());

        player.teleportAsync(newLocation).thenAccept( teleport -> {
            if (!teleport) return; // teleport failed

            // apply artificial pearl effects after the teleport
            player.damage(PEARL_DAMAGE, pearl);
            newLocation.getWorld().playSound(newLocation, Sound.ENTITY_PLAYER_TELEPORT, 1F, 1F);
        });
    }
}
