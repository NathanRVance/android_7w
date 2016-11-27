package net.dumtoad.srednow7.backend.implementation.variableResource;

import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;

public interface VariableResource {

    ResQuant getResources(Game game, Player player);

    ResourceStyle getStyle();

    enum ResourceStyle {
        STANDARD,
        STOLEN_SCIENCE
    }

}
