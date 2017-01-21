package net.dumtoad.srednow7.backend.implementation.variableResource;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;
import net.dumtoad.srednow7.backend.implementation.ResQuantImpl;

public class StealScience implements ResourceStrategy {

    //Copies one of the symbols on a green card in an adjacent city
    //In the case of multiple StealSciences, a card can only be copied once
    //A card that's played in both adjacent cities could be copied twice

    @Override
    public ResQuant getResources(Game game, Player player) {
        ResQuant stolenScience = new ResQuantImpl();
        for (Game.Direction direction : Game.Direction.values()) {
            for (Card card : game.getPlayerDirection(player, direction).getPlayedCards()) {
                if (card.getProductionStyle() != ResourceStyle.STOLEN_SCIENCE) { //Can't steal what's already been stolen
                    for (Card.Resource science : new Card.Resource[]{Card.Resource.COMPASS, Card.Resource.TABLET, Card.Resource.GEAR}) {
                        stolenScience.add(science, card.getProducts(player).get(science));
                    }
                }
            }
        }

        return stolenScience;
    }

    @Override
    public ResQuant getResourcesNotSpecial() {
        return new ResQuantImpl();
    }

    @Override
    public ResourceStyle getStyle() {
        return ResourceStyle.STOLEN_SCIENCE;
    }

    @Override
    public boolean isSpecialIn(Card.Resource resource) {
        return resource == Card.Resource.COMPASS || resource == Card.Resource.TABLET || resource == Card.Resource.GEAR;
    }
}
