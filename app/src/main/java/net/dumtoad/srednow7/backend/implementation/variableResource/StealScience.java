package net.dumtoad.srednow7.backend.implementation.variableResource;

import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;

public class StealScience implements VariableResource {

    //Copies one of the symbols on a green card in an adjacent city
    //In the case of multiple StealSciences, a card can only be copied once
    //A card that's played in both adjacent cities can't be copied twice

    @Override
    public ResQuant getResources(Player player) {
        return null;
    }
}
