package net.dumtoad.srednow7.backend.implementation.variableResource;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;

public interface ResourceStrategy {

    ResQuant getResources(Game game, Player player);

    ResQuant getResourcesNotSpecial();

    ResourceStyle getStyle();

    boolean isSpecialIn(Card.Resource resource);

    enum ResourceStyle {
        STANDARD,
        STOLEN_SCIENCE,
        SOMETHING_ALREADY_PRODUCED
    }

}
