package net.dumtoad.srednow7.backend.implementation.variableResource;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;
import net.dumtoad.srednow7.backend.implementation.ResQuantImpl;

public class StaticResource implements VariableResource {

    private ResQuant resources = new ResQuantImpl();

    public StaticResource setResource(Card.Resource resource, int amount) {
        resources.put(resource, amount);
        return this;
    }

    @Override
    public ResQuant getResources(Game game, Player player) {
        return new ResQuantImpl().addResources(resources);
    }

    @Override
    public ResourceStyle getStyle() {
        return ResourceStyle.STANDARD;
    }
}
