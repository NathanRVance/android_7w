package net.dumtoad.srednow7.cards.special;

import net.dumtoad.srednow7.player.Player;

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
