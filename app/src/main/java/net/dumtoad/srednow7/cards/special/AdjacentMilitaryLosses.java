package net.dumtoad.srednow7.cards.special;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.player.Player;

public class AdjacentMilitaryLosses implements SpecialValue {

    @Override
    public int getSpecialValue(Player player) {
        int ret = 0;
        for (boolean direction : new boolean[]{true, false}) {
            ret += MainActivity.getMasterViewController().getTableController().getPlayerDirection(player, direction).getScore().getMilitaryLosses();
        }
        return ret;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
