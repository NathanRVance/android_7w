package net.dumtoad.srednow7.backend.implementation.variableResource;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;
import net.dumtoad.srednow7.backend.implementation.ResQuantImpl;
import net.dumtoad.srednow7.backend.implementation.specialValue.SpecialValue;

import java.util.HashMap;
import java.util.Map;

public class StandardResource implements ResourceStrategy {

    private ResQuant resources = new ResQuantImpl();
    private Map<Card.Resource, SpecialValue> special = new HashMap<>();

    public StandardResource setResource(Card.Resource resource, int amount) {
        resources.put(resource, amount);
        return this;
    }

    public StandardResource setResource(Card.Resource resource, SpecialValue amount) {
        special.put(resource, amount);
        return this;
    }

    @Override
    public ResQuant getResources(Game game, Player player) {
        ResQuant ret = new ResQuantImpl().addResources(resources);
        for(Card.Resource res : special.keySet()) {
            ret.put(res, special.get(res).getSpecialValue(game, player));
        }
        return ret;
    }

    @Override
    public boolean isSpecialIn(Card.Resource resource) {
        return special.containsKey(resource);
    }

    @Override
    public ResourceStyle getStyle() {
        return ResourceStyle.STANDARD;
    }
}
