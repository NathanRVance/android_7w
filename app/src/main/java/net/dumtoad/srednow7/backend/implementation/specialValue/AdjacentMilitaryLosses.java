package net.dumtoad.srednow7.backend.implementation.specialValue;

import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;

public class AdjacentMilitaryLosses implements SpecialValue {

    @Override
    public int getSpecialValue(Game game, Player player) {
        int ret = 0;
        for (Game.Direction direction : Game.Direction.values()) {
            ret += game.getPlayerDirection(player, direction).getScore().getMilitaryLosses();
        }
        return ret;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
