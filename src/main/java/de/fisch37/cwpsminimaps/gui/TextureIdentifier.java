package de.fisch37.cwpsminimaps.gui;

import de.fisch37.clientwps.data.AccessLevel;
import net.minecraft.util.Identifier;

public abstract class TextureIdentifier {
    public static Identifier fromAccess(AccessLevel level) {
        return Identifier.of("cwps", "waypoints/" + level.name);
    }
}
