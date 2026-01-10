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

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@Setter
@Getter
public final class Settings {

    @Comment("Enables debug mode. Advanced users only!")
    private final boolean debug = false;

    @Comment("Settings related to Ender Pearl behaviour.")
    private final EnderPearls enderPearls = new EnderPearls();

    @Configuration
    @Getter
    public static class EnderPearls {
        @Comment("""
            The interval (in ticks) at which bounding calculations should be ran.
            Modifying this value may drastically impact results. It's not recommended
            to be changed unless performance is affected.
            Recommended value: 1
            """
        )
        private final int checkTickInterval = 1;

        @Comment("""
            Sets the time after which Pearl data should expire. A higher expiry time
            may help with countering race conditions. Do not change if you don't know what you are doing.
            Recommended value: 10
            """
        )
        private final int expireAfterSeconds = 10;
    }

    @Comment("The version of the configuration file. DO NOT TOUCH!!!")
    private final int configVersion = 1;
}
