package net.dumtoad.srednow7.backend.implementation.variableResource;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;
import net.dumtoad.srednow7.backend.implementation.ResQuantImpl;

import static net.dumtoad.srednow7.backend.TradeBackend.tradeable;

public class SomethingAlreadyProduced implements ResourceStrategy {

    @Override
    public ResQuant getResources(Game game, Player player) {
        ResQuant ret = new ResQuantImpl();
        for(Card.Resource resource : tradeable) {
            for(Card card : player.getPlayedCards()) {
                if(card.getProductionStyle() != ResourceStyle.SOMETHING_ALREADY_PRODUCED
                        && card.getProducts(player).get(resource) > 0) {
                    ret.put(resource, 1);
                    break;
                }
            }
        }
        return ret;
    }

    @Override
    public ResourceStyle getStyle() {
        return ResourceStyle.SOMETHING_ALREADY_PRODUCED;
    }

    @Override
    public boolean isSpecialIn(Card.Resource resource) {
        return false;
    }
}
