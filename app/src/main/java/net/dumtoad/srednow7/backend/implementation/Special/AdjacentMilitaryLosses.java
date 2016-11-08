package net.dumtoad.srednow7.backend.implementation.Special;

import net.dumtoad.srednow7.backend.Backend;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.bus.Bus;

public class AdjacentMilitaryLosses implements SpecialValue {

    @Override
    public int getSpecialValue(Player player) {
        int ret = 0;
        for (Backend.Direction direction : Backend.Direction.values()) {
            ret += Bus.bus.getBackend().getPlayerDirection(player, direction).getScore().getMilitaryLosses();
        }
        return ret;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
