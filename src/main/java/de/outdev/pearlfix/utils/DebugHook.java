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

import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface DebugHook {
    void onCollisionCheck(@NotNull BoundingBox box, @NotNull World world, boolean collided);

    // default debug hook for no operations
    DebugHook NO_OP = (world, boundingBox, collided) -> {};
    // default debug hook for drawing a bounding box
    DebugHook DRAW = (box, world, collided) ->
        BoundingBoxUtils.drawBoundingBox(box, world, collided ? Color.RED : Color.LIME);

    static DebugHook getDebugHook(boolean debug) {
        return debug ? DRAW : NO_OP;
    }
}
