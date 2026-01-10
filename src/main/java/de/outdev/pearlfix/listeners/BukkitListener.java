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

package de.outdev.pearlfix.listeners;

import de.outdev.pearlfix.PearlFix;
import de.outdev.pearlfix.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * Generic base class for Bukkit {@link Listener}s to reduce boilerplate code.
 * <p>
 * Provides convenient access to the {@link PearlFix} plugin instance and its
 * {@link ConfigManager}. Automatically registers the listener instance upon
 * construction with Bukkit's {@link org.bukkit.plugin.PluginManager}.
 * </p>
 *
 * <p>
 * This class is intended to be extended by listener implementations.
 * </p>
 */
public abstract class BukkitListener implements Listener {

    protected final PearlFix plugin;
    protected final ConfigManager config;

    public BukkitListener(PearlFix plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
