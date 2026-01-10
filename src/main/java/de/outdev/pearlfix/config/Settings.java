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
import de.outdev.pearlfix.utils.CheckMode;
import lombok.Getter;
import lombok.Setter;

@Configuration
@Setter
@Getter
public final class Settings {

    @Comment("""
        PearlFix by OutDev – https://github.com/OutDev0/PearlFix
        
        Enables debug mode. For advanced users only!""")
    private boolean debug = false;

    @Comment("\nSettings related to Ender Pearl behavior.")
    private EnderPearls enderPearls = new EnderPearls();

    @Configuration
    @Getter
    public static class EnderPearls {
        @Comment("\nEnables or disables the Ender Pearl patch.")
        private boolean enabled = true;

        @Comment("""

            The mode that should be used for checking bounding boxes.
            Modifies the height-ratio multiplier for the checked bounding box.
            Modes:
                FULL – Checks the entire body of the player.
                PARTIAL – Checks the sneak height of the player.
                HALF – Checks one full block.
                NORMAL – Recommended size."""
        )
        private CheckMode mode = CheckMode.NORMAL;

        @Comment("""

            The interval (in ticks) at which bounding calculations should be run.
            Modifying this value may drastically affect results. It is not recommended
            to change this unless performance issues occur.
            Recommended value: 1
            Minimum value: 1"""
        )
        private int checkTickInterval = 1;

        @Comment("""

            Sets the time after which Pearl data should expire. A higher expiry time
            may help counter race conditions. Do not change this unless you know
            what you are doing.
            Recommended value: 10
            Minimum value: 1"""
        )
        private int expireAfterSeconds = 10;
    }

    @Comment("\nThe version of the configuration file. DO NOT TOUCH!!!")
    private int configVersion = 1;
}
