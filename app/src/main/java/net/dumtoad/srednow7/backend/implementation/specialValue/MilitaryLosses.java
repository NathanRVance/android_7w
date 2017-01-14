package net.dumtoad.srednow7.backend.implementation.specialValue;

import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;

public class MilitaryLosses implements SpecialValue {

    private int amount;
    private boolean includeAdjacent;
    private boolean inlcudeOwn;

    public MilitaryLosses(int amount, boolean includeAdjacent, boolean includeOwn) {
        this.amount = amount;
        this.includeAdjacent = includeAdjacent;
        this.inlcudeOwn = includeOwn;
    }

    @Override
    public int getSpecialValue(Game game, Player player) {
        int ret = 0;
        if(includeAdjacent) {
            for (Game.Direction direction : Game.Direction.values()) {
                ret += game.getPlayerDirection(player, direction).getScore().getMilitaryLosses() * amount;
            }
        }
        if(inlcudeOwn) {
            ret += player.getScore().getMilitaryLosses() * amount;
        }
        return ret;
    }

}
