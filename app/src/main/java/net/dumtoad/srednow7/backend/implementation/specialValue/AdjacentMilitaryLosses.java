package net.dumtoad.srednow7.backend.implementation.specialValue;

import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.bus.Bus;

public class AdjacentMilitaryLosses implements SpecialValue {

    @Override
    public int getSpecialValue(Player player) {
        int ret = 0;
        for (Game.Direction direction : Game.Direction.values()) {
            ret += Bus.bus.getGame().getPlayerDirection(player, direction).getScore().getMilitaryLosses();
        }
        return ret;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
