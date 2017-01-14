package net.dumtoad.srednow7.backend.implementation.specialValue;

import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;

public class MilitaryVictories implements SpecialValue {

    private int amount;
    private boolean includeAdjacent;
    private boolean inlcudeOwn;

    public MilitaryVictories(int amount, boolean includeAdjacent, boolean includeOwn) {
        this.amount = amount;
        this.includeAdjacent = includeAdjacent;
        this.inlcudeOwn = includeOwn;
    }

    @Override
    public int getSpecialValue(Game game, Player player) {
        int ret = 0;
        if(includeAdjacent) {
            for (Game.Direction direction : Game.Direction.values()) {
                ret += getVictories(game.getPlayerDirection(player, direction)) * amount;
            }
        }
        if(inlcudeOwn) {
            ret += getVictories(player) * amount;
        }
        return ret;
    }

    private int getVictories(Player player) {
        int[] victories = player.getScore().getMilitaryVictories();
        int ret = 0;
        for (int victory : victories) {
            ret += victory;
        }
        return ret;
    }
}
