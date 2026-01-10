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

package de.outdev.pearlfix;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.outdev.pearlfix.commands.MainCommand;
import de.outdev.pearlfix.config.ConfigManager;
import de.outdev.pearlfix.config.Settings;
import de.outdev.pearlfix.listeners.impl.ProjectileHitListener;
import de.outdev.pearlfix.listeners.impl.ProjectileLaunchListener;
import de.outdev.pearlfix.utils.PearlData;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class PearlFix extends JavaPlugin {

    private static PearlFix instance;
    @Getter
    private ConfigManager configManager;
    @Getter
    private Cache<UUID, PearlData> pearlDataCache;

    public static @NotNull PearlFix getInstance() {
        return Objects.requireNonNull(instance, "Plugins is not initialized yet!");
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);

        if (!loadConfig()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        final Settings settings = configManager.getSettings();

        int expireSeconds = settings.getEnderPearls().getExpireAfterSeconds() < 1
            ? 1 // making sure this value isn't bellow 1
            : settings.getEnderPearls().getExpireAfterSeconds();

        pearlDataCache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(expireSeconds))
            .build();

        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        pearlDataCache.invalidateAll();
    }

    private void registerListeners() {
        new ProjectileLaunchListener(this);
        new ProjectileHitListener(this);
    }

    private void registerCommands() {
        PluginCommand pluginCommand = getCommand("pearlfix");
        if (pluginCommand == null) return;

        MainCommand command = new MainCommand(this);

        pluginCommand.setExecutor(command);
        pluginCommand.setTabCompleter(command);
    }

    private boolean loadConfig() {
        final Optional<Throwable> error = configManager.loadConfig();
        if (error.isPresent()) {
            getLogger().log(java.util.logging.Level.SEVERE, "Failed to load configuration!", error.get());
            return false;
        }
        return true;
    }
}
