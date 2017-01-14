package net.dumtoad.srednow7.backend.implementation.variableResource;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;
import net.dumtoad.srednow7.backend.implementation.ResQuantImpl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static net.dumtoad.srednow7.backend.TradeBackend.tradeable;

public class SomethingNotProduced implements ResourceStrategy {

    @Override
    public ResQuant getResources(Game game, Player player) {
        Set<Card.Resource> products = new HashSet<>();
        Collections.addAll(products, tradeable);
        for(Card card : player.getPlayedCards()) {
            if(card.getProductionStyle() != ResourceStyle.SOMETHING_ALREADY_PRODUCED) {
                for(Card.Resource resource : Card.Resource.values()) {
                    if(card.getProducts(player).get(resource) > 0) {
                        products.remove(resource);
                    }
                }
            }
        }
        ResQuant ret = new ResQuantImpl();
        for(Card.Resource resource : products) {
            ret.put(resource, 1);
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
