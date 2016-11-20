package net.dumtoad.srednow7.backend.implementation.specialValue;

import net.dumtoad.srednow7.backend.Player;

public class NotSoSpecial implements SpecialValue {

    @Override
    public int getSpecialValue(Player player) {
        return 0;
    }

    @Override
    public boolean isSpecial() {
        return false;
    }
}
