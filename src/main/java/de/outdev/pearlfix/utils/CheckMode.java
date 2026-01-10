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

import lombok.Getter;

public enum CheckMode {
    FULL(1.0),
    PARTIAL(BoundingBoxUtils.SNEAK_HEIGHT_RATIO),
    HALF(BoundingBoxUtils.BLOCK_HEIGHT_RATIO),
    NORMAL(BoundingBoxUtils.SWIM_HEIGHT_RATIO);

    @Getter
    private final double multiplier;

    CheckMode(double multiplier) {
        this.multiplier = multiplier;
    }
}
