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

package de.outdev.pearlfix.config;

import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import de.outdev.pearlfix.PearlFix;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Getter
public class ConfigManager {

    private final PearlFix plugin;
    private Settings settings;

    public ConfigManager(PearlFix plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public Optional<Throwable> loadConfig() {
        final YamlConfigurationProperties properties = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
            .charset(StandardCharsets.UTF_8)
            .outputNulls(true)
            .inputNulls(false)
            .build();
        final File settingsFile = new File(plugin.getDataFolder(), "config.yml");

        try {
            settings = YamlConfigurations.update(
                settingsFile.toPath(),
                Settings.class,
                properties
            );
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(e);
        }
    }

    public void reload() {
        final YamlConfigurationProperties properties = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
            .charset(StandardCharsets.UTF_8)
            .outputNulls(true)
            .inputNulls(false)
            .build();
        settings = YamlConfigurations.load(new File(plugin.getDataFolder(), "config.yml").toPath(), Settings.class, properties);
    }
}

